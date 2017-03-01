var QUESTION = "?";
var hostURL = "http://localhost:8080"
var QueryParam = "queryParam="
var apiInsertItem = "/api/books/newitem"
var apiGetCategories = "/api/books/getcategories"

function insertBooksItem() {
	var param = new Object();

	param.user_id = (document.getElementById("booksItemUserId").value == "") ?
			"webuser" : document.getElementById("booksItemUserId").value;
	param.event_date = document.getElementById("booksItemEventDate").value;
	param.amount = document.getElementById("booksItemAmount").value;
	param.category = document.getElementById("booksItemCategory").value;
	param.note = document.getElementById("booksItemNote").value;
	param.picture_url = "";
	
	if (param.user_id == "") {
		$("#booksItemUserId").parent().addClass("has-error");
		return;
	}
	
	if (param.event_date == "") {
		$("#booksItemEventDate").parent().parent().addClass("has-error");
		return;
	}
	
	if (param.amount == "") {
		$("#booksItemAmount").parent().addClass("has-error");
		return;
	}
	
	if (param.category == "") {
		$("#booksItemCategory").parent().parent().addClass("has-error");
		return;
	}

	// format date
	param.event_date = formatUSToISO(param.event_date);

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
	var parentDiv = document.getElementById("booksItemCategoryList");

	while (parentDiv.firstChild) {
		parentDiv.removeChild(parentDiv.firstChild);
	}

	
	console.log("received " + jsonArray.length + " categories");
	for (var i = 0; i < jsonArray.length; i++) {
		var jsonObj = jsonArray[i];
		var optionLi = document.createElement("li");
		var optionA = document.createElement("a");

		optionA.textContent = jsonObj.name;
		optionA.value = jsonObj.name;
		
		optionLi.appendChild(optionA);
		parentDiv.appendChild(optionLi);
	}
}

function getCategories() {
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
			$('#booksItemCategoryList li a').on('click', function(){
				$('#booksItemCategory').val($(this).text());
				$('#booksItemCategory').text($(this).text());
			});
                }
        });
}

function formatISOToUS(date) {
	return date.slice(5, 7) + '/' + date.slice(8, 10) + '/' + date.slice(0, 4);
}

function formatUSToISO(date) {
	return date.slice(6, 10) + "-" + date.slice(0, 2) + "-" + date.slice(3, 5);
}

/************************** jquery functions ********************************/

$(document).ready(function(){
	// set date
	var date = new Date();
	var month = date.getMonth() + 1;
	month = (month > 9) ? month : "0" + month;
	date = month + "/" + date.getDate() + "/" + date.getFullYear();
	//console.log("current time : " + date);
	$('#booksItemEventDate').attr("value", date);

	// load category
	getCategories();
});

$(function() {
	$('.datepicker').datepicker( {
		format: 'mm/dd/yyyy',
		autoclose: true,
		immediateUpdates: true,
		assumeNearbyYear: true,
		todayHighlight: true
	});
});

$('.date').on('changeDate', function(ev){
	$(this).datepicker('hide');
});
