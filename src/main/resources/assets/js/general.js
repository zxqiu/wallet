/**
 * Created by zxqiu on 3/22/17.
 */

function getBrightness(rgb) {
    if (!rgb) {
        return;
    }
    return 0.2126 * parseInt(rgb[0])
        + 0.7152 * parseInt(rgb[1])
        + 0.0722 * parseInt(rgb[2]); // per ITU-R BT.709
}

function getBackgroundColorRGB(node) {
    if (!node) {
        return;
    }
    var rgb = node.style.backgroundColor.slice(
        node.style.backgroundColor.indexOf('(') + 1
        , node.style.backgroundColor.indexOf(')')
    );
    var rgb = rgb.split(", ");

    return rgb;
}

function adjustFontColor(brightness, node) {
    if (!node) {
        return;
    }
    if (brightness > 135) {
        node.style.color = "#4A4A4A";
    } else {
        node.style.color = "white";
    }
}

function setCategoryListFontColor() {
    var selector = $(".category-list");

    for (var i = 0; i < selector.length; i++) {
        var a = $(selector[i]).find("a");
        var rgb = getBackgroundColorRGB(selector[i]);
        if (a.length == 0 || rgb.length < 3) {
            continue;
        }

        var bright = getBrightness(rgb);
        adjustFontColor(bright, a[0]);
    }
}

function setCategoryOptionFontColor() {
    var selector = $(".category-option");
    for (var i = 0; i < selector.length; i++) {
        var rgb = getBackgroundColorRGB(selector[i]);
        var bright = getBrightness(rgb);

        adjustFontColor(bright, selector[i]);
    }

    var parent = selector.parent()[0];
    var rgb = getBackgroundColorRGB(parent);
    var bright = getBrightness(rgb);

    adjustFontColor(bright, parent);
}
/************************** jquery functions ********************************/
$(document).ready(function () {
    setCategoryListFontColor();
    setCategoryOptionFontColor();
});
