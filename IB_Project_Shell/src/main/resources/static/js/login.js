function login(){
	var email =  $('#loginEmail').val().trim();
	var password = $('#loginPassword').val().trim();
	if(email=="" || password==""){
		alert("All fields must be filled.")
		return;
	}
	var data = {
			'username':email,
			'password':password
	}
	console.log(data);
	
	$.ajax({
		type: 'POST',
        contentType: 'application/json',
        url: 'https://localhost:8443/api/auth/login',
        data: JSON.stringify(data),
        dataType: 'json',
        crossDomain: true,
		cache: false,
		processData: false,
		success:function(response){
			var token = response.access_token;
			console.log(token);
			console.log(response);
			
			localStorage.setItem("token",token);
			
			$('#loginModal').modal('toggle');
			
			window.location.href = "users.html";
			
		},
		error: function (jqXHR, textStatus, errorThrown) {  
			if(jqXHR.status=="401"){
				alert("Wrong email or password.");
			}
		}
	});
}
function register(){
	var email =  $('#registerEmail').val().trim();
	var password = $('#registerPassword').val().trim();
	console.log(email+" "+password);
	if(email=="" || password==""){
		alert("All fields must be filled.")
		return;
	}
	var data = {
			'email':email,
			'password':password
	}
	console.log(data);
	
	$.ajax({
		type: 'POST',
        contentType: 'application/json',
        url: 'https://localhost:8443/api/users/register',
        data: JSON.stringify(data),
        dataType: 'json',
        crossDomain: true,
		cache: false,
		processData: false,
		success:function(response){
			$('#registerModal').modal('toggle');
			alert("Successful registration. You need to be accepted by administrator before being able to login.")
		},
		error: function (jqXHR, textStatus, errorThrown) {  
			if(jqXHR.status=="403"){
				alert("Email already in use.");
			}
		}
	});
	
}