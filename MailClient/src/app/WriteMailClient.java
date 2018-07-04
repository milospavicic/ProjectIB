package app;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.ParseException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.mail.internet.MimeMessage;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.xml.security.encryption.EncryptedData;
import org.apache.xml.security.encryption.EncryptedKey;
import org.apache.xml.security.encryption.XMLCipher;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.keys.KeyInfo;
import org.apache.xml.security.signature.XMLSignature;
import org.apache.xml.security.signature.XMLSignatureException;
import org.apache.xml.security.transforms.TransformationException;
import org.apache.xml.security.transforms.Transforms;
import org.apache.xml.security.utils.Constants;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.google.api.services.gmail.Gmail;

import keystore.KeyStoreReader;
import support.MailHelper;
import support.MailWritter;
import util.Base64;
import util.GzipUtil;
import util.IVHelper;

public class WriteMailClient extends MailClient {

	// private static final String KEY_FILE = "./data/session.key";
	private static final String KEY_STORE_USERA_FILE = "./data/usera.jks";
	private static final String KEY_STORE_USERB_FILE = "./data/usera.jks";

	static {
		// staticka inicijalizacija
		Security.addProvider(new BouncyCastleProvider());
		org.apache.xml.security.Init.init();
	}

	public static void main(String[] args) {

		try {
			Gmail service = getGmailService();

			// Unos podataka
			System.out.println("Insert a reciever:");
			BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
			String reciever = reader.readLine();

			System.out.println("Insert a subject:");
			String subject = reader.readLine();

			System.out.println("Insert body:");
			String body = reader.readLine();

			// kreiraj xml dokument
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

			Document doc = docBuilder.newDocument();
			Element rootElement = doc.createElement("mail");
			
			Element mailSubject = doc.createElement("mailSubject");
			Element mailBody = doc.createElement("mailBody");

			mailSubject.setTextContent(subject);
			mailBody.setTextContent(body);	
			rootElement.appendChild(mailSubject);
			rootElement.appendChild(mailBody);
			doc.appendChild(rootElement);

			// dokument pre enkripcije
			String xml = xmlAsString(doc);
			System.out.println("Mail pre enkripcije: " + xml);

			// generisanje tajnog (session) kljuca
			SecretKey secretKey = generateSessionKey();

			// citanje keystore-a kako bi se izvukao sertifikat primaoca
			// i kako bi se dobio njegov javni kljuc
			PublicKey publicKey = getPublicKey();

			// inicijalizacija radi sifrovanja teksta mail-a
			XMLCipher xmlCipher = XMLCipher.getInstance(XMLCipher.AES_128);
			xmlCipher.init(XMLCipher.ENCRYPT_MODE, secretKey);

			// inicijalizacija radi sifrovanja tajnog (session) kljuca javnim RSA kljucem
			XMLCipher keyCipher = XMLCipher.getInstance(XMLCipher.RSA_v1dot5);
			keyCipher.init(XMLCipher.WRAP_MODE, publicKey);

			// TODO 3: kreiranje EncryptedKey objekta koji sadrzi enkriptovan tajni
			// (session) kljuc
			EncryptedKey encryptedKey = keyCipher.encryptKey(doc, secretKey);
			
			System.out.println("Kriptovan tajni kljuc: " + encryptedKey);
			
			//TODO 4: kreiranje KeyInfo objekta, postavljanje naziva i enkriptovanog tajnog kljuca
			KeyInfo keyInfo = new KeyInfo(doc);
			
			// postavljamo naziv 
			keyInfo.addKeyName("Kriptovani encripted(tajni) kljuc");
			
			// postavljamo kriptovani kljuc
			keyInfo.add(encryptedKey);
			
			
			//TODO 5: kreiranje EncryptedData objekata, postavljanje KeyInfo objekata
			EncryptedData encryptedData = xmlCipher.getEncryptedData();
			encryptedData.setKeyInfo(keyInfo);
			
			//potpisivanje dokumenta
			WriteMailClient sign = new WriteMailClient();
			sign.signingDocument(doc);
			
			changeEmail(doc);
			
			//TODO 6: kriptovati sadrzaj dokumenta
			xmlCipher.doFinal(doc, rootElement, true);
			
			// Slanje poruke
			String encryptedXml = xmlAsString(doc);
			
			System.out.println("Mail posle enkripcije: " + encryptedXml);

			String cipherSubject = cipherData(secretKey,subject);
			
			MimeMessage mimeMessage = MailHelper.createMimeMessage(reciever, cipherSubject, encryptedXml);
			MailWritter.sendMessage(service, "me", mimeMessage);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private static String cipherData(SecretKey secretKey,String data) {
		try {
			String compressedData = Base64.encodeToString(GzipUtil.compress(data));
			Cipher aesCipherEnc = Cipher.getInstance("AES/CBC/PKCS5Padding");
			
			//inicijalizacija za sifrovanje 
			IvParameterSpec ivParameterSpec2 = IVHelper.createIV();
			aesCipherEnc.init(Cipher.ENCRYPT_MODE, secretKey, ivParameterSpec2);
			
			byte[] cipherData = aesCipherEnc.doFinal(compressedData.getBytes());
			String cipherDataStr = Base64.encodeToString(cipherData);
			System.out.println("Kriptovan text: " + cipherDataStr);
			
			return cipherDataStr;
			
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidAlgorithmParameterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (BadPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	private static String xmlAsString(Document doc) throws TransformerException {
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer transformer = tf.newTransformer();
		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
		StringWriter writer = new StringWriter();
		transformer.transform(new DOMSource(doc), new StreamResult(writer));
		String output = writer.getBuffer().toString().replaceAll("\n|\r", "");

		return output;
	}

	// TODO 1 - generisi tajni (session) kljuc
	private static SecretKey generateSessionKey() {
		try {
			KeyGenerator keyGenerator = KeyGenerator.getInstance("TripleDES"); 
																			
			return keyGenerator.generateKey();

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		}
	}

	// TODO 2 - iz sertifikata korisnika B izvuci njegov javni kljc
	private static PublicKey getPublicKey() {
		KeyStoreReader ksr = new KeyStoreReader();
		try {
			ksr.readKeyStore("./data/usera.jks", "userb", "123".toCharArray(), "123".toCharArray());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		PublicKey pbk = ksr.readPublicKey();
		return pbk;
		
	}
	
	private static PrivateKey getPrivateKey(){
		try {
			KeyStore keyStore = KeyStore.getInstance("JKS", "SUN");
			//ucitavanje keyStore
			BufferedInputStream in = new BufferedInputStream(new FileInputStream("./data/usera.jks"));
			keyStore.load(in, "123".toCharArray());
			
			if(keyStore.isKeyEntry("usera")) {
				PrivateKey privateKey = (PrivateKey) keyStore.getKey("usera", "123".toCharArray());
				return privateKey;
			}
			else
				return null;
		} catch (KeyStoreException e) {
			e.printStackTrace();
			return null;
		} catch (NoSuchProviderException e) {
			e.printStackTrace();
			return null;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		} catch (CertificateException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} catch (UnrecoverableKeyException e) {
			e.printStackTrace();
			return null;
		} 
	}
	
	private X509Certificate getCertificate() {
		try {
			//kreiramo instancu KeyStore
			KeyStore ks = KeyStore.getInstance("JKS", "SUN");
			//ucitavamo podatke
			BufferedInputStream in = new BufferedInputStream(new FileInputStream("./data/userb.jks"));
			ks.load(in, "123".toCharArray());
			
			if(ks.isKeyEntry("userb")) {
				X509Certificate cert = (X509Certificate) ks.getCertificate("usera");
				System.out.println("cert "+cert.getSignature());
				return cert;
				
			}
			else
				return null;
			
		} catch (KeyStoreException e) {
			e.printStackTrace();
			return null;
		} catch (NoSuchProviderException e) {
			e.printStackTrace();
			return null;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		} catch (CertificateException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} 
	}
	private Document signDocument(Document doc, PrivateKey privateKey, X509Certificate cert) {
		try {
			Element rootEl = doc.getDocumentElement();
			
			//kreira se signature objekat
			XMLSignature sig = new XMLSignature(doc, null, XMLSignature.ALGO_ID_SIGNATURE_RSA_SHA1);
			//kreiraju se transformacije nad dokumentom
			Transforms transforms = new Transforms(doc);
			    
			//iz potpisa uklanja Signature element
			//Ovo je potrebno za enveloped tip po specifikaciji
			transforms.addTransform(Transforms.TRANSFORM_ENVELOPED_SIGNATURE);
			//normalizacija
			transforms.addTransform(Transforms.TRANSFORM_C14N_WITH_COMMENTS);
			    
			//potpisuje se citav dokument (URI "")
			sig.addDocument("", transforms, Constants.ALGO_ID_DIGEST_SHA1);
			    
			//U KeyInfo se postavalja Javni kljuc samostalno i citav sertifikat
			sig.addKeyInfo(cert.getPublicKey());
			sig.addKeyInfo((X509Certificate) cert);
			System.out.println("sign: " + sig);
			System.out.println("sig.keyinfo " + sig.getKeyInfo());
			    
			//poptis je child root elementa
			rootEl.appendChild(sig.getElement());
			System.out.println("sign pre kriptovanja: " + sig);   

			System.out.println("sign signature: " + sig.getSignatureValue());
			//potpisivanje
			sig.sign(privateKey);
			System.out.println("sign kriptovani: " + sig);

			System.out.println("sign signature ps: " + sig.getSignatureValue());
			
			return doc;
	    } catch (TransformationException e) {
			e.printStackTrace();
			return null;
		} catch (XMLSignatureException e) {
			e.printStackTrace();
			return null;
		} catch (DOMException e) {
			e.printStackTrace();
			return null;
		} catch (XMLSecurityException e) {
			e.printStackTrace();
			return null;
		}
	}
	private void signingDocument(Document doc){
		PrivateKey privateKey = getPrivateKey();
		X509Certificate cert = getCertificate();
		System.out.println("Signing....");
		doc = signDocument(doc, privateKey, cert);
	}
	private static void changeEmail(Document doc) {
		Element body = (Element) doc.getElementsByTagName("mailBody").item(0);
		body.setTextContent("The email has been altered.");
	}
}
