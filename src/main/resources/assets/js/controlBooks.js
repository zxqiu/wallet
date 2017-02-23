var SPLITER = "!";
var QUESTION = "?";
var QueryParam = "queryParam";
var hostURL = "http://localhost:8080";
var apiInsertBooks = "/api/books/newitem";
var apiGetBooks = "/api/books/getbooks";

var booksItemsEachLine = 6;
var test_user_cnt = 0;

function insertBooks() {
	console.log("insert new books to server start");

	var param = new Object();

	param.user_id = "webuser";
	param.event_time = 19900329;
	param.category = "buy";
	param.amount = 10 + test_user_cnt++;
	param.note = "note";
	param.picture_url = "";

	var paramJSONString = JSON.stringify(param);

	var request = hostURL + apiInsertBooks + QUESTION + QueryParam + "=" + paramJSONString;
	$.ajax({
		type: "POST",
		url: request,
		dataType: "json",
	}).then(function(jsonData) {
		if(jsonData != null) {
			console.log(request);
		}
	});
	console.log("insert new books to server done");
}


function getBooks() {
	console.log("get books from server start");

	var param = new Object();

	param.user_id = "webuser";

	var paramJSONString = JSON.stringify(param);

	var request = hostURL + apiGetBooks + QUESTION + QueryParam + "=" + paramJSONString;
	$.ajax({
		type: "GET",
		url: request,
		dataType: "json",
	}).then(function(retData) {
		if(retData != null) {
			console.log(retData);
			createBooksItems(retData);
		}
	});
	console.log("get books from server done");
}


function createBooksItems(retData) {
	var cnt = 0, parentDiv, booksItemsCol, booksItemsRow;
        var jsonArray = retData;//JSON.parse(retData);
	parentDiv = document.getElementById("booksList");

	while (parentDiv.firstChild) {
		parentDiv.removeChild(parentDiv.firstChild);
	}

	console.log("received " + jsonArray.length + " items");
	for (var i = 0; i < jsonArray.length; i++) {
                var jsonString = jsonArray[i];
		var jsonObj = JSON.parse(jsonString);

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
	itemA.href = "#";
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
	itemImg.src = "http://www.koolbreeze.eclipse.co.uk/block%20lightblue.jpg"; //itemInfo.Image
	itemSpanDetailTitle.innerHTML =
			itemInfo.event_time + " $" +
			itemInfo.amount;
	itemSpanDetailInfoCategory.innerHTML = "category: " + itemInfo.category;
	itemSpanDetailInfoNote.innerHTML = "note: " + itemInfo.note;

	return itemDiv;
}


