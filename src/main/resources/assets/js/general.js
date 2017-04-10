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

function noBookWarning(bookList) {
    if ((!bookList || bookList.length == 0)
        && confirm("Add a new book first! Press OK to edit books.") == true) {
        window.location = "/books/booklist";
    }
}

/*
 -2: not jpeg
 -1: not defined
 */
function getOrientation(file, callback) {
    var reader = new FileReader();
    reader.onload = function(e) {

        var view = new DataView(e.target.result);
        if (view.getUint16(0, false) != 0xFFD8) return callback(-2);
        var length = view.byteLength, offset = 2;
        while (offset < length) {
            var marker = view.getUint16(offset, false);
            offset += 2;
            if (marker == 0xFFE1) {
                if (view.getUint32(offset += 2, false) != 0x45786966) return callback(-1);
                var little = view.getUint16(offset += 6, false) == 0x4949;
                offset += view.getUint32(offset + 4, little);
                var tags = view.getUint16(offset, little);
                offset += 2;
                for (var i = 0; i < tags; i++)
                    if (view.getUint16(offset + (i * 12), little) == 0x0112)
                        return callback(view.getUint16(offset + (i * 12) + 8, little));
            }
            else if ((marker & 0xFF00) != 0xFF00) break;
            else offset += view.getUint16(offset, false);
        }
        return callback(-1);
    };
    reader.readAsArrayBuffer(file);
}
