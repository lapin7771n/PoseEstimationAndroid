package com.nlapin.poseestimation;

public abstract class VideoRecording {

    static final String TAG = "VideoRecording";

    abstract void startRecording();

    abstract void stopRecording();
}
