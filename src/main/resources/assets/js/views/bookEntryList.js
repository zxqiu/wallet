function entryFilter() {
    var entries = document.getElementById("bookList").children;
    var curYear = parseInt($("#yearShow").val());
    var curMonth = parseInt($("#monthShow").val());
    var selected = $("#entryFilterSelector").find("option:selected");
    var curCategoryGroupID = new Array();
    var curBookGroupID = new Array();
    for (var i = 0; i < selected.length; i++) {
        var id = selected[i].id;
        if ($(selected[i]).hasClass("option-category")) {
            curCategoryGroupID.push(id);
        } else if ($(selected[i]).hasClass("option-book")) {
            curBookGroupID.push(id);
        }
    }

    var categorySum = new Object();
    var bookSum = new Object();
    categorySum["All"] = 0.0;
    bookSum["All"] = 0.0;

    for (var i = 0; i < entries.length; i++) {
        var entryYear;
        var entryMonth;
        var entryCategoryGroupID;
        var entryBookGroupID;
        var entryAmount;

        var entry = entries[i];
        var title = $(entry).find("#bookTitle")[0];
        title = $(title).text().split(" ");

        var d = title[0];
        d = d.split("-");

        entryYear = parseInt(d[0]);
        entryMonth = parseInt(d[1]);

        entryCategoryGroupID = $(entry).find("#bookCategoryGroupID")[0];
        entryCategoryGroupID = $(entryCategoryGroupID).val();

        entryBookGroupID = $(entry).find("#bookGroupID")[0];
        entryBookGroupID = $(entryBookGroupID).val();

        entryAmount = parseFloat(title[1].substr(1, title[1].length - 1));

        if (entryYear == curYear && entryMonth == curMonth) {
            var allIndex = curCategoryGroupID.indexOf("All");
            var allIndex = curBookGroupID.indexOf("All");
            if ((curCategoryGroupID.indexOf("All") != -1 || curCategoryGroupID.indexOf(entryCategoryGroupID) != -1)
                && (curBookGroupID.indexOf("All") != -1 || curBookGroupID.indexOf(entryBookGroupID) != -1)) {
                $(entry).show();
            } else {
                $(entry).hide();
            }

            if (!(categorySum.hasOwnProperty(entryCategoryGroupID))) {
                categorySum[entryCategoryGroupID] = 0.0;
            }
            categorySum["All"] += entryAmount;
            categorySum[entryCategoryGroupID] += entryAmount;

            if (!(bookSum.hasOwnProperty(entryBookGroupID))) {
                bookSum[entryBookGroupID] = 0.0;
            }
            bookSum["All"] += entryAmount;
            bookSum[entryBookGroupID] += entryAmount;
        } else {
            $(entry).hide();
        }
    }

    showSum(categorySum, bookSum);
}

function showSum(categorySum, bookSum) {
    var list = $("#entryFilterSelector").find("option");
    console.log(categorySum);
    console.log(bookSum);
    for (var i = 0; i < list.length; i++) {
        if ($(list[i]).hasClass("option-control")) {
            continue;
        }

        var name = $(list[i]).text().trim();
        console.log(name);
        if ($(list[i]).hasClass("option-category") && categorySum.hasOwnProperty(name)) {
            $(list[i]).attr("data-subtext", "$" + categorySum[name].toFixed(2));
        } else if ($(list[i]).hasClass("option-book") && bookSum.hasOwnProperty(name)) {
            $(list[i]).attr("data-subtext", "$" + bookSum[name].toFixed(2));
        } else {
            $(list[i]).attr("data-subtext", "$0.00");
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
    var titles = $(".book-list-text-title");
    for (var i = 0; i < titles.length; i++) {
        var rgb = getBackgroundColorRGB(titles[i]);
        var bright = getBrightness(rgb);

        adjustFontColor(bright, titles[i]);
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


$('.book-list-text').on("click", function(e) {
    if ($(e.target).hasClass("dblclicked")) {
        window.location.replace("/books/entry/" + $(this).attr("title"));
        $(e.target).removeClass("dblclicked");
    } else {
        $(e.target).addClass("dblclicked");
        setTimeout(function() { $(e.target).removeClass("dblclicked"); }, 1500);
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

$("#entryFilterSelector").on("changed.bs.select", function (e, clickedIndex, newValue, oldValue) {
    var curSelect = $(this).find('option').eq(clickedIndex);
    if (curSelect.hasClass("option-control")) {
        window.location.href = $(curSelect).val();
    }
    entryFilter();
})
