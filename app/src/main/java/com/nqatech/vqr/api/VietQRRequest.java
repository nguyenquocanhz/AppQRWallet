package com.nqatech.vqr.api;

public class VietQRRequest {
    private String accountNo;
    private String accountName;
    private int acqId; // Bank ID (Bin)
    private int amount;
    private String addInfo;
    private String format; // "text" or "qr_image"
    private String template; // "compact", "qr_only", etc.

    public VietQRRequest(String accountNo, String accountName, int acqId, int amount, String addInfo, String format, String template) {
        this.accountNo = accountNo;
        this.accountName = accountName;
        this.acqId = acqId;
        this.amount = amount;
        this.addInfo = addInfo;
        this.format = format;
        this.template = template;
    }

    public String getAccountNo() {
        return accountNo;
    }

    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public int getAcqId() {
        return acqId;
    }

    public void setAcqId(int acqId) {
        this.acqId = acqId;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getAddInfo() {
        return addInfo;
    }

    public void setAddInfo(String addInfo) {
        this.addInfo = addInfo;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }
}