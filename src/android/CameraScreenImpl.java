//package com.nlapin.poseestimation;
//
//import android.Manifest;
//import android.annotation.SuppressLint;
//import android.content.pm.PackageManager;
//import android.graphics.Bitmap;
//import android.graphics.SurfaceTexture;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.TextureView;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.camera.core.CameraX;
//import androidx.camera.core.ImageAnalysis;
//import androidx.camera.core.ImageAnalysisConfig;
//import androidx.camera.core.Preview;
//import androidx.camera.core.PreviewConfig;
//import androidx.camera.core.VideoCapture;
//import androidx.camera.core.VideoCaptureConfig;
//import androidx.core.app.ActivityCompat;
//import androidx.core.content.ContextCompat;
//import androidx.lifecycle.Lifecycle;
//import androidx.lifecycle.LifecycleOwner;
//import androidx.lifecycle.LifecycleRegistry;
//
//import java.io.File;
//import java.util.Arrays;
//import java.util.Collections;
//import java.util.List;
//import java.util.concurrent.TimeUnit;
//
//import io.reactivex.BackpressureStrategy;
//import io.reactivex.Flowable;
//import io.reactivex.FlowableOnSubscribe;
//import io.reactivex.disposables.CompositeDisposable;
//import io.reactivex.disposables.Disposable;
//import io.reactivex.schedulers.Schedulers;
//
//public class CameraScreenImpl extends CameraScreen implements LifecycleOwner {
//
//    private static final String TAG = "CameraScreenImpl";
//    public static final int REQUEST_CODE_PERMISSIONS = 42;
//    public static final List<String> REQUIRED_PERMISSIONS = Arrays.asList(
//            Manifest.permission.CAMERA);
//
//    private TextureView cameraViewTextureV;
//
//    private LifecycleRegistry lifecycleRegistry;
//    private ImageBuffer imageBuffer;
//
//    private Flowable<Bitmap> bitmapFlowable;
//    private CompositeDisposable disposables;
//    private VideoCapture videoCapture;
//
//    @Nullable
//    @Override
//    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
//        Log.d(TAG, "onCreateView: ");
//        final View view = inflater.inflate(R.layout.camera_view, container, false);
//        cameraViewTextureV = view.findViewById(R.id.cameraPreview);
//
//        imageBuffer = ImageBuffer.getInstance();
//        return view;
//    }
//
//    @Override
//    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//        disposables = new CompositeDisposable();
//        Log.d(TAG, "onViewCreated: ");
//
//        if (allPermissionsGranted()) {
//            startCamera();
//        } else {
//            ActivityCompat.requestPermissions(getActivity(),
//                    REQUIRED_PERMISSIONS.toArray(new String[0]),
//                    REQUEST_CODE_PERMISSIONS);
//        }
//    }
//
//    @Override
//    public void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        lifecycleRegistry = new LifecycleRegistry(this);
//        lifecycleRegistry.markState(Lifecycle.State.CREATED);
//    }
//
//    @Override
//    public void onStart() {
//        super.onStart();
//        lifecycleRegistry.markState(Lifecycle.State.STARTED);
//    }
//
//    @Override
//    public void onResume() {
//        super.onResume();
//        lifecycleRegistry.markState(Lifecycle.State.RESUMED);
////        disposables.add(updateSubscription());
//    }
//
//    @Override
//    public void onStop() {
//        super.onStop();
//        disposables.dispose();
//        imageBuffer.clear();
//    }
//
//    @Override
//    public void onDestroy() {
//        try {
//            super.onDestroy();
//            lifecycleRegistry.markState(Lifecycle.State.DESTROYED);
//            SurfaceTexture surfaceTexture = cameraViewTextureV.getSurfaceTexture();
//            if (surfaceTexture != null)
//                surfaceTexture.release();
//        } catch (RuntimeException e) {
//            //Ignoring stupid cordova logic
//        }
//    }
//
//    @NonNull
//    @Override
//    public Lifecycle getLifecycle() {
//        return lifecycleRegistry;
//    }
//
//    private boolean allPermissionsGranted() {
//        for (String permission : REQUIRED_PERMISSIONS) {
//            if (ContextCompat.checkSelfPermission(
//                    getActivity(), permission) != PackageManager.PERMISSION_GRANTED) {
//                return false;
//            }
//        }
//        Log.i(TAG, "allPermissionsGranted");
//        return true;
//    }
//
//    private void startCamera() {
//        PreviewConfig previewConfig = new PreviewConfig.Builder()
//                .setLensFacing(PluginConfig.CAMERA_LENS.getValue())
//                .build();
//
//        Preview preview = new Preview(previewConfig);
//        preview.setOnPreviewOutputUpdateListener(output -> cameraViewTextureV
//                .setSurfaceTexture(output.getSurfaceTexture()));
//
//        CameraX.bindToLifecycle(this, preview, buildVideoRecordingConfig(), buildImageAnalysisUseCase());
//    }
//
//    @SuppressLint("RestrictedApi")
//    private VideoCapture buildVideoRecordingConfig() {
//        VideoCaptureConfig captureConfig = new VideoCaptureConfig.Builder()
//                .setLensFacing(PluginConfig.CAMERA_LENS.getValue())
//                .build();
//
//        videoCapture = new VideoCapture(captureConfig);
//        return videoCapture;
//    }
//
//    private ImageAnalysis buildImageAnalysisUseCase() {
//        ImageAnalysisConfig analysisConfig = new ImageAnalysisConfig.Builder()
//                .setLensFacing(PluginConfig.CAMERA_LENS.getValue())
//                .build();
//
//        ImageAnalysis analysis = new ImageAnalysis(analysisConfig);
//        bitmapFlowable = Flowable.create((FlowableOnSubscribe<Bitmap>) emitter -> {
//            analysis.setAnalyzer((image, rotationDegrees) -> {
//                Bitmap bitmap = cameraViewTextureV.getBitmap(
//                        ImageBuffer.SIZE_OF_IMAGE,
//                        ImageBuffer.SIZE_OF_IMAGE);
//
//                emitter.onNext(bitmap);
//            });
//        }, BackpressureStrategy.BUFFER)
//                .debounce(40, TimeUnit.MILLISECONDS)
//                .subscribeOn(Schedulers.io())
//                .observeOn(Schedulers.io());
//
//        Disposable disposable = updateSubscription();
//
//        disposables.add(disposable);
//        return analysis;
//    }
//
//    private Disposable updateSubscription() {
//        return bitmapFlowable
//                .subscribe(imageBuffer::addFrame);
//    }
//
//    @Override
//    void initCamera(CameraCallback callback) {
//
//    }
//
//    @Override
//    public boolean showSkeleton(boolean enable) {
//        return false;
//    }
//
//    @Override
//    public boolean isSkeletonShowing() {
//        return false;
//    }
//
//    @Override
//    public boolean showBoundingBox(boolean enable) {
//        return false;
//
//    }
//
//    @Override
//    public boolean isBoundingBoxShowing() {
//        return false;
//    }
//
//    @Override
//    public void startCapture(int nmsRadius, int outputStride, float scoreTreshold, float minConfidence) {
//
//    }
//
//    @Override
//    public void stopCapture() {
//
//    }
//
//    @SuppressLint("RestrictedApi")
//    @Override
//    public void startRecording() {
//        File fileToSave = new File(
//                getActivity().getFilesDir(),
//                "poseEstimationVideo_" + System.currentTimeMillis() / 100F);
//        videoCapture.startRecording(fileToSave, new VideoCapture.OnVideoSavedListener() {
//            @Override
//            public void onVideoSaved(File file) {
//                Log.i(TAG, "Video file - " + file + " saved");
//            }
//
//            @Override
//            public void onError(VideoCapture.UseCaseError useCaseError, String message, @Nullable Throwable cause) {
//                Log.e(TAG, "Saving video error: " + message, cause);
//            }
//        });
//    }
//
//    @SuppressLint("RestrictedApi")
//    @Override
//    public void stopRecording() {
//        videoCapture.stopRecording();
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        if (requestCode == REQUEST_CODE_PERMISSIONS) {
//            if (allPermissionsGranted()) {
//                startCamera();
//            } else {
//                Toast.makeText(getActivity(),
//                        "Permissions not granted by the user.",
//                        Toast.LENGTH_SHORT).show();
//                getActivity().finish();
//            }
//        }
//    }
//}
