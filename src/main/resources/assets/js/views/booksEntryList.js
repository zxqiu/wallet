/************************** jquery functions ********************************/
$('.books-list-text').on("click", function(e) {
    if ($(e.target).hasClass("dblclicked")) {
        console.log("double clicked");

        console.log($(this).attr("title"));
        window.location.replace("/books/entry/" + $(this).attr("title"));

        $(e.target).removeClass("dblclicked");
    } else {
        $(e.target).addClass("dblclicked");
        setTimeout(function() { $(e.target).removeClass("dblclicked"); }, 1000);
    }
});
