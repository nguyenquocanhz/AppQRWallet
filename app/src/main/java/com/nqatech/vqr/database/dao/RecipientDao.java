package com.nqatech.vqr.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.nqatech.vqr.database.entity.Recipient;

import java.util.List;

@Dao
public interface RecipientDao {
    @Query("SELECT * FROM recipients ORDER BY createdAt DESC")
    List<Recipient> getAllRecipients();

    @Insert
    void insertRecipient(Recipient recipient);

    @Update
    void updateRecipient(Recipient recipient);

    @Delete
    void deleteRecipient(Recipient recipient);
}