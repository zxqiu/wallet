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
        var idElem = forms[i].getElementsByClassName("bookID");
        var action = forms[i].getElementsByClassName("bookAction");
        var nameElem = forms[i].getElementsByClassName("bookName");
        var colorElem = forms[i].getElementsByClassName("bookColor");

        categories[i] = new Object();
        categories[i].id = idElem[0].value;
        categories[i].action = action[0].value;
        categories[i].name = nameElem[0].value;
        categories[i].picture_id = colorElem[0].value;
    });

    api.setPostBookListSuccessCallback(submitSuccess);
    api.postBookList(categories);
};

var newCnt = 0;
function newBook() {
    var parent = document.getElementById("bookList");

    var form = document.createElement("form");
    form.setAttribute("role", "form");
    form.setAttribute("id", "bookForm");

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


function initDeleteBookBtn() {
    $(".bookDelete").click(function (e) {
        var btn = $(e.target);
        var parent = btn.parent();
        var grandparent = parent.parent();
        var name = parent.find("input");
        var action = grandparent.find(".bookAction");

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
            swatches: ["#D50000","#304FFE","#00B8D4","#00C853","#FFD600","#FF6D00","#FF1744","#3D5AFE","#00E5FF","#00E676","#FFEA00","#FF9100","#FF5252","#536DFE","#18FFFF","#69F0AE","#FFFF00","#FFAB40"],
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
    initDeleteBookBtn();
    initBookColorBtn();
});

$('#bookShareForm').ajaxForm({
    url : '/t/toshortbyform',
    success : function (response) {
        console.log(response);
        var form = document.getElementById("bookShareForm");
        while (form.children.length > 0) {
            form.removeChild(form.firstChild);
        }

        var shortUrl = document.createElement("input");
        shortUrl.setAttribute("disabled", "true");
        shortUrl.setAttribute("type", "text");
        shortUrl.setAttribute("value", window.location.host + "/t/tolong/" + response.short_url);
        shortUrl.setAttribute("class", "form-control");

        form.appendChild(shortUrl);
    }
});

var form = document.getElementById("bookShareForm");
form.noValidate = true;
$("#bookShareFormSubmit").on("click", function() {
    for (var i = 0; i < form.length; i++) {
        if (!form[i].checkValidity()) {
            $("input").each(function () {
                if ($(this).prop("required") == true && $(this).val() == "") {
                    $(this).parent().parent().addClass("has-error");
                }
            });
            return;
        }
    }

    form.submit();
});
