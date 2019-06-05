package android.cordovapluginskeleton.utils;

import android.graphics.Bitmap;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.PriorityQueue;

public class ImageBuffer {

    private static final String TAG = ImageBuffer.class.getCanonicalName();
    public static final int SIZE_OF_IMAGE = 513;

    /**
     * This collection contains all images from camera
     */
    private final PriorityQueue<ByteBuffer> imageBuffer = new PriorityQueue<>();

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

    public ByteBuffer get() {
        return imageBuffer.poll();
    }

    public void addFrame(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] array = stream.toByteArray();
        Log.d(TAG, "addFrame: " + Arrays.toString(array));
        imageBuffer.add(ByteBuffer.wrap(array));
    }

    private ImageBuffer() {
    }
}
