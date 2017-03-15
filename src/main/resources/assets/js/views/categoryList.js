/**
 * Created by zxqiu on 3/14/17.
 */
var books = Books.createNew();

function submitSuccess(data) {
    window.location.href = data.message;
}
function submitList () {
    var forms = $("form");
    var categories = new Array();

    forms.each(function(i) {
        var idElem = forms[i].getElementsByClassName("categoryID");
        var action = forms[i].getElementsByClassName("categoryAction");
        var nameElem = forms[i].getElementsByClassName("categoryName");
        var colorElem = forms[i].getElementsByClassName("categoryColor");

        categories[i] = new Object();
        categories[i].id = idElem[0].value;
        categories[i].action = action[0].value;
        categories[i].name = nameElem[0].value;
        categories[i].picture_id = colorElem[0].value;
    });

    books.setPostCategoryListSuccessCallback(submitSuccess);
    books.postCategoryList(categories);
};

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

    var inputGroup = document.createElement("div");
    inputGroup.setAttribute("class", "input-group");

    var name = document.createElement("input");
    name.setAttribute("id", "newCategoryName" + newCnt);
    name.setAttribute("name", "name");
    name.setAttribute("type", "text");
    name.setAttribute("value", "");
    name.setAttribute("title", "");
    name.setAttribute("class", "categoryName"
        + " form-control"
        + " btn"
        + " jscolor {valueElement:'newCategoryColor" + newCnt + "',value:'000000'}");

    var deleteS = document.createElement("span");
    deleteS.setAttribute("class", "input-group-addon btn categoryDelete");
    deleteS.innerHTML = "X";

    inputGroup.appendChild(name);
    inputGroup.appendChild(deleteS);
    div.appendChild(action);
    div.appendChild(color);
    div.appendChild(inputGroup);
    form.appendChild(id);
    form.appendChild(div);
    parent.appendChild(form);

    newCnt++;

    deleteCategoryBtn();
    jscolor.installByClassName("jscolor");
}


function deleteCategoryBtn() {
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
            btn.addClass("delete")
            name.val("mark delete");
            action.val("delete");
        }
    });
}

/************************** jquery functions ********************************/

$(document).ready(function () {
    deleteCategoryBtn();
});
