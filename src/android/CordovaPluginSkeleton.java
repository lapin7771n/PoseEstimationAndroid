package com.nlapin.poseestimation;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.widget.FrameLayout;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.Timer;
import java.util.TimerTask;

/**
 * This class echoes a string called from JavaScript.
 */
public class CordovaPluginSkeleton extends CordovaPlugin {

    private static final String INIT = "init";
    private static final String SHOW_SKELETON = "showSkeleton";
    private static final String SHOW_BOUNDING_BOX = "showBoundingBox";
    private static final String START_CAPTURE = "startCapture";
    private static final String NMS_RADIUS = "nmsRadius";
    private static final String OUTPUT_STRIDE = "outputStride";
    private static final String SCORE_TRESHOLD = "scoreTreshold";
    private static final String MIN_CONFIDENCE = "minConfidence";
    private static final String STOP_CAPTURE = "stopCapture";
    private static final String START_RECORDING = "startRecording";
    private static final String STOP_RECORDING = "stopRecording";

    private static final String UNKNOWN_METHOD_ERROR = "Unknown method. Please check if this method exists.";

    private int containerViewId = 24;
    private CameraScreen cameraScreen;

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        JSONObject params = new JSONObject();
//        if (args != null)
//            params = (JSONObject) args.get(0);
        switch (action) {
            case INIT: {
                return initPlugin();
            }

            case SHOW_SKELETON: {
                boolean showSkeleton = params.getBoolean(SHOW_SKELETON);
                boolean status = cameraScreen.showSkeleton(showSkeleton);

                PluginResult pluginResult = buildOkCallbackResult(status, SHOW_SKELETON);

                callbackContext.sendPluginResult(pluginResult);
                return status;
            }

            case SHOW_BOUNDING_BOX: {
                boolean showBoundingBox = params.getBoolean(SHOW_BOUNDING_BOX);
                boolean status = cameraScreen.showBoundingBox(showBoundingBox);

                PluginResult pluginResult = buildOkCallbackResult(status, SHOW_BOUNDING_BOX);

                callbackContext.sendPluginResult(pluginResult);
                return status;
            }

            case START_CAPTURE: {
                int nmsRadius = params.getInt(NMS_RADIUS);
                int outputStride = params.getInt(OUTPUT_STRIDE);
                float scoreTreshold = BigDecimal.valueOf(
                        params.getDouble(SCORE_TRESHOLD)).floatValue();

                float minConfidence = BigDecimal.valueOf(
                        params.getDouble(MIN_CONFIDENCE)).floatValue();

                cameraScreen.startCapture(nmsRadius, outputStride, scoreTreshold, minConfidence);

                PluginResult pluginResult = buildOkCallbackResult(true, START_CAPTURE);
                callbackContext.sendPluginResult(pluginResult);
                return true;
            }

            case STOP_CAPTURE: {
                cameraScreen.stopCapture();

                PluginResult pluginResult = buildOkCallbackResult(true, STOP_CAPTURE);
                callbackContext.sendPluginResult(pluginResult);
                return true;
            }

            case START_RECORDING: {
                if (cameraScreen == null) {
                    PluginResult pluginResult = buildErrorCallbackResult(
                            "Camera is not ready yet",
                            START_RECORDING);
                    callbackContext.sendPluginResult(pluginResult);
                    return true;
                }
                cameraScreen.startRecording();

                PluginResult pluginResult = buildOkCallbackResult(true, START_RECORDING);
                callbackContext.sendPluginResult(pluginResult);
                return true;
            }

            case STOP_RECORDING: {
                cameraScreen.stopRecording();

                PluginResult pluginResult = buildOkCallbackResult(true, STOP_RECORDING);
                callbackContext.sendPluginResult(pluginResult);
                return true;
            }

            default:
                PluginResult pluginResult = buildErrorCallbackResult(UNKNOWN_METHOD_ERROR, null);
                callbackContext.sendPluginResult(pluginResult);
                return true;
        }
    }

    private PluginResult buildOkCallbackResult(boolean status, String methodName) throws JSONException {
        JSONObject object = new JSONObject();
        object.put("method", methodName);
        object.put("success", status);
        return new PluginResult(PluginResult.Status.OK, object);
    }

    private PluginResult buildErrorCallbackResult(String errorMessage, String methodName) throws JSONException {
        JSONObject object = new JSONObject();
        object.put("method", methodName);
        object.put("message", errorMessage);
        return new PluginResult(PluginResult.Status.ERROR, object);
    }


    private boolean initPlugin() {
        Activity activity = cordova.getActivity();

        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        activity.runOnUiThread(() -> {
            FrameLayout containerView = activity.findViewById(containerViewId);
            if (containerView == null) {
                containerView = new FrameLayout(activity.getApplicationContext());
                containerView.setId(containerViewId);

                final FrameLayout.LayoutParams containerLayoutParams =
                        new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                                FrameLayout.LayoutParams.MATCH_PARENT);

                activity.addContentView(containerView, containerLayoutParams);

                //web view to front
                webView.getView().setBackgroundColor(Color.TRANSPARENT);
                webView.getView().bringToFront();

                cameraScreen = new Camera2Screen();
                cameraScreen.initCamera(() -> cameraScreen.startRecording());

                TimerTask timerTask = new TimerTask() {
                    @Override
                    public void run() {
                        cameraScreen.stopRecording();
                    }
                };

                Timer timer = new Timer(true);
                timer.schedule(timerTask, 3000);

                activity.getFragmentManager()
                        .beginTransaction()
                        .add(containerViewId, cameraScreen)
                        .commit();
            }
        });
        return true;
    }

    @Override
    public void onPause(boolean multitasking) {
        super.onPause(multitasking);
        cameraScreen.onPause();
    }

    @Override
    public void onResume(boolean multitasking) {
        super.onResume(multitasking);
        cameraScreen.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        cameraScreen.onDestroy();
    }

    @Override
    public void onStop() {
        super.onStop();
        cameraScreen.onStop();
    }
}
