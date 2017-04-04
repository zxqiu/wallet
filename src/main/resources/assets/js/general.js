/**
 * Created by zxqiu on 3/22/17.
 */

var COLOR_LIST = ["#F0F8FF","#FAEBD7","#7FFFD4","#F0E68C","#ADD8E6","#F08080","#E0FFFF","#FFB6C1","#FFA07A","#87CEFA"
    ,"#800000","#DB7093","#DDA0DD","#40E0D0","#EE82EE","#9ACD32","#FFE4E1","#40E0D0","#AFEEEE","#98FB98","#ADFF2F"
    ,"#FFFFFF","#000000"];

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


