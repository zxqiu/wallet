var QUESTION = "?";
var hostURL = "http://localhost:8080"
var QueryParam = "queryParam="
var apiInsertItem = "/api/books/newitem"
var apiGetCategories = "/api/books/getcategories"

$( document ).ready(function() {
	var param = new Object();
	param.user_id = "webuser";
	var paramJSONString = JSON.stringify(param);

	var request = hostURL + apiGetCategories + QUESTION + QueryParam + paramJSONString;
        $.ajax({
                type: "GET",
                url: request,
                dataType: "json",
        }).then(function(jsonData) {
                if(jsonData != null) {
			console.log(JSON.stringify(jsonData));
			createCategoryOptions(jsonData);
                }
        });

});


function insertBooksItem() {
	var param = new Object();

	param.user_id = (document.getElementById("booksItemUserId").value == "") ?
			"webuser" : document.getElementById("booksItemUserId").value;
	param.event_date = (document.getElementById("booksItemEventDate").value == "") ?
			"19900329" : document.getElementById("booksItemEventDate").value;
	param.amount = (document.getElementById("booksItemAmount").value == "") ?
			"100" : document.getElementById("booksItemAmount").value;
	param.category = (document.getElementById("booksItemCategory").value == "") ?
			"all" : document.getElementById("booksItemCategory").value;
	param.note = (document.getElementById("booksItemNote").value == "") ?
			"note" : document.getElementById("booksItemNote").value;
	param.picture_url = "";

	paramJSONObj = {"user_id":param.user_id,
			"event_date":param.event_date,
			"amount":param.amount,
			"category":param.category,
			"note":param.note,
			"picture_url":param.picture_url};
	paramJSONString = JSON.stringify(paramJSONObj);

	postURL = hostURL + apiInsertItem;
	console.log(postURL);
	console.log(paramJSONString);

	$.ajax({
		type: "POST",
		url: postURL,
		data: paramJSONString,
		dataType: 'json',
		contentType: 'application/json',
		success: function(data, textStatus, jqXHR) {
			alert("success: " + data.message);
		},
		error: function(jqXHR, textStatus, errorThrown) {
			alert("error with status: " + textStatus);
		}
	});
}

function createCategoryOptions(retData) {
	var jsonArray = retData;
	var parentDiv = document.getElementById("booksItemCategory");

	while (parentDiv.firstChild) {
		parentDiv.removeChild(parentDiv.firstChild);
	}

	
	console.log("received " + jsonArray.length + " categories");
	for (var i = 0; i < jsonArray.length; i++) {
		var jsonObj = jsonArray[i];
		var option = document.createElement("option");

		option.textContent = jsonObj.name;
		option.value = jsonObj.name;
		parentDiv.appendChild(option);
	}
}

/************************** jquery functions ********************************/
$(function() {
	$('.datepicker').datepicker( {
		format: 'mm/dd/yyyy',
		autoclose: true,
		immediateUpdates: true,
		assumeNearbyYear: true,
		todayHighlight: true
	});
});

$(document).ready(function(){
	var date = new Date();
	date = date.toISOString().slice(5,7) + '/' + date.toISOString().slice(8,10) + '/' + date.toISOString().slice(0,4);
  $('#booksItemEventDate').attr("value", date);
});
