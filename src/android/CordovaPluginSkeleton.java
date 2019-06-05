package com.nlapin.poseestimation;

import android.app.Activity;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.IdRes;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;

/**
 * This class echoes a string called from JavaScript.
 */
public class CordovaPluginSkeleton extends CordovaPlugin {

    private static final String TAG = "CordovaPluginSkeleton";

    private int containerViewId = 24;

    private Activity activity;

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        Log.i(TAG, "execute: start");
        if (action.equals("coolMethod")) {
            String message = args.getString(0);
            this.coolMethod(message, callbackContext);
            activity = cordova.getActivity();
            activity.runOnUiThread(() -> {
                FrameLayout containerView = activity.findViewById(containerViewId);
                if (containerView == null) {
                    containerView = new FrameLayout(activity.getApplicationContext());
                    containerView.setId(containerViewId);

                    webView.getView().setBackgroundColor(Color.TRANSPARENT);
                    webView.getView().setVisibility(View.INVISIBLE);

                    final FrameLayout.LayoutParams containerLayoutParams =
                            new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                                    FrameLayout.LayoutParams.MATCH_PARENT);

                    activity.addContentView(containerView, containerLayoutParams);

                    final CameraScreen cameraScreen = new CameraScreen();

                    activity.getFragmentManager()
                            .beginTransaction()
                            .add(getContainerViewId(), cameraScreen)
                            .commit();
                }
            });
            return true;
        }
        Log.i(TAG, "execute: end");
        return false;
    }

    private void coolMethod(String message, CallbackContext callbackContext) {
        if (message != null && message.length() > 0) {
            callbackContext.success(message);
        } else {
            callbackContext.error("Expected one non-empty string argument.");
        }
    }

    public @IdRes
    int getContainerViewId() {
        return containerViewId;
    }
}
