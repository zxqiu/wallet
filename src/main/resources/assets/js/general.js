/**
 * Created by zxqiu on 3/22/17.
 */

function getBrightness(rgb) {
    return 0.2126 * parseInt(rgb[0])
        + 0.7152 * parseInt(rgb[1])
        + 0.0722 * parseInt(rgb[2]); // per ITU-R BT.709
}

function getBackgroundColorRGB(node) {
    var rgb = node.style.backgroundColor.slice(
        node.style.backgroundColor.indexOf('(') + 1
        , node.style.backgroundColor.indexOf(')')
    );
    var rgb = rgb.split(", ");

    return rgb;
}

function setCategorySelectorFontColor() {
    var selector = $(".category-selector");
    for (var i = 0; i < selector.length; i++) {
        var a = $(selector[i]).find("a");
        var rgb = getBackgroundColorRGB(selector[i]);
        if (a.length == 0 || rgb.length < 3) {
            continue;
        }

        var bright = getBrightness(rgb);

        if (bright > 135) {
            a[0].style.color = "#4A4A4A";
        } else {
            a[0].style.color = "white";
        }
    }
}

/************************** jquery functions ********************************/
$(document).ready(function () {
    setCategorySelectorFontColor();
});
