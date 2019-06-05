package com.nlapin.poseestimation;

import android.Manifest;
import android.app.Fragment;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Bundle;
import android.util.Log;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.CameraX;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageAnalysisConfig;
import androidx.camera.core.Preview;
import androidx.camera.core.PreviewConfig;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleRegistry;

import java.util.Arrays;
import java.util.List;

public class CameraScreen extends Fragment implements LifecycleOwner {

    private static final String TAG = "CameraScreen";

    public static final List<String> REQUIRED_PERMISSIONS = Arrays.asList(Manifest.permission.CAMERA);
    private TextureView cameraViewTextureV;
    private LifecycleRegistry lifecycleRegistry;
    public static final int REQUEST_CODE_PERMISSIONS = 42;

    private int rotation = 0;
    private Size previewConfigSize;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        LinearLayout mainView = new LinearLayout(getActivity());

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);

        mainView.setLayoutParams(layoutParams);

        cameraViewTextureV = new TextureView(getActivity());
        mainView.addView(cameraViewTextureV);

        return mainView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

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
        previewConfigSize = new Size(cameraViewTextureV.getWidth(), cameraViewTextureV.getHeight());
        PreviewConfig previewConfig = new PreviewConfig.Builder()
                .setTargetResolution(previewConfigSize)
                .build();

        Preview preview = new Preview(previewConfig);
        preview.setOnPreviewOutputUpdateListener(output -> {
            ViewGroup parent = (ViewGroup) cameraViewTextureV.getParent();
            parent.removeView(cameraViewTextureV);
            parent.addView(cameraViewTextureV, 0);

            cameraViewTextureV.setSurfaceTexture(output.getSurfaceTexture());
            updateTransform();
        });

        CameraX.bindToLifecycle(this, preview, buildImageAnalysisUseCase());
    }

    private ImageAnalysis buildImageAnalysisUseCase() {
        ImageAnalysisConfig analysisConfig = new ImageAnalysisConfig.Builder()
                .setTargetResolution(previewConfigSize)
                .build();

        ImageAnalysis analysis = new ImageAnalysis(analysisConfig);
        ImageBuffer imageBuffer = ImageBuffer.getInstance();

        analysis.setAnalyzer((image, rotationDegrees) -> {
            new Thread(() -> {
                Bitmap bitmap = cameraViewTextureV.getBitmap(
                        ImageBuffer.SIZE_OF_IMAGE,
                        ImageBuffer.SIZE_OF_IMAGE);

                imageBuffer.addFrame(bitmap);
            }).start();
        });

        return analysis;
    }

    private void updateTransform() {
        Matrix matrix = new Matrix();

        float centerX = cameraViewTextureV.getWidth() / 2f;
        float centerY = cameraViewTextureV.getHeight() / 2f;

        switch (cameraViewTextureV.getDisplay().getRotation()) {
            case Surface.ROTATION_0:
                rotation = 0;
                break;

            case Surface.ROTATION_90:
                rotation = 90;
                break;

            case Surface.ROTATION_180:
                rotation = 180;
                break;

            case Surface.ROTATION_270:
                rotation = 270;
                break;

            default:
                rotation = 0;
                break;
        }

        matrix.postRotate(-rotation, centerX, centerY);

        cameraViewTextureV.setTransform(matrix);
    }
}
