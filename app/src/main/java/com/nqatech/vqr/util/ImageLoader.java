package com.nqatech.vqr.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageView;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ImageLoader {
    private static final ExecutorService executor = Executors.newFixedThreadPool(4);
    private static final Handler handler = new Handler(Looper.getMainLooper());

    public static void load(ImageView imageView, String url) {
        if (url == null || url.isEmpty()) {
            imageView.setImageDrawable(null); // Or set placeholder
            return;
        }

        // Tag the ImageView to check if the URL matches when the image is loaded (for RecyclerView recycling)
        imageView.setTag(url);

        executor.execute(() -> {
            Bitmap bitmap = null;
            try {
                URL imageUrl = new URL(url);
                HttpURLConnection connection = (HttpURLConnection) imageUrl.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                bitmap = BitmapFactory.decodeStream(input);
            } catch (Exception e) {
                e.printStackTrace();
            }

            final Bitmap finalBitmap = bitmap;
            // Check tag on main thread
            handler.post(() -> {
                if (url.equals(imageView.getTag())) {
                    if (finalBitmap != null) {
                        imageView.setImageBitmap(finalBitmap);
                    } else {
                        // Set error placeholder if needed
                    }
                }
            });
        });
    }
}