var exec = require('cordova/exec');

function _clearBackground() {
    const body = document.body;
    if (body.style) {
        body.style.backgroundColor = 'rgba(0,0,0,0.01)';
        body.style.backgroundImage = '';

        setTimeout(function () {
            body.style.backgroundColor = 'transparent';
        }, 1);

        if (body.parentNode && body.parentNode.style) {
            body.parentNode.style.backgroundColor = 'transparent';
            body.parentNode.style.backgroundImage = '';
        }
    }
}

exports.coolMethod = function (arg0, success, error) {
    _clearBackground();
    exec(success, error, 'PoseEstimationAndroid', 'coolMethod', [arg0]);
};
