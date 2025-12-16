package com.nqatech.vqr.database.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "recipients")
public class Recipient {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String bankName; // Format: ShortName - FullName
    public String bankCode; // e.g. VCB
    public String bin;      // e.g. 970436
    public String accountNumber;
    public String accountName;
    public String amount;
    public String content;
    public String qrDataURL; // Base64 of the QR image
    public long createdAt;

    public Recipient(String bankName, String bankCode, String bin, String accountNumber, String accountName, String amount, String content) {
        this.bankName = bankName;
        this.bankCode = bankCode;
        this.bin = bin;
        this.accountNumber = accountNumber;
        this.accountName = accountName;
        this.amount = amount;
        this.content = content;
        this.createdAt = System.currentTimeMillis();
    }
}