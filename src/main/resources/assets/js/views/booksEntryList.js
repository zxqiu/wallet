function getBrightness(rgb) {
    return 0.2126 * parseInt(rgb[0])
        + 0.7152 * parseInt(rgb[1])
        + 0.0722 * parseInt(rgb[2]); // per ITU-R BT.709
}

function showByMonth() {
    var entries = document.getElementById("booksList").children;

    for (var i = 0; i < entries.length; i++) {
        var entry = entries[i];
        var title = $(entry).find("#bookTitle")[0];
        title = $(title).text();

        var d = title.split(" ")[0];
        d = d.split("-");

        var entryYear = parseInt(d[0]);
        var entryMonth = parseInt(d[1]);

        var curYear = parseInt($("#yearShow").val());
        var curMonth = parseInt($("#monthShow").val());

        if (entryYear == curYear && entryMonth == curMonth) {
            $(entry).show();
        } else {
            $(entry).hide();
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
        var rgb = titles[i].style.backgroundColor.slice(
            titles[i].style.backgroundColor.indexOf('(') + 1
            , titles[i].style.backgroundColor.indexOf(')')
        );
        var rgb = rgb.split(", ");
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

    showByMonth();
});


$('.books-list-text').on("click", function(e) {
    if ($(e.target).hasClass("dblclicked")) {
        window.location.replace("/books/entry/" + $(this).attr("title"));
        $(e.target).removeClass("dblclicked");
    } else {
        $(e.target).addClass("dblclicked");
        setTimeout(function() { $(e.target).removeClass("dblclicked"); }, 1000);
    }
});

$('#monthSelector li a').on('click', function(){
    for (var i = 0; i < monthNames.length; i++) {
        if ($(this).text() == monthNames[i] && $('#monthShow').val() != i + 1) {
            $('#monthShow').val(i + 1);
            $('#monthShow').text($(this).text());
            showByMonth();
        }
    }
});

$('#yearSelector li a').on('click', function() {
    var showYear = parseInt($(this).val());
    var newYear = parseInt($(this).text());

    if (showYear != newYear) {
        $("#yearShow").val(newYear);
        $("#yearShow").text(newYear);
        $("#yearShow").addClass("yearChanged");
        showByMonth();
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
    $("#yearShow").val(showYear - 1);
    $("#yearShow").text(showYear - 1);
    $("#yearShow").addClass("yearChanged");
    showByMonth();
})

$("#yearPlus").on("click", function () {
    var showYear = parseInt($("#yearShow").val());
    $("#yearShow").val(showYear + 1);
    $("#yearShow").text(showYear + 1);
    $("#yearShow").addClass("yearChanged");
    showByMonth();
})

$("#monthMinus").on("click", function () {
    var showMonth = parseInt($("#monthShow").val());
    $("#monthShow").val(showMonth - 1);
    $("#monthShow").text(monthNames[showMonth - 1]);
    showByMonth();
})

$("#monthPlus").on("click", function () {
    var showMonth = parseInt($("#monthShow").val());
    $("#monthShow").val(showMonth + 1);
    $("#monthShow").text(monthNames[showMonth + 1]);
    showByMonth();
})
