package com.nqatech.vqr.database.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "notification_history")
public class NotificationHistory {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String packageName;
    public String title;
    public String content;
    public double amount;
    public long timestamp;

    public NotificationHistory(String packageName, String title, String content, double amount, long timestamp) {
        this.packageName = packageName;
        this.title = title;
        this.content = content;
        this.amount = amount;
        this.timestamp = timestamp;
    }
}