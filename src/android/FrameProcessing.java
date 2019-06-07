package com.nlapin.poseestimation;

public abstract class FrameProcessing {

    static final String TAG = "FrameProcessing";

    abstract void startProcessing();

    abstract void stopProcessing();
}
