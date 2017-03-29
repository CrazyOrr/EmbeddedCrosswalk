function android2Js(content) {
    console.log('android2Js called with: ' + content);
    return 'Got it from JS'
}

function js2Android(content) {
    var ret = window.example.js2Android(content);
    console.log('js2Android returns: ' + ret);
}

/**
 @return {boolean} true:web页面已处理,false:web页面未处理
 */
function onBackPressed() {
    var checked = document.getElementById('handled').checked;
    console.log('onBackPressed() returns ' + checked);
    return checked
}