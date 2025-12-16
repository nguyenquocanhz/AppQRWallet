package com.nqatech.vqr.api;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VietQRRepository {

    private final VietQRService service;

    public VietQRRepository() {
        this.service = ApiClient.getService();
    }

    public void generateQRImage(VietQRRequest request, final ImageCallback callback) {
        Call<ResponseBody> call = service.generateQRImage(request);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Convert stream to Bitmap
                    Bitmap bitmap = BitmapFactory.decodeStream(response.body().byteStream());
                    callback.onSuccess(bitmap);
                } else {
                    callback.onError("Failed to generate QR: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public interface ImageCallback {
        void onSuccess(Bitmap bitmap);
        void onError(String error);
    }
}