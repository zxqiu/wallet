/**
 * Created by zxqiu on 3/14/17.
 */
var api = APIs.createNew();

function submitSuccess(data) {
    window.location.href = data.message;
}
function submitList () {
    var forms = $("form");
    var categories = new Array();

    forms.each(function(i) {
        var idElem = forms[i].getElementsByClassName("categoryID");
        var action = forms[i].getElementsByClassName("categoryAction");
        var bookSelector = forms[i].getElementsByClassName("bookSelector");
        var nameElem = forms[i].getElementsByClassName("categoryName");
        var colorElem = forms[i].getElementsByClassName("categoryColor");

        categories[i] = new Object();
        categories[i].id = idElem[0].value;
        categories[i].book_group_id = bookSelector[0].value;
        categories[i].action = action[0].value;
        categories[i].name = nameElem[0].value;
        categories[i].picture_id = colorElem[0].value;
    });

    api.setPostCategoryListSuccessCallback(submitSuccess);
    api.postCategoryList(categories);
}

var user_id = $("#userID").val();
var bookList;
api.setGetAllBookSuccessCallback(function (data) {
    var newCategoryBtn = document.getElementById("newCategoryBtn");
    newCategoryBtn.removeAttribute("disabled");
    newCategoryBtn.innerHTML = "new cat";
    bookList = data;
    noBookWarning(bookList);
});
api.getAllBook(user_id);

var newCnt = 0;
function newCategory() {
    var parent = document.getElementById("categoryList");

    var form = document.createElement("form");
    form.setAttribute("role", "form");
    form.setAttribute("id", "categoryForm");

    var id = document.createElement("input");
    id.setAttribute("type", "hidden");
    id.setAttribute("value", "");
    id.setAttribute("class", "categoryID");
    id.setAttribute("name", "id");

    var div = document.createElement("div");
    div.setAttribute("class", "form-group");

    var action = document.createElement("input");
    action.setAttribute("class", "categoryAction");
    action.setAttribute("name", "action");
    action.setAttribute("type", "hidden");
    action.setAttribute("value", "edit");

    var color = document.createElement("input");
    color.setAttribute("id", "newCategoryColor" + newCnt);
    color.setAttribute("name", "picture_id");
    color.setAttribute("class", "categoryColor");
    color.setAttribute("type", "hidden");
    color.setAttribute("value", "#FFFFFF");

    var inputGroup = document.createElement("div");
    inputGroup.setAttribute("class", "input-group");

    var colorBtn = document.createElement("span");
    colorBtn.setAttribute("class", "input-group-addon btn categoryColorBtn");
    colorBtn.setAttribute("id", "newCategoryColorBtn" + newCnt);
    colorBtn.innerHTML = "Color";

    var name = document.createElement("input");
    name.setAttribute("id", "newCategoryName" + newCnt);
    name.setAttribute("name", "name");
    name.setAttribute("type", "text");
    name.setAttribute("value", "");
    name.setAttribute("title", "");
    name.setAttribute("style", "text-align: center;");
    name.setAttribute("class", "categoryName"
        + " form-control");

    var emptyS = document.createElement("span");
    emptyS.setAttribute("class", "input-group-addon");
    emptyS.innerHTML = "Book";

    var bookSelect = document.createElement("select");
    bookSelect.setAttribute("class", "form-control bookSelector");
    for (var i = 0; i < bookList.length; i++) {
        var bookO = document.createElement("option");
        bookO.setAttribute("class", "book-option");
        bookO.setAttribute("style", "background: " + bookList[i]["picture_id"]);
        bookO.setAttribute("value", bookList[i]["group_id"]);
        bookO.innerHTML = bookList[i]["name"];
        bookSelect.appendChild(bookO);
    }

    var deleteS = document.createElement("span");
    deleteS.setAttribute("class", "input-group-addon btn categoryDelete");
    deleteS.innerHTML = "X";

    inputGroup.appendChild(colorBtn);
    inputGroup.appendChild(name);
    inputGroup.appendChild(emptyS);
    inputGroup.appendChild(bookSelect);
    inputGroup.appendChild(deleteS);
    div.appendChild(action);
    div.appendChild(color);
    div.appendChild(inputGroup);
    form.appendChild(id);
    form.appendChild(div);
    parent.appendChild(form);

    newCnt++;

    initDeleteCategoryBtn();
    initCategoryColorBtn();
}


function initDeleteCategoryBtn() {
    $(".categoryDelete").click(function (e) {
        var btn = $(e.target);
        var parent = btn.parent();
        var grandparent = parent.parent();
        var name = parent.find("input");
        var action = grandparent.find(".categoryAction");

        if (btn.hasClass("delete")) {
            btn.removeClass("delete");
            name.val(name.attr("title"));
            action.val("edit");
        } else {
            btn.addClass("delete");
            name.val("mark delete");
            action.val("delete");
        }
    });
}

function initCategoryColorBtn() {
    $(".categoryColorBtn").each(function () {
        var picker = this;
        var parent = $(picker).parent();
        var categoryColor = $($(parent).parent()[0]).find(".categoryColor")[0];
        var categoryName = $(parent).find(".categoryName")[0];

        var categoryColorValue = categoryColor.value;
        var categoryColorValueTiny = tinycolor(categoryColorValue);
        var categoryColorValueCielch = $.fn.ColorPickerSliders.rgb2lch(categoryColorValueTiny.toRgb());
        if (categoryColorValueCielch.l < 60) {
            $(categoryName).css("color", "white");
        } else {
            $(categoryName).css("color", "black");
        }

        $(this).ColorPickerSliders({
            previewontriggerelement: false,
            placement: 'bottom',
            color: categoryColorValue,
            swatches: COLOR_LIST,
            customswatches: false,
            order: {},
            onchange: function (container, color) {

                $(categoryName).css("background-color", color.tiny.toRgbString());
                $(categoryColor).val(color.tiny.toHexString());

                if (color.cielch.l < 60) {
                    $(categoryName).css("color", "white");
                } else {
                    $(categoryName).css("color", "black");
                }
            }
        });
    });
}

/************************** jquery functions ********************************/

$(document).ready(function () {
    initDeleteCategoryBtn();
    initCategoryColorBtn();
});
