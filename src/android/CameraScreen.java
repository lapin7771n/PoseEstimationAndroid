package com.nlapin.poseestimation;

import android.app.Fragment;

public abstract class CameraScreen extends Fragment {

    static final String TAG = "CameraScreen";

    abstract void initCamera(CameraCallback callback);

    abstract boolean showSkeleton(boolean enable);

    abstract boolean isSkeletonShowing();

    abstract boolean showBoundingBox(boolean enable);

    abstract boolean isBoundingBoxShowing();

    abstract void startCapture(int nmsRadius, int outputStride, float scoreTreshold, float minConfidence);

    abstract void stopCapture();

    abstract void startRecording();

    abstract void stopRecording();
}
