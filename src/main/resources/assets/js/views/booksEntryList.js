function getBrightness(rgb) {
    return 0.2126 * parseInt(rgb[0])
        + 0.7152 * parseInt(rgb[1])
        + 0.0722 * parseInt(rgb[2]); // per ITU-R BT.709
}

/************************** jquery functions ********************************/
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


    var monthNames = ["January", "February", "March", "April", "May", "June",
        "July", "August", "September", "October", "November", "December"
    ];
    var today = new Date();
    $('#monthShow').val(monthNames[today.getMonth()]);
    $('#monthShow').text(monthNames[today.getMonth()]);
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
    $('#monthShow').val($(this).text());
    $('#monthShow').text($(this).text());
});
