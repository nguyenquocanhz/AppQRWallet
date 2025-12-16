package com.nqatech.vqr.qr;

import android.graphics.ImageFormat;
import androidx.annotation.NonNull;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.PlanarYUVLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import java.nio.ByteBuffer;

public class QRCodeAnalyzer implements ImageAnalysis.Analyzer {

    private final MultiFormatReader reader;
    private final QRCodeListener listener;

    public interface QRCodeListener {
        void onQRCodeFound(String qrCode);
        void onQRCodeNotFound();
    }

    public QRCodeAnalyzer(QRCodeListener listener) {
        this.listener = listener;
        this.reader = new MultiFormatReader();
    }

    @Override
    public void analyze(@NonNull ImageProxy image) {
        if (image.getFormat() == ImageFormat.YUV_420_888) {
            ByteBuffer buffer = image.getPlanes()[0].getBuffer();
            byte[] data = new byte[buffer.remaining()];
            buffer.get(data);

            int width = image.getWidth();
            int height = image.getHeight();

            PlanarYUVLuminanceSource source = new PlanarYUVLuminanceSource(
                    data, width, height, 0, 0, width, height, false);
            BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

            try {
                Result result = reader.decode(bitmap);
                listener.onQRCodeFound(result.getText());
            } catch (NotFoundException e) {
                listener.onQRCodeNotFound();
            } finally {
                image.close();
            }
        } else {
            image.close();
        }
    }
}