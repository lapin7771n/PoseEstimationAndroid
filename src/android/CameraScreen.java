package com.nlapin.poseestimation;

import android.Manifest;
import android.app.Fragment;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Rational;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.CameraX;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageAnalysisConfig;
import androidx.camera.core.Preview;
import androidx.camera.core.PreviewConfig;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleRegistry;

import java.util.Collections;
import java.util.List;

public class CameraScreen extends Fragment implements LifecycleOwner {

    private static final String TAG = "CameraScreen";

    public static final List<String> REQUIRED_PERMISSIONS = Collections.singletonList(
                                                                Manifest.permission.CAMERA);
    private TextureView cameraViewTextureV;
    private LifecycleRegistry lifecycleRegistry;
    public static final int REQUEST_CODE_PERMISSIONS = 42;

    private Size previewConfigSize;
    private Rational rational;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: ");
        final View view = inflater.inflate(R.layout.camera_view, container, false);
        cameraViewTextureV = view.findViewById(R.id.cameraPreview);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onViewCreated: ");
        if (allPermissionsGranted()) {
            startCamera();
        } else {
            ActivityCompat.requestPermissions(getActivity(),
                    REQUIRED_PERMISSIONS.toArray(new String[0]),
                    REQUEST_CODE_PERMISSIONS);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        lifecycleRegistry = new LifecycleRegistry(this);
        lifecycleRegistry.markState(Lifecycle.State.CREATED);
    }

    @Override
    public void onStart() {
        super.onStart();
        lifecycleRegistry.markState(Lifecycle.State.STARTED);
    }

    @Override
    public void onResume() {
        super.onResume();
        lifecycleRegistry.markState(Lifecycle.State.RESUMED);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        lifecycleRegistry.markState(Lifecycle.State.DESTROYED);
    }

    @NonNull
    @Override
    public Lifecycle getLifecycle() {
        return lifecycleRegistry;
    }

    private boolean allPermissionsGranted() {
        for (String permission : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(
                    getActivity(), permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        Log.i(TAG, "allPermissionsGranted");
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera();
            } else {
                Toast.makeText(getActivity(),
                        "Permissions not granted by the user.",
                        Toast.LENGTH_SHORT).show();
                getActivity().finish();
            }
        }
    }

    private void startCamera() {
        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getRealMetrics(metrics);
        previewConfigSize = new Size(metrics.widthPixels, metrics.heightPixels);
        rational = new Rational(metrics.widthPixels, metrics.heightPixels);

        PreviewConfig previewConfig = new PreviewConfig.Builder()
                .setTargetResolution(previewConfigSize)
                .setTargetAspectRatio(rational)
                .setTargetRotation(cameraViewTextureV.getDisplay().getRotation())
                .build();

        Preview preview = new Preview(previewConfig);
        preview.setOnPreviewOutputUpdateListener(output -> cameraViewTextureV
                .setSurfaceTexture(output.getSurfaceTexture()));

        CameraX.bindToLifecycle(this, preview, buildImageAnalysisUseCase());
    }

    private ImageAnalysis buildImageAnalysisUseCase() {
        ImageAnalysisConfig analysisConfig = new ImageAnalysisConfig.Builder()
                .setTargetResolution(previewConfigSize)
                .setTargetAspectRatio(rational)
                .build();

        ImageAnalysis analysis = new ImageAnalysis(analysisConfig);
        ImageBuffer imageBuffer = ImageBuffer.getInstance();

        analysis.setAnalyzer((image, rotationDegrees) ->
                new Thread(() -> {
                    Bitmap bitmap = cameraViewTextureV.getBitmap(
                            ImageBuffer.SIZE_OF_IMAGE,
                            ImageBuffer.SIZE_OF_IMAGE);

                    imageBuffer.addFrame(bitmap);
                }).start());

        return analysis;
    }
}
