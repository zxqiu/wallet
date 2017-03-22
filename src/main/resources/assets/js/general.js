/**
 * Created by zxqiu on 3/22/17.
 */

function getBrightness(rgb) {
    return 0.2126 * parseInt(rgb[0])
        + 0.7152 * parseInt(rgb[1])
        + 0.0722 * parseInt(rgb[2]); // per ITU-R BT.709
}

/************************** jquery functions ********************************/
$(document).ready(function () {
    var selector = $(".categorySelector");
    for (var i = 0; i < selector.length; i++) {
        var color = $(selector[i]).style.background;
        console.log(color);
    }
});
