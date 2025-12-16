package com.nqatech.vqr.api.model;

import com.google.gson.annotations.SerializedName;

public class Bank {
    @SerializedName("id")
    private int id;
    
    @SerializedName("name")
    private String name;
    
    @SerializedName("code")
    private String code;
    
    @SerializedName("bin")
    private String bin;
    
    @SerializedName("shortName")
    private String shortName;
    
    @SerializedName("logo")
    private String logo;

    public int getId() { return id; }
    public String getName() { return name; }
    public String getCode() { return code; }
    public String getBin() { return bin; }
    public String getShortName() { return shortName; }
    public String getLogo() { return logo; }
    
    @Override
    public String toString() {
        return shortName + " - " + name;
    }
}