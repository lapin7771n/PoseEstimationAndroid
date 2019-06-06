package com.nlapin.poseestimation;

import android.graphics.Bitmap;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.PriorityQueue;

public class ImageBuffer {

    static final int SIZE_OF_IMAGE = 513;

    /**
     * This collection contains all frames from camera from camera
     *
     * @see PriorityQueue
     */
    private final PriorityQueue<ByteBuffer> frameBuffer = new PriorityQueue<>();

    private static volatile ImageBuffer mInstance;

    public static ImageBuffer getInstance() {
        if (mInstance == null) {
            synchronized (ImageBuffer.class) {
                if (mInstance == null) {
                    mInstance = new ImageBuffer();
                }
            }
        }
        return mInstance;
    }

    /**
     * Gets first element from queue
     *
     * @return first method from queue with frames
     * @see PriorityQueue
     * @see ByteBuffer
     */
    public ByteBuffer get() {
        return frameBuffer.poll();
    }

    /**
     * Adds frame to queue
     *
     * @param bitmap frame from camera
     * @see Bitmap
     * @see PriorityQueue
     */
    public void addFrame(Bitmap bitmap) {
        if (bitmap == null) return;
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] array = stream.toByteArray();
        frameBuffer.add(ByteBuffer.wrap(array));
    }

    private ImageBuffer() {
    }
}
