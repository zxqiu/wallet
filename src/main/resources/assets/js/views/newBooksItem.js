var QUESTION = "?";
var hostURL = "http://localhost:8080"
var QueryParam = "queryParam="
var apiInsertItem = "/api/books/newitem"
var apiGetMakeModel = "/api/getMakeModel"

var allMakes = "allMakes"

$( document ).ready(function() {
	var param = new Object();
	param.getAllMakes = "1";
	var paramJSONString = JSON.stringify(param);

	var request = hostURL + apiGetMakeModel + QUESTION + QueryParam + paramJSONString;
        $.ajax({
                type: "GET",
                url: request,
                dataType: "json",
        }).then(function(jsonData) {
                if(jsonData != null) {
			console.log(JSON.stringify(jsonData[allMakes]));
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


/************************** element control functions ********************************/
$(function() {
    $('#booksItemEventDatePicker').datepicker( {
        //changeMonth: true,
        //changeYear: true,
        //showButtonPanel: true,
        //dateFormat: 'MM dd yy',
        //onClose: function(dateText, inst) {
			//var date = $("#ui-datepicker-div .ui-datepicker-date :selected").val();
            //var month = $("#ui-datepicker-div .ui-datepicker-month :selected").val();
            //var year = $("#ui-datepicker-div .ui-datepicker-year :selected").val();
            //$(this).datepicker('setDate', new Date(year, month, date));
        //}
    });
});