var exec = require('cordova/exec');

const NATIVE_MODULE = "PoseEstimationAndroid";

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

function _execCallback(method, callback, args = []) {
    exec(function (...results) {
        callback && callback(...results);
    }, function (error) {
        callback && callback(null, error && error.error || error);
    }, NATIVE_MODULE, method, args);
}

const pluginProps = {
    showSkeleton: true,
    showBoundingBox: true,
    recordingState: 0
};

const poseEstimation = {
    CAMERA_FRONT: "front",
    CAMERA_BACK: "back",

    /**
     * Responsible for conversion quality of image from camera frames
     */
    CONVERSION_QUALITY_HIGH: "high",
    CONVERSION_QUALITY_MEDIUM: "medium",
    CONVERSION_QUALITY_LOW: "low",

    get showSkeleton() {
        return pluginProps.showSkeleton;
    },

    set showSkeleton(enabled) {
        pluginProps.showSkeleton = (enabled != null) ? enabled : true;
        _execCallback('showSkeleton', null, [pluginProps.showSkeleton]);
    },

    get showBoundingBox() {
        return pluginProps.showBoundingBox;
    },

    set showBoundingBox(enabled) {
        pluginProps.showBoundingBox = (enabled != null) ? enabled : true;
        _execCallback('showBoundingBox', null, [pluginProps.showBoundingBox]);
    },

    initPoseEstimation: function (params, callback) {
        _execCallback('init', callback, [params]);
    },

    startCapture: function (params, callback) {
        _clearBackground();
        _execCallback('startCapture', callback, [params]);
    },

    stopCapture: function (callback) {
        _execCallback('stopCapture', callback);
    },

    startRecording: function (callback) {
        _execCallback('startRecording', function (result, error) {
            pluginProps.recordingState = 1;
            callback && callback(result, error);
        });
    },

    stopRecording: function (callback) {
        _execCallback('stopRecording', function (result, error) {
            pluginProps.recordingState = 0;
            callback && callback(result, error);
        });
    },
};