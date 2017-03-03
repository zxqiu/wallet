var books = Books.createNew();
var item = new Object();

function goHome(data) {
	window.location.replace("/index.html");
}

function postError(textStatus) {
	console.log("post new books item failed : " + textStatus);
	$('#booksItemSubmit').removeAttr("disabled");
	var text = "try again";
	$('#booksItemSubmit').html(text.fontcolor("red"));
}

function postBooksItem() {
	if (!item.id) {
		item.id = "";
	}
	item.user_id = (document.getElementById("booksItemUserId").value == "") ?
			"webuser" : document.getElementById("booksItemUserId").value;
	item.event_date = document.getElementById("booksItemEventDate").value;
	item.amount = document.getElementById("booksItemAmount").value;
	item.category = document.getElementById("booksItemCategory").value;
	item.note = document.getElementById("booksItemNote").value;
	item.picture_url = "";
	
	if (item.user_id == "") {
		$("#booksItemUserId").parent().addClass("has-error");
		return;
	}
	
	if (item.event_date == "") {
		$("#booksItemEventDate").parent().parent().addClass("has-error");
		return;
	}
	
	if (item.amount == "") {
		$("#booksItemAmount").parent().addClass("has-error");
		return;
	}
	
	if (item.category == "") {
		$("#booksItemCategory").parent().parent().addClass("has-error");
		return;
	}

	// format date
	item.event_date = formatUSToISO(item.event_date);

	paramJSONObj = {"id":item.id,
			"user_id":item.user_id,
			"event_date":item.event_date,
			"amount":item.amount,
			"category":item.category,
			"note":item.note,
			"picture_url":item.picture_url};

	books.setPostBooksItemSuccessCallback(goHome);
	books.setPostBooksItemErrorCallback(postError);
	books.postBooksItem(paramJSONObj);
}

function deleteError(textStatus) {
	console.log("delete books item failed : " + textStatus);
	$('#booksItemDelete').removeAttr("disabled");
	var text = "try again";
	$('#booksItemDelete').html(text.fontcolor("red"));
}

function deleteBooksItem() {
	if (!item.id || item.id == "") {
		return;
	}
	
	books.setDeleteBooksItemSuccessCallback(goHome);
	books.setDeleteBooksItemErrorCallback(deleteError);
	books.deleteBooksItem(item.id);
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

function fillForm(item) {
	if (item.user_id) {
		$('#booksItemUserId').attr("value", item.user_id);
	}
	if (item.event_date) {
		$('#booksItemEventDate').attr("value", item.event_date);
	}
	if (item.amount) {
		$('#booksItemAmount').attr("value", item.amount);
	}
	if (item.category) {
		$('#booksItemCategory').attr("value", item.category);
	}
	if (item.note) {
		$('#booksItemNote').attr("value", item.note);
	}
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

	// load category
	getCategoriesAndGenerate();
	
	// parse parameter
	var urlParam = location.search.substr(location.search.indexOf("?")+1).replace(/%22/g, "\"");
	if (urlParam && urlParam != "") {
		item = JSON.parse(urlParam);
		$('#booksItemDelete').show();
	}
	
	if (!item.event_date || item.event_date == "") {
		item.event_date = date;
	} else {
		item.event_date = formatISOToUS(item.event_date);
	}
	
	fillForm(item);
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

$('#booksItemDelete').on('click', function(){
	$('#booksItemDelete').html("submitted");
	$(this).attr("disabled", true);
});