package com.nqatech.vqr.api;

import com.nqatech.vqr.api.model.Bank;
import com.nqatech.vqr.api.model.GenQRResponse;
import com.nqatech.vqr.api.model.VietQRResponse;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Streaming;
import java.util.List;

public interface VietQRService {

    @POST("v2/generate")
    Call<VietQRResponse<GenQRResponse>> generateQR(@Body VietQRRequest request);

    @GET("v2/banks")
    Call<VietQRResponse<List<Bank>>> getBanks();
    
    @Streaming
    @POST("generate-image-endpoint-placeholder") 
    Call<ResponseBody> generateQRImage(@Body VietQRRequest request);
}