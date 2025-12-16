package com.nqatech.vqr.api.model;

import com.google.gson.annotations.SerializedName;

public class GenQRResponse {
    @SerializedName("qrCode")
    private String qrCode; // The raw QR string
    
    @SerializedName("qrDataURL")
    private String qrDataURL; // Base64 encoded image
    
    public String getQrCode() { return qrCode; }
    public String getQrDataURL() { return qrDataURL; }
}