var books = Books.createNew();
var user_id;

var booksItemsEachLine = 6;
var test_user_cnt = 0;

function getBooks() {
	console.log("get books from server start");

	books.setGetAllBooksItemSuccessCallback(createBooksItems);
	books.getAllBooksItem(user_id);

	console.log("get books from server done");
}


function createBooksItems(retData) {
	var cnt = 0, parentDiv, booksItemsCol, booksItemsRow;
	var jsonArray = retData;
	parentDiv = document.getElementById("booksList");

	while (parentDiv.firstChild) {
		parentDiv.removeChild(parentDiv.firstChild);
	}

	console.log("received " + jsonArray.length + " items");
	for (var i = 0; i < jsonArray.length; i++) {
		var jsonObj = jsonArray[i];

		if (cnt % booksItemsEachLine == 0) {
			booksItemsRow = document.createElement("div");
			booksItemsRow.className = "row";
			parentDiv.appendChild(booksItemsRow);
		}

		booksItemsCol = document.createElement("div");
		booksItemsCol.className = "col-md-" + (12/booksItemsEachLine);
		booksItemsRow.appendChild(booksItemsCol);

		console.log("generating : " + JSON.stringify(jsonObj));
		var item = createSingleBooksItem(jsonObj);

		booksItemsCol.appendChild(item);
		
		cnt++;
	}
	console.log(cnt);
	
	$('.books-list-text').on("click", function(e) {
		if ($(e.target).hasClass("dblclicked")) {
			console.log("double clicked");

			console.log($(this).attr("title"));
			window.location.replace("/views/newBooksItem.html?" + $(this).attr("title"));

			$(e.target).removeClass("dblclicked");
		} else {
			$(e.target).addClass("dblclicked");
			setTimeout(function() { $(e.target).removeClass("dblclicked"); }, 1000);
		}
	});
}

function createSingleBooksItem(itemInfo) {
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
