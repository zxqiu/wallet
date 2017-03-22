function entryFilter() {
    var entries = document.getElementById("booksList").children;
    var curYear = parseInt($("#yearShow").val());
    var curMonth = parseInt($("#monthShow").val());
    var curCategory = $("#categoryShow").text().trim();

    var categorySum = new Object();
    categorySum["All"] = 0.0;

    for (var i = 0; i < entries.length; i++) {
        var entryYear;
        var entryMonth;
        var entryCategory;
        var entryAmount;

        var entry = entries[i];
        var title = $(entry).find("#bookTitle")[0];
        title = $(title).text().split(" ");

        var d = title[0];
        d = d.split("-");

        entryYear = parseInt(d[0]);
        entryMonth = parseInt(d[1]);

        entryCategory = $(entry).find("#bookCategory")[0];
        entryCategory = $(entryCategory).text();
        entryCategory = entryCategory.split(":");
        if (entryCategory.length < 2) {
            return;
        }
        entryCategory = entryCategory[1].trim();

        entryAmount = parseFloat(title[1].substr(1, title[1].length - 1));

        if (entryYear == curYear && entryMonth == curMonth) {
            if (curCategory == "All" || entryCategory == curCategory) {
                $(entry).show();
            } else {
                $(entry).hide();
            }

            if (!(categorySum.hasOwnProperty(entryCategory))) {
                categorySum[entryCategory] = 0.0;
            }
            categorySum["All"] += entryAmount;
            categorySum[entryCategory] += entryAmount;
        } else {
            $(entry).hide();
        }
    }

    showCategorySum(categorySum);
}

function showCategorySum(categorySum) {
    var list = $("#categorySelector").find("a");
    for (var i = 0; i < list.length; i++) {
        if ($(list[i]).hasClass("dropdown-control")) {
            continue;
        }

        var categoryName = $(list[i]).text().split(":")[0];
        if (categorySum.hasOwnProperty(categoryName)) {
            list[i].innerHTML = categoryName + ":&emsp;$" + categorySum[categoryName].toFixed(2);
        } else {
            list[i].innerHTML = categoryName + ":&emsp;$0.00";
        }
    }
}

function logout() {
    FB.getLoginStatus(function(response) {
        if (response.status == "connected") {
            FB.logout(function (data) {
                console.log("facebook logout success");
                window.location.href = "/logout";
            });
        } else {
            window.location.href = "/logout";
        }
    });
}

/************************** jquery functions ********************************/
var monthNames = ["Jan", "Feb", "Mar", "Apr", "May", "Jun",
    "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
];

$(document).ready(function () {
    var titles = $(".books-list-text-title");
    for (var i = 0; i < titles.length; i++) {
        var rgb = getBackgroundColorRGB(titles[i]);
        var bright = getBrightness(rgb);

        if (bright > 135) {
            titles[i].style.color = "#4A4A4A";
        } else {
            titles[i].style.color = "white";
        }
    }


    var today = new Date();
    // set year
    $('#yearShow').val(today.getFullYear());
    $('#yearShow').text(today.getFullYear());
    $("#yearShow").addClass("yearChanged");
    // set month
    $('#monthShow').val(today.getMonth() + 1);
    $('#monthShow').text(monthNames[today.getMonth()]);

    entryFilter();
});


$('.books-list-text').on("click", function(e) {
    if ($(e.target).hasClass("dblclicked")) {
        window.location.replace("/books/entry/" + $(this).attr("title"));
        $(e.target).removeClass("dblclicked");
    } else {
        $(e.target).addClass("dblclicked");
        setTimeout(function() { $(e.target).removeClass("dblclicked"); }, 2000);
    }
});

$('#monthSelector li a').on('click', function(){
    for (var i = 0; i < monthNames.length; i++) {
        if ($(this).text() == monthNames[i] && $('#monthShow').val() != i + 1) {
            $('#monthShow').val(i + 1);
            $('#monthShow').text($(this).text());
            entryFilter();
            break;
        }
    }
});

$('#yearSelector li a').on('click', function() {
    var showYear = parseInt($("#yearShow").val());
    var newYear = parseInt($(this).text());

    if (showYear != newYear) {
        $("#yearShow").val(newYear);
        $("#yearShow").text(newYear);
        $("#yearShow").addClass("yearChanged");
        entryFilter();
    }
});

$('#yearShow').on('click', function() {
    if ($(this).hasClass("yearChanged")) {
        var li = document.getElementById("yearSelector").children;
        var year = parseInt($(this).val()) - li.length + 2;
        for (var i = 0; i < li.length; i++) {
            li[i].firstElementChild.text = year + i;
        }

        $(this).removeClass("yearChanged");
    }
});

$("#yearMinus").on("click", function () {
    var showYear = parseInt($("#yearShow").val());
    if (showYear > 0) {
        $("#yearShow").val(showYear - 1);
        $("#yearShow").text(showYear - 1);
        $("#yearShow").addClass("yearChanged");
        entryFilter();
    }
});

$("#yearPlus").on("click", function () {
    var showYear = parseInt($("#yearShow").val());
    $("#yearShow").val(showYear + 1);
    $("#yearShow").text(showYear + 1);
    $("#yearShow").addClass("yearChanged");
    entryFilter();
});

$("#monthMinus").on("click", function () {
    var showMonth = parseInt($("#monthShow").val());
    if (showMonth > 1) {
        $("#monthShow").val(showMonth - 1);
        $("#monthShow").text(monthNames[showMonth - 2]);
        entryFilter();
    }
});

$("#monthPlus").on("click", function () {
    var showMonth = parseInt($("#monthShow").val());
    if (showMonth < 12) {
        $("#monthShow").val(showMonth + 1);
        $("#monthShow").text(monthNames[showMonth]);
        entryFilter();
    }
});

$("#categorySelector li a").on("click", function () {
    var showCategory = $("#categoryShow").text();
    var newCategory = $(this).text().split(":")[0].trim();

    if (showCategory != newCategory) {
        $("#categoryShow").text(newCategory);
        entryFilter();
    }
});
