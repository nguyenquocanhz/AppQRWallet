package com.nqatech.vqr.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.nqatech.vqr.database.entity.NotificationHistory;

import java.util.List;

@Dao
public interface NotificationHistoryDao {
    @Insert
    void insert(NotificationHistory notificationHistory);

    @Query("SELECT * FROM notification_history ORDER BY timestamp DESC")
    List<NotificationHistory> getAll();
    
    @Query("SELECT * FROM notification_history WHERE packageName = :pkgName ORDER BY timestamp DESC")
    List<NotificationHistory> getByPackage(String pkgName);
}