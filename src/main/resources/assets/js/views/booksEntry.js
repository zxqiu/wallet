var books = Books.createNew();
var item = new Object();
var user_id;

function goHome(data) {
	window.location.replace("/books");
}

function postError(textStatus) {
	console.log("post new books item failed : " + textStatus);
	$('#booksEntrySubmit').removeAttr("disabled");
	var text = "try again";
	$('#booksEntrySubmit').html(text.fontcolor("red"));
}

function postBooksEntry() {
	if (!item.id) {
		item.id = "";
	}
	item.user_id = user_id;
	item.event_date = document.getElementById("booksEntryEventDate").value;
	item.amount = document.getElementById("booksEntryAmount").value;
	item.category = document.getElementById("booksEntryCategory").value;
	item.note = document.getElementById("booksEntryNote").value;
	item.photo = "";
	
	if (item.user_id == "") {
		$("#booksEntryUserId").parent().addClass("has-error");
		return;
	}
	
	if (item.event_date == "") {
		$("#booksEntryEventDate").parent().parent().addClass("has-error");
		return;
	}
	
	if (item.amount == "") {
		$("#booksEntryAmount").parent().addClass("has-error");
		return;
	}
	
	if (item.category == "") {
		$("#booksEntryCategory").parent().parent().addClass("has-error");
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

	books.setPostBooksEntrySuccessCallback(goHome);
	books.setPostBooksEntryErrorCallback(postError);
	books.postBooksEntry(paramJSONObj);
}

function deleteError(textStatus) {
	console.log("delete books item failed : " + textStatus);
	$('#booksEntryDelete').removeAttr("disabled");
	var text = "try again";
	$('#booksEntryDelete').html(text.fontcolor("red"));
}

function deleteBooksEntry() {
	if (!item.id || item.id == "") {
		return;
	}
	
	books.setDeleteBooksEntrySuccessCallback(goHome);
	books.setDeleteBooksEntryErrorCallback(deleteError);
	books.deleteBooksEntry(item.id);
}

function formatISOToUS(date) {
	return date.slice(5, 7) + '/' + date.slice(8, 10) + '/' + date.slice(0, 4);
}

function formatUSToISO(date) {
	return date.slice(6, 10) + "-" + date.slice(0, 2) + "-" + date.slice(3, 5);
}

function fillForm(item) {
	if (item.user_id) {
		$('#booksEntryUserId').attr("value", item.user_id);
	}
	if (item.event_date) {
		$('#booksEntryEventDate').attr("value", item.event_date);
	}
	if (item.amount) {
		$('#booksEntryAmount').attr("value", item.amount);
	}
	if (item.category) {
		$('#booksEntryCategory').attr("value", item.category);
	}
	if (item.note) {
		$('#booksEntryNote').attr("value", item.note);
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

	// load session cookie
	var session_cookie = getCookie(SESSION_COOKIE_NAME);
	if (!session_cookie || session_cookie == "") {
		window.location.replace("/views/login.html");
	}
	
	user_id = session_cookie.substr(0, session_cookie.indexOf(":"));

	// load category
    $('#booksEntryCategoryList li a').on('click', function(){
        $('#booksEntryCategory').val($(this).text()).change();
        $('#booksEntryCategory').text($(this).text());
    });

	// parse parameter
	var urlParam = location.search.substr(location.search.indexOf("?")+1).replace(/%22/g, "\"");
	if (urlParam && urlParam != "") {
		item = JSON.parse(urlParam);
		$('#booksEntryDelete').show();
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
	$('#booksEntrySubmit').html("submit");
	$('#booksEntrySubmit').removeAttr("disabled");

	$('#booksEntryForm div').removeClass("has-error");;
});

$(':input').on('change', function(){
	$('#booksEntrySubmit').html("submit");
	$('#booksEntrySubmit').removeAttr("disabled");

	$('#booksEntryForm div').removeClass("has-error");;
});

$('#booksEntrySubmit').on('click', function(){
	$('#booksEntrySubmit').html("submitted");
	$(this).attr("disabled", true);
});

$('#booksEntryDelete').on('click', function(){
	$('#booksEntryDelete').html("submitted");
	$(this).attr("disabled", true);
});
