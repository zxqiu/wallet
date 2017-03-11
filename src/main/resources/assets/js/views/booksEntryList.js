var books = Books.createNew();
var user_id;

var booksEntrysEachLine = 6;

function insertBooksEntry() {
	books.insertBooksEntryView();
}

function getBooks() {
	console.log("get books from server start");

	books.setGetAllBooksEntrySuccessCallback(createBooksEntrys);
	books.setGetAllBooksEntryErrorCallback(getBooksError);
	books.getAllBooksEntry(user_id);

	console.log("get books from server done");
}

function getBooksError(data) {
	if (data.responseJSON.message == "Session Cookie Invalid") {
		window.location.replace("/views/login.html");
	}
}

function createBooksEntrys(retData) {
	var cnt = 0, parentDiv, booksEntrysCol, booksEntrysRow;
	var jsonArray = retData;
	parentDiv = document.getElementById("booksList");

	while (parentDiv.firstChild) {
		parentDiv.removeChild(parentDiv.firstChild);
	}

	console.log("received " + jsonArray.length + " items");
	for (var i = 0; i < jsonArray.length; i++) {
		var jsonObj = jsonArray[i];

		if (cnt % booksEntrysEachLine == 0) {
			booksEntrysRow = document.createElement("div");
			booksEntrysRow.className = "row";
			parentDiv.appendChild(booksEntrysRow);
		}

		booksEntrysCol = document.createElement("div");
		booksEntrysCol.className = "col-md-" + (12/booksEntrysEachLine);
		booksEntrysRow.appendChild(booksEntrysCol);

		console.log("generating : " + JSON.stringify(jsonObj));
		var item = createSingleBooksEntry(jsonObj);

		booksEntrysCol.appendChild(item);
		
		cnt++;
	}
	console.log(cnt);
	
	$('.books-list-text').on("click", function(e) {
		if ($(e.target).hasClass("dblclicked")) {
			console.log("double clicked");

			console.log($(this).attr("title"));
			window.location.replace("/views/booksEntry.rocker.html?" + $(this).attr("title"));

			$(e.target).removeClass("dblclicked");
		} else {
			$(e.target).addClass("dblclicked");
			setTimeout(function() { $(e.target).removeClass("dblclicked"); }, 1000);
		}
	});
}

function createSingleBooksEntry(itemInfo) {
	var itemDiv = document.createElement("div");
	var itemA = document.createElement("a");
	var itemImg = document.createElement("img");
	var itemSpanDetail = document.createElement("span");
	var itemSpanDetailInfo = document.createElement("span");
	var itemSpanDetailInfoCategory = document.createElement("span");
	var itemSpanDetailInfoNote = document.createElement("span");
	var itemSpanDetailTitle = document.createElement("span");

	itemDiv.className = "books-list";
	itemImg.className = "img-rounded books-list-img";
	itemImg.style.cssText = "width: 100%;";
	itemSpanDetail.className = "books-list-text";
	itemSpanDetailInfo.className = "books-list-text-info";
	itemSpanDetailInfoCategory.id = "bookCategory";
	itemSpanDetailInfoNote.id = "bookNote";
	itemSpanDetailTitle.className = "books-list-text-title";
	itemSpanDetailTitle.id = "vehicleListTitle";

	itemDiv.appendChild(itemA);
	itemA.appendChild(itemImg);
	itemA.appendChild(itemSpanDetail);
	itemSpanDetail.appendChild(itemSpanDetailTitle);
	itemSpanDetail.appendChild(itemSpanDetailInfo);
	itemSpanDetailInfo.appendChild(itemSpanDetailInfoCategory);
	itemSpanDetailInfo.appendChild(itemSpanDetailInfoNote);

	console.log(itemInfo);
	itemSpanDetail.title = JSON.stringify(itemInfo);
	itemImg.src = "http://www.koolbreeze.eclipse.co.uk/block%20lightblue.jpg"; //itemInfo.Image
	itemSpanDetailTitle.innerHTML =
			itemInfo.event_date + " $" +
			itemInfo.amount;
	itemSpanDetailInfoCategory.innerHTML = "category: " + itemInfo.category;
	itemSpanDetailInfoNote.innerHTML = "note: " + itemInfo.note;

	return itemDiv;
}

function formatISOToUS(date) {
	return date.slice(5,7) + '/' + date.slice(8,10) + '/' + date.slice(0,4);
}

/************************** jquery functions ********************************/

$( document ).ready(function() {
	// load session cookie
	var session_cookie = getCookie(SESSION_COOKIE_NAME);
	if (!session_cookie || session_cookie == "") {
		window.location.replace("/views/login.html");
	}
	
	user_id = session_cookie.substr(0, session_cookie.indexOf(":"));
	
	getBooks();
});
