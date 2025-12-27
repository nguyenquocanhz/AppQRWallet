package com.nqatech.vqr;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.WindowManager;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class SecurityUtils {

    private static final String PREFS_NAME = "vqr_secure_prefs";

    /**
     * Returns a singleton instance of EncryptedSharedPreferences.
     */
    public static SharedPreferences getEncryptedSharedPreferences(Context context) {
        try {
            String masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);

            return EncryptedSharedPreferences.create(
                    PREFS_NAME,
                    masterKeyAlias,
                    context.getApplicationContext(),
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
        } catch (GeneralSecurityException | IOException e) {
            // Handle the error, e.g., by logging or throwing a runtime exception
            e.printStackTrace();
            throw new RuntimeException("Could not create EncryptedSharedPreferences", e);
        }
    }

    /**
     * Prevents screenshots and screen recording for a given Activity.
     * This should be called in onCreate() of any Activity displaying sensitive data.
     */
    public static void secureActivity(Activity activity) {
        if (activity != null) {
            activity.getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE
            );
        }
    }
}
