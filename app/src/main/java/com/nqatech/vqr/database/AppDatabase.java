package com.nqatech.vqr.database;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.nqatech.vqr.database.dao.NotificationHistoryDao;
import com.nqatech.vqr.database.dao.RecipientDao;
import com.nqatech.vqr.database.dao.UserDao;
import com.nqatech.vqr.database.entity.NotificationHistory;
import com.nqatech.vqr.database.entity.Recipient;
import com.nqatech.vqr.database.entity.User;

@Database(entities = {User.class, Recipient.class, NotificationHistory.class}, version = 5, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    private static volatile AppDatabase INSTANCE;

    public abstract UserDao userDao();
    public abstract RecipientDao recipientDao();
    public abstract NotificationHistoryDao notificationHistoryDao();

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "vqr_database")
                            .fallbackToDestructiveMigration() // Handle migration destructively for dev phase
                            .allowMainThreadQueries() // Not recommended for production
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}