$(document).ready(function() {
	var token = localStorage.getItem("token");
	console.log(token)
	if (token == "undefined" || token == null || token == "null") {
		window.location.href = "login.html";
	} else {
		currentUser();

	}
});
function fillTable(){
	var token = localStorage.getItem("token");
	tableHeader();
	$.ajax({
		url:'https://localhost:8443/api/users/active',
		headers:{Authorization:"Bearer " + token},
		type: 'GET',
		dataType:'json',
		crossDomain: true,
		success:function(response){
			if(response.length == 0){
				var table =  $('#myTable');
				table.empty();
				return;
			}
			for(var i=0; i<response.length; i++) {
				var table =  $('#myTable');
				user = response[i];
				console.log(user.email);
				table.append('<tr>'+
								'<td>'+user.email+'</td>'+
								'<td><button onclick="downloadCer(\''+user.email+'\')" class="btn btn-default">Download</button></td>'+
							'</tr>');
			}
		},
		error: function (jqXHR, textStatus, errorThrown) {  
			alert(textStatus+" "+jqXHR.status)
		}
	});
}
function fillInactiveTable(){
	var token = localStorage.getItem("token");
	tableInactiveHeader();
	$.ajax({
		url:'https://localhost:8443/api/users/inactive',
		headers:{Authorization:"Bearer " + token},
		type: 'GET',
		dataType:'json',
		crossDomain: true,
		success:function(response){
			if(response.length == 0){
				var table =  $('#myInactiveTable');
				table.empty();
				return;
			}
			for(var i=0; i<response.length; i++) {
				var table =  $('#myInactiveTable');
				user = response[i];
				console.log(user.email);
				table.append('<tr>'+
								'<td>'+user.email+'</td>'+
								'<td><button onclick="activateUser('+user.id+')" class="btn btn-default">Activate</button></td>'+
							'</tr>');
			}
		},
		error: function (jqXHR, textStatus, errorThrown) {  
			alert(textStatus+" "+jqXHR.status)
		}
	});
}
function tableHeader(){
	var table =  $('#myTable');
	table.empty();
	table.append('<tr>'+
				'<th>Active Users</th>'+
				'<th><button onclick="fillTable()" class="btn btn-default">Refresh</button></th>'+
				'</tr>'+
				'<tr>'+
					'<th><input id="paramActive" type="text" placeholder="Search" ></th>'+
					'<th><button onclick="searchActive()" class="btn btn-default">Submit</button</th>'+
				'</tr>'+
				'<tr>'+
					'<th>Email</th>'+
					'<th>Download</th>'+
				'</tr>');
}
function tableInactiveHeader(){
	var table =  $('#myInactiveTable');
	table.empty();
	table.append('<tr>'+
				'<th>Inactive Users</th>'+
				'<th><button onclick="fillInactiveTable()" class="btn btn-default">Refresh</button></th>'+
				'</tr>'+
				'<tr>'+
					'<th><input id="paramInctive" type="text" placeholder="Search"></th>'+
					'<th><button onclick="searchInactive()" class="btn btn-default">Submit</button</th>'+
				'</tr>'+
				'<tr>'+
					'<th>Email</th>'+
					'<th>Acivate</th>'+
				'</tr>');
}
function searchActive(){
	var token = localStorage.getItem("token");
	var param = $('#paramActive').val().trim();
	if(param==""){
		return;
	}
	tableHeader();
	$.ajax({
		url:'https://localhost:8443/api/users/search_active/'+param,
		headers:{Authorization:"Bearer " + token},
		type: 'GET',
		dataType:'json',
		crossDomain: true,
		success:function(response){
			for(var i=0; i<response.length; i++) {
				var table =  $('#myTable');
				user = response[i];
				console.log(user.email);
				table.append('<tr>'+
								'<td>'+user.email+'</td>'+
								'<td><button class="btn btn-default">Download</button></td>'+
							'</tr>');
			}
		},
		error: function (jqXHR, textStatus, errorThrown) {  
			alert(textStatus+" "+jqXHR.status)
		}
	});
}
function searchInactive(){
	var token = localStorage.getItem("token");
	var param = $('#paramInctive').val().trim();
	if(param==""){
		return;
	}
	tableInactiveHeader();
	$.ajax({
		url:'https://localhost:8443/api/users/search_inactive/'+param,
		headers:{Authorization:"Bearer " + token},
		type: 'GET',
		dataType:'json',
		crossDomain: true,
		success:function(response){
			for(var i=0; i<response.length; i++) {
				var table =  $('#myInactiveTable');
				user = response[i];
				console.log(user.email);
				table.append('<tr>'+
								'<td>'+user.email+'</td>'+
								'<td><button onclick="activateUser('+user.id+')" class="btn btn-default">Activate</button></td>'+
							'</tr>');
			}
		},
		error: function (jqXHR, textStatus, errorThrown) {  
			alert(textStatus+" "+jqXHR.status)
		}
	});
}
function activateUser(id){
	var token = localStorage.getItem("token");
	console.log(id)
	$.ajax({
		type: 'PUT',
		headers:{"Authorization" :"Bearer " + token},
        url: 'https://localhost:8443/api/users/activate/'+id,
        dataType: 'json',
        crossDomain: true,
		cache: false,
		processData: false,
		success:function(response){
			alert("User activated.");
			fillTable();
			fillInactiveTable();
		},
		error: function (jqXHR, textStatus, errorThrown) {  
			alert(textStatus+" "+jqXHR.status)
		}
	});
}
function currentUser(){
	var token = localStorage.getItem("token");
	$.ajax({
		type: 'PUT',
		headers:{"Authorization" :"Bearer " + token},
        url: 'https://localhost:8443/api/users/whoami',
        dataType: 'json',
        crossDomain: true,
		cache: false,
		processData: false,
		success:function(response){
			console.log(response);
			for ( var i in response.authorities) {
				console.log(i);
				console.log(response.authorities[i].name);
				if(response.authorities[i].name=="ADMIN"){
					fillInactiveTable();
				}
			}
			fillTable();
		},
		error: function (jqXHR, textStatus, errorThrown) {  
			alert(textStatus+" "+jqXHR.status)
		}
	});
}
function logout(){
	localStorage.removeItem("token");
	window.location.replace("https://localhost:8443/login.html");
}
function downloadCer(userName){
	var token = localStorage.getItem("token");
	console.log(token);
	
	var xhr = new XMLHttpRequest();
	xhr.open('GET', "/api/demo/download/"+userName, true);
	xhr.setRequestHeader("Authorization", "Bearer " + token);
	xhr.responseType = 'blob';

	xhr.onload = function(e) {
		if (this.status == 200) {
			var blob = this.response;
			console.log(blob);
			var a = document.createElement('a');
			var url = window.URL.createObjectURL(blob);
			a.href = url;
			a.download = xhr.getResponseHeader('filename');
			a.click();
			window.URL.revokeObjectURL(url);
		}
	};

	xhr.send();
}