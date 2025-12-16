package com.nqatech.vqr.qr;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import com.google.common.util.concurrent.ListenableFuture;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class QRScannerHelper {

    private final Context context;
    private final PreviewView previewView;
    private final LifecycleOwner lifecycleOwner;
    private final QRCodeAnalyzer.QRCodeListener listener;
    private ExecutorService cameraExecutor;

    public QRScannerHelper(Context context, PreviewView previewView, LifecycleOwner lifecycleOwner, QRCodeAnalyzer.QRCodeListener listener) {
        this.context = context;
        this.previewView = previewView;
        this.lifecycleOwner = lifecycleOwner;
        this.listener = listener;
        this.cameraExecutor = Executors.newSingleThreadExecutor();
    }

    public void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(context);

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                bindPreview(cameraProvider);
            } catch (ExecutionException | InterruptedException e) {
                // Handle any errors (including cancellation) here.
                e.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(context));
    }

    private void bindPreview(@NonNull ProcessCameraProvider cameraProvider) {
        Preview preview = new Preview.Builder().build();
        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();

        preview.setSurfaceProvider(previewView.getSurfaceProvider());

        ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build();

        imageAnalysis.setAnalyzer(cameraExecutor, new QRCodeAnalyzer(listener));

        try {
            cameraProvider.unbindAll();
            cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, preview, imageAnalysis);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stopCamera() {
        if (cameraExecutor != null) {
            cameraExecutor.shutdown();
        }
    }
}