package com.nqatech.vqr.util;

import android.content.Context;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import java.util.concurrent.Executor;

public class BiometricUtil {

    public interface BiometricCallback {
        void onSuccess();
        void onError(String error);
    }

    public static boolean isBiometricAvailable(Context context) {
        BiometricManager biometricManager = BiometricManager.from(context);
        return biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG) 
                == BiometricManager.BIOMETRIC_SUCCESS;
    }

    public static void showBiometricPrompt(FragmentActivity activity, String title, String subtitle, BiometricCallback callback) {
        Executor executor = ContextCompat.getMainExecutor(activity);
        
        BiometricPrompt biometricPrompt = new BiometricPrompt(activity, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                // errorCode == BiometricPrompt.ERROR_NEGATIVE_BUTTON means user cancelled
                callback.onError(errString.toString());
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                callback.onSuccess();
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                // Fingerprint not recognized, let system handle UI, but we can log
            }
        });

        BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle(title)
                .setSubtitle(subtitle)
                .setNegativeButtonText("Hủy") // Or use setAllowedAuthenticators if allowing device credential
                .build();

        biometricPrompt.authenticate(promptInfo);
    }
}