package com.nqatech.vqr;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.credentials.CredentialManager;
import androidx.credentials.GetCredentialRequest;
import androidx.credentials.GetCredentialResponse;
import androidx.credentials.exceptions.GetCredentialException;

import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.libraries.identity.googleid.GetGoogleIdOption;
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential;
import com.nqatech.vqr.theme.ThemeManager;

import java.util.UUID;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    private CredentialManager credentialManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        ThemeManager.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        credentialManager = CredentialManager.create(this);

        SignInButton signInButton = findViewById(R.id.google_sign_in_button);
        signInButton.setOnClickListener(v -> signIn());
    }

    private void signIn() {
        GetGoogleIdOption googleIdOption = GetGoogleIdOption.builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(getString(R.string.default_web_client_id)) // Make sure this string exists in your project
                .setNonce(UUID.randomUUID().toString()) // Security nonce
                .build();

        GetCredentialRequest request = new GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build();

        credentialManager.getCredentialAsync(
                request,
                this, // Activity
                null, // CancellationSignal
                getMainExecutor(), // Executor
                new CredentialManager.Callback<GetCredentialResponse, GetCredentialException>() {
                    @Override
                    public void onResult(GetCredentialResponse result) {
                        try {
                            GoogleIdTokenCredential credential = GoogleIdTokenCredential.createFrom(result.getCredential().getData());
                            GoogleSignInAccount account = GoogleSignIn.getSignedInAccountFromIntent(new Intent().putExtra("google_sign_in_credential", credential)).getResult();
                            handleSignInResult(account);
                        } catch (Exception e) {
                            Log.e(TAG, "Credential parsing failed", e);
                            Toast.makeText(LoginActivity.this, "Đăng nhập thất bại", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(GetCredentialException e) {
                        Log.w(TAG, "getCredentialAsync failed", e);
                        Toast.makeText(LoginActivity.this, "Đăng nhập đã bị hủy hoặc có lỗi", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void handleSignInResult(GoogleSignInAccount account) {
        if (account == null) {
             Log.w(TAG, "signInResult:failed, account is null");
             Toast.makeText(this, "Đăng nhập thất bại", Toast.LENGTH_SHORT).show();
             return;
        }

        Log.d(TAG, "signInResult:success id=" + account.getId());
        
        // Save login state
        saveLoginState(true, account.getDisplayName(), account.getEmail());

        // Navigate directly to HomeActivity, skipping PIN setup
        navigateToHome();
    }

    private void saveLoginState(boolean isLoggedIn, String name, String email) {
        SharedPreferences prefs = getSharedPreferences("vqr_prefs", MODE_PRIVATE);
        prefs.edit()
            .putBoolean("is_logged_in", isLoggedIn)
            .putString("user_name", name)
            .putString("user_email", email)
            .apply();
    }

    private void navigateToHome() {
        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
