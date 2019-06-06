package com.nlapin.poseestimation;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.widget.FrameLayout;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;

/**
 * This class echoes a string called from JavaScript.
 */
public class CordovaPluginSkeleton extends CordovaPlugin {

    private int containerViewId = 24;
    private CameraScreen cameraScreen;

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("init")) {
            String message = args.getString(0);
            this.coolMethod(message, callbackContext);

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

                    cameraScreen = new CameraScreen();

                    activity.getFragmentManager()
                            .beginTransaction()
                            .add(containerViewId, cameraScreen)
                            .commit();
                }
            });
            return true;
        }
        return false;
    }

    private void coolMethod(String message, CallbackContext callbackContext) {
        if (message != null && message.length() > 0) {
            callbackContext.success(message);
        } else {
            callbackContext.error("Expected one non-empty string argument.");
        }
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
