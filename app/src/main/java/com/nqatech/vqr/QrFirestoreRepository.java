package com.nqatech.vqr;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.nqatech.vqr.database.AppDatabase;
import com.nqatech.vqr.database.dao.RecipientDao;
import com.nqatech.vqr.database.entity.Recipient;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class QrFirestoreRepository {

    private static final String TAG = "QrFirestoreRepository";
    private static final String FIRESTORE_COLLECTION = "user_qrs";
    private static final String ENCRYPTION_KEY_ALIAS = "firestore_encryption_key";
    private static final String ALGORITHM = "AES";

    private final FirebaseFirestore db;
    private final RecipientDao recipientDao;
    private final ExecutorService executor;
    private final Context context;
    private SecretKey secretKey;

    private static volatile QrFirestoreRepository INSTANCE;

    private QrFirestoreRepository(Context context) {
        this.context = context.getApplicationContext();
        this.db = FirebaseFirestore.getInstance();
        this.recipientDao = AppDatabase.getDatabase(context).recipientDao();
        this.executor = Executors.newSingleThreadExecutor();
        this.secretKey = getOrCreateEncryptionKey();
    }

    public static QrFirestoreRepository getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (QrFirestoreRepository.class) {
                if (INSTANCE == null) {
                    INSTANCE = new QrFirestoreRepository(context);
                }
            }
        }
        return INSTANCE;
    }

    private SecretKey getOrCreateEncryptionKey() {
        SharedPreferences securePrefs = SecurityUtils.getEncryptedSharedPreferences(context);
        String encodedKey = securePrefs.getString(ENCRYPTION_KEY_ALIAS, null);

        if (encodedKey == null) {
            try {
                KeyGenerator keyGen = KeyGenerator.getInstance(ALGORITHM);
                keyGen.init(256); // AES-256
                SecretKey newKey = keyGen.generateKey();
                String newEncodedKey = Base64.encodeToString(newKey.getEncoded(), Base64.DEFAULT);
                securePrefs.edit().putString(ENCRYPTION_KEY_ALIAS, newEncodedKey).apply();
                Log.d(TAG, "Generated and stored new encryption key.");
                return newKey;
            } catch (Exception e) {
                Log.e(TAG, "Error generating new encryption key", e);
                throw new RuntimeException(e);
            }
        } else {
            byte[] decodedKey = Base64.decode(encodedKey, Base64.DEFAULT);
            return new SecretKeySpec(decodedKey, 0, decodedKey.length, ALGORITHM);
        }
    }

    private String encrypt(String data) {
        if (data == null) return null;
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] encryptedBytes = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return Base64.encodeToString(encryptedBytes, Base64.DEFAULT);
        } catch (Exception e) {
            Log.e(TAG, "Encryption failed", e);
            return null; // Or handle error appropriately
        }
    }

    private String decrypt(String encryptedData) {
        if (encryptedData == null) return null;
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] decodedBytes = Base64.decode(encryptedData, Base64.DEFAULT);
            byte[] decryptedBytes = cipher.doFinal(decodedBytes);
            return new String(decryptedBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            Log.e(TAG, "Decryption failed for data: " + encryptedData, e);
            return null; // Or handle error appropriately
        }
    }

    private String getCurrentUserId() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        return (user != null) ? user.getUid() : null;
    }

    public void saveRecipient(Recipient recipient) {
        executor.execute(() -> {
            // Save to local Room DB first
            recipientDao.insertRecipient(recipient);

            // Then save to Firestore
            String userId = getCurrentUserId();
            if (userId == null) {
                Log.w(TAG, "Cannot save to Firestore, user not logged in.");
                return;
            }

            Map<String, Object> data = new HashMap<>();
            data.put("id", recipient.id);
            data.put("bankName", encrypt(recipient.bankName));
            data.put("bankCode", encrypt(recipient.bankCode));
            data.put("bin", encrypt(recipient.bin));
            data.put("accountNumber", encrypt(recipient.accountNumber));
            data.put("accountName", encrypt(recipient.accountName));
            data.put("amount", encrypt(recipient.amount));
            data.put("content", encrypt(recipient.content));
            data.put("qrDataURL", encrypt(recipient.qrDataURL)); // Also encrypt the QR data URL
            data.put("createdAt", recipient.createdAt);

            db.collection(FIRESTORE_COLLECTION)
              .document(userId)
              .collection("qrs")
              .document(String.valueOf(recipient.id))
              .set(data)
              .addOnSuccessListener(aVoid -> Log.d(TAG, "Recipient " + recipient.id + " saved to Firestore."))
              .addOnFailureListener(e -> Log.e(TAG, "Error saving recipient to Firestore", e));
        });
    }

    public void syncRecipientsFromFirestore() {
        String userId = getCurrentUserId();
        if (userId == null) {
            Log.w(TAG, "Cannot sync from Firestore, user not logged in.");
            return;
        }

        db.collection(FIRESTORE_COLLECTION)
          .document(userId)
          .collection("qrs")
          .get()
          .addOnSuccessListener(queryDocumentSnapshots -> executor.execute(() -> {
              for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                  try {
                      Recipient recipient = new Recipient(
                          decrypt(doc.getString("bankName")),
                          decrypt(doc.getString("bankCode")),
                          decrypt(doc.getString("bin")),
                          decrypt(doc.getString("accountNumber")),
                          decrypt(doc.getString("accountName")),
                          decrypt(doc.getString("amount")),
                          decrypt(doc.getString("content"))
                      );
                      recipient.id = Integer.parseInt(doc.getId());
                      recipient.qrDataURL = decrypt(doc.getString("qrDataURL"));
                      Date createdAt = doc.getDate("createdAt");
                      if (createdAt != null) {
                        recipient.createdAt = createdAt.getTime();
                      }

                      // Insert or update local database
                      recipientDao.insertRecipient(recipient);
                  } catch (Exception e) {
                      Log.e(TAG, "Error parsing recipient from Firestore doc: " + doc.getId(), e);
                  }
              }
              Log.d(TAG, "Sync from Firestore completed.");
          }))
          .addOnFailureListener(e -> Log.e(TAG, "Error fetching recipients from Firestore", e));
    }
}
