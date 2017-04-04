/**
 * Created by zxqiu on 3/14/17.
 */
var api = APIs.createNew();

function submitSuccess(data) {
    window.location.href = data.message;
}
function submitList () {
    var forms = $(".bookForm");
    var books = new Array();

    forms.each(function(i) {
        var idElem = forms[i].getElementsByClassName("bookID");
        var action = forms[i].getElementsByClassName("bookAction");
        var nameElem = forms[i].getElementsByClassName("bookName");
        var colorElem = forms[i].getElementsByClassName("bookColor");

        books[i] = new Object();
        books[i].id = idElem[0].value;
        books[i].action = action[0].value;
        books[i].name = nameElem[0].value;
        books[i].picture_id = colorElem[0].value;
    });

    api.setPostBookListSuccessCallback(submitSuccess);
    api.postBookList(books);
};

var newCnt = 0;
function newBook() {
    var parent = document.getElementById("bookList");

    var form = document.createElement("form");
    form.setAttribute("role", "form");
    form.setAttribute("class", "bookForm");

    var id = document.createElement("input");
    id.setAttribute("type", "hidden");
    id.setAttribute("value", "");
    id.setAttribute("class", "bookID");
    id.setAttribute("name", "id");

    var div = document.createElement("div");
    div.setAttribute("class", "form-group");

    var action = document.createElement("input");
    action.setAttribute("class", "bookAction");
    action.setAttribute("name", "action");
    action.setAttribute("type", "hidden");
    action.setAttribute("value", "edit");

    var color = document.createElement("input");
    color.setAttribute("id", "newBookColor" + newCnt);
    color.setAttribute("name", "picture_id");
    color.setAttribute("class", "bookColor");
    color.setAttribute("type", "hidden");
    color.setAttribute("value", "#FFFFFF");

    var inputGroup = document.createElement("div");
    inputGroup.setAttribute("class", "input-group");

    var colorBtn = document.createElement("span");
    colorBtn.setAttribute("class", "input-group-addon btn bookColorBtn");
    colorBtn.setAttribute("id", "newBookColorBtn" + newCnt);
    colorBtn.innerHTML = "Color";

    var name = document.createElement("input");
    name.setAttribute("id", "newBookName" + newCnt);
    name.setAttribute("name", "name");
    name.setAttribute("type", "text");
    name.setAttribute("value", "");
    name.setAttribute("title", "");
    name.setAttribute("style", "text-align: center;");
    name.setAttribute("class", "bookName"
        + " form-control");

    var deleteS = document.createElement("span");
    deleteS.setAttribute("class", "input-group-addon btn bookDelete");
    deleteS.innerHTML = "X";

    inputGroup.appendChild(colorBtn);
    inputGroup.appendChild(name);
    inputGroup.appendChild(deleteS);
    div.appendChild(action);
    div.appendChild(color);
    div.appendChild(inputGroup);
    form.appendChild(id);
    form.appendChild(div);
    parent.appendChild(form);

    newCnt++;

    initDeleteBookBtn();
    initBookColorBtn();
}

function initShareBookBtn() {
    $(".bookShare").click(function (e) {
        var btn = $(e.target);
        var parent = btn.parent();
        var grandparent = parent.parent();
        var grandgrandparent = grandparent.parent();
        var bookID = grandgrandparent.find(".bookID")[0].value;
        var id = "bookShareFormDiv" + bookID;

        if (btn.hasClass("share")) {
            btn.removeClass("share");
            document.getElementById(id).setAttribute("style", "display: none;");
        } else {
            btn.addClass("share")
            document.getElementById(id).setAttribute("style", "display: block;");
        }
    });
}

var deleteWarn = true;
function initDeleteBookBtn() {
    $(".bookDelete").click(function (e) {
        var btn = $(e.target);
        var parent = btn.parent();
        var grandparent = parent.parent();
        var name = parent.find("input");
        var action = grandparent.find(".bookAction");

        if (btn.hasClass("delete")) {
            btn.removeClass("delete");
            btn.text("X");
            name.val(name.attr("title"));
            action.val("edit");
        } else {
            if (deleteWarn) {
                alert("Delete book will also delete all entries and categories in this book!");
                deleteWarn = false;
            }
            btn.addClass("delete")
            btn.text("O");
            name.val("mark delete");
            action.val("delete");
        }
    });
}

function initBookColorBtn() {
    $(".bookColorBtn").each(function () {
        var picker = this;
        var parent = $(picker).parent();
        var bookColor = $($(parent).parent()[0]).find(".bookColor")[0];
        var bookName = $(parent).find(".bookName")[0];

        var bookColorValue = bookColor.value;
        var bookColorValueTiny = tinycolor(bookColorValue);
        var bookColorValueCielch = $.fn.ColorPickerSliders.rgb2lch(bookColorValueTiny.toRgb());
        if (bookColorValueCielch.l < 60) {
            $(bookName).css("color", "white");
        } else {
            $(bookName).css("color", "black");
        }

        $(this).ColorPickerSliders({
            previewontriggerelement: false,
            placement: 'bottom',
            color: bookColorValue,
            swatches: COLOR_LIST,
            customswatches: false,
            order: {},
            onchange: function (container, color) {

                $(bookName).css("background-color", color.tiny.toRgbString());
                $(bookColor).val(color.tiny.toHexString());

                if (color.cielch.l < 60) {
                    $(bookName).css("color", "white");
                } else {
                    $(bookName).css("color", "black");
                }
            }
        });
    });
}

/************************** jquery functions ********************************/

$(document).ready(function () {
    initShareBookBtn();
    initDeleteBookBtn();
    initBookColorBtn();
});

var form = document.getElementsByClassName("bookShareForm");
for (var i = 0; i < form.length; i++) {
    form[i].noValidate = true;
}
$(".bookShareFormSubmit").on("click", function(e) {
    var btn = $(e.target);
    var parent = btn.parent();
    var grandparent = parent.parent();
    var grandgrandparent = grandparent.parent();
    var formCur = grandgrandparent[0];
    for (var i = 0; i < formCur.length; i++) {
        if (!formCur[i].checkValidity()) {
            $("input").each(function () {
                if ($(btn).prop("required") == true && $(this).val() == "") {
                    $(btn).parent().parent().addClass("has-error");
                }
            });
            return;
        }
    }

    var request = new Object();
    request["full_url"] = $(formCur).find("#fullUrl")[0].value;
    request["expire_click"] = parseInt($(formCur).find("#expireClick")[0].value);

    api.setPostTinyUrlToShortSuccessCallback(function (response) {
        while (formCur.firstChild) {
            formCur.removeChild(formCur.firstChild);
        }

        var shortUrl = document.createElement("input");
        shortUrl.setAttribute("readonly", "true");
        shortUrl.setAttribute("type", "text");
        shortUrl.setAttribute("value", window.location.host + "/t/tolong/" + response.short_url);
        shortUrl.setAttribute("class", "form-control");
        shortUrl.setAttribute("style", "cursor:pointer");
        shortUrl.setAttribute("onClick", "this.setSelectionRange(0, this.value.length)");

        formCur.appendChild(shortUrl);
    });

    api.postTinyUrlToShort(request);
});
