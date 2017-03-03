var books = Books.createNew();

function postSuccess(data) {
	window.location.replace("/index.html");
}

function postError(textStatus) {
	console.log("post new books item failed : " + textStatus);
	$('#booksItemSubmit').removeAttr("disabled");
	var text = "try again";
	$('#booksItemSubmit').html(text.fontcolor("red"));
}

function postBooksItem() {
	var param = new Object();

	var urlParam = location.search.substr(location.search.indexOf("?")+1);
	console.log(urlParam);
	//if (id) {
		//param.id = id; 
	//} else {
		param.id = "";
        //}

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

	paramJSONObj = {"id":param.id,
			"user_id":param.user_id,
			"event_date":param.event_date,
			"amount":param.amount,
			"category":param.category,
			"note":param.note,
			"picture_url":param.picture_url};

	books.setPostBooksItemSuccessCallback(postSuccess);
	books.setPostBooksItemErrorCallback(postError);
	books.postBooksItem(paramJSONObj);
}

function createCategoryOptions(data) {
	var jsonArray = data;
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
	
	$('#booksItemCategoryList li a').on('click', function(){
		$('#booksItemCategory').val($(this).text()).change();
		$('#booksItemCategory').text($(this).text());
	});
}

function getCategoriesAndGenerate() {
	books.setGetCategoriesSuccessCallback(createCategoryOptions);
	books.getCategoriesAsync("webuser");
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
	var day = date.getDate();
	var month = date.getMonth() + 1;
	day = (day > 9) ? day : "0" + day;
	month = (month > 9) ? month : "0" + month;

	date = month + "/" + day + "/" + date.getFullYear();
	//console.log("current time : " + date);
	$('#booksItemEventDate').attr("value", date);

	// load category
	getCategoriesAndGenerate();
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

$(':input').on('input', function(){
	$('#booksItemSubmit').html("submit");
	$('#booksItemSubmit').removeAttr("disabled");

	$('#booksItemForm div').removeClass("has-error");;
});

$(':input').on('change', function(){
	$('#booksItemSubmit').html("submit");
	$('#booksItemSubmit').removeAttr("disabled");

	$('#booksItemForm div').removeClass("has-error");;
});

$('#booksItemSubmit').on('click', function(){
	$('#booksItemSubmit').html("submitted");
	$(this).attr("disabled", true);
});
