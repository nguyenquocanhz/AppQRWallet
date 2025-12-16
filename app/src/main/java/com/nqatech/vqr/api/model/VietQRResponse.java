package com.nqatech.vqr.api.model;

import com.google.gson.annotations.SerializedName;

public class VietQRResponse<T> {
    @SerializedName("code")
    private String code;
    
    @SerializedName("desc")
    private String desc;
    
    @SerializedName("data")
    private T data;

    public String getCode() { return code; }
    public String getDesc() { return desc; }
    public T getData() { return data; }
}