package com.nqatech.vqr;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;
import com.nqatech.vqr.theme.ThemeManager;
import com.nqatech.vqr.util.BiometricUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SettingsActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "vqr_prefs";
    private static final String KEY_BIOMETRIC_ENABLED = "biometric_enabled";
    private static final String KEY_USER_NAME = "user_name";
    private static final String KEY_USER_PHONE = "user_phone";
    private static final String KEY_TARGET_PACKAGE = "target_bank_package";

    private TextView tvUserName;
    private TextView tvPhone;
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        ThemeManager.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Configure Google Sign-In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());
        
        tvUserName = findViewById(R.id.tvUserName);
        tvPhone = findViewById(R.id.tvPhone);

        loadUserInfo();

        // Setup Edit Profile Button
        ImageView btnEditProfile = findViewById(R.id.btnEditProfile);
        btnEditProfile.setOnClickListener(v -> showEditProfileDialog());
        
        // Setup Notification Settings
        LinearLayout btnNotificationPermission = findViewById(R.id.btnNotificationPermission);
        if (btnNotificationPermission != null) {
            btnNotificationPermission.setOnClickListener(v -> {
                try {
                    Intent intent = new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS);
                    startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(this, "Không thể mở cài đặt thông báo", Toast.LENGTH_SHORT).show();
                }
            });
        }
        
        LinearLayout btnConfigBankPackage = findViewById(R.id.btnConfigBankPackage);
        if (btnConfigBankPackage != null) {
            btnConfigBankPackage.setOnClickListener(v -> showAppSelectionDialog());
        }

        // Setup Theme RadioGroup
        RadioGroup rgTheme = findViewById(R.id.rgTheme);
        String currentMode = ThemeManager.getCurrentTheme(this);
        
        if (ThemeManager.MODE_LIGHT.equals(currentMode)) {
            rgTheme.check(R.id.rbLight);
        } else if (ThemeManager.MODE_DARK.equals(currentMode)) {
            rgTheme.check(R.id.rbDark);
        } else {
            rgTheme.check(R.id.rbSystem);
        }
        
        rgTheme.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbLight) {
                ThemeManager.setTheme(SettingsActivity.this, ThemeManager.MODE_LIGHT);
            } else if (checkedId == R.id.rbDark) {
                ThemeManager.setTheme(SettingsActivity.this, ThemeManager.MODE_DARK);
            } else {
                ThemeManager.setTheme(SettingsActivity.this, ThemeManager.MODE_SYSTEM);
            }
            recreate();
        });

        // Biometric Settings
        LinearLayout layoutBiometric = findViewById(R.id.layoutBiometric);
        SwitchMaterial switchBiometric = findViewById(R.id.switchBiometric);
        
        if (BiometricUtil.isBiometricAvailable(this)) {
            layoutBiometric.setVisibility(View.VISIBLE);
            
            SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
            boolean isEnabled = prefs.getBoolean(KEY_BIOMETRIC_ENABLED, false);
            switchBiometric.setChecked(isEnabled);
            
            switchBiometric.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    // Require authentication to enable
                    BiometricUtil.showBiometricPrompt(this, "Xác thực để bật bảo mật", "Sử dụng vân tay hoặc Face ID", new BiometricUtil.BiometricCallback() {
                        @Override
                        public void onSuccess() {
                            prefs.edit().putBoolean(KEY_BIOMETRIC_ENABLED, true).apply();
                            Toast.makeText(SettingsActivity.this, "Đã bật bảo mật sinh trắc học", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onError(String error) {
                            switchBiometric.setChecked(false); // Revert switch
                            Toast.makeText(SettingsActivity.this, "Xác thực thất bại: " + error, Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    // Disable directly (or ask for confirmation)
                    prefs.edit().putBoolean(KEY_BIOMETRIC_ENABLED, false).apply();
                }
            });
        } else {
            layoutBiometric.setVisibility(View.GONE);
        }

        // Logout Button
        TextView btnLogout = findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(v -> signOut());

        // Setup Bottom Nav Interactions
        LinearLayout navHome = findViewById(R.id.navHome);
        navHome.setOnClickListener(v -> {
            Intent intent = new Intent(SettingsActivity.this, HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });

        FrameLayout btnScanCenter = findViewById(R.id.btnScanCenter);
        btnScanCenter.setOnClickListener(v -> {
            Intent intent = new Intent(SettingsActivity.this, ScanQRActivity.class);
            startActivity(intent);
        });
    }

    private void signOut() {
        mGoogleSignInClient.signOut().addOnCompleteListener(this, task -> {
            // Clear login state
            SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
            prefs.edit().putBoolean("is_logged_in", false).apply();

            // Navigate to LoginActivity
            Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void loadUserInfo() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String name = prefs.getString(KEY_USER_NAME, "Admin");
        String phone = prefs.getString(KEY_USER_PHONE, "+84 1234567890");
        tvUserName.setText(name);
        tvPhone.setText(phone);
    }

    private void showEditProfileDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Chỉnh sửa thông tin");

        View viewInflated = LayoutInflater.from(this).inflate(R.layout.dialog_edit_profile, null);
        final TextInputEditText inputName = viewInflated.findViewById(R.id.etEditName);
        final TextInputEditText inputPhone = viewInflated.findViewById(R.id.etEditPhone);

        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        inputName.setText(prefs.getString(KEY_USER_NAME, "Nguyen Van A"));
        inputPhone.setText(prefs.getString(KEY_USER_PHONE, "+84 90 123 4567"));

        builder.setView(viewInflated);

        builder.setPositiveButton("Lưu", (dialog, which) -> {
            String newName = inputName.getText().toString();
            String newPhone = inputPhone.getText().toString();
            
            prefs.edit()
                .putString(KEY_USER_NAME, newName)
                .putString(KEY_USER_PHONE, newPhone)
                .apply();
                
            loadUserInfo();
            Toast.makeText(SettingsActivity.this, "Đã cập nhật thông tin", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });
        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.cancel());

        builder.show();
    }
    
    private void showAppSelectionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Chọn App Ngân Hàng");

        View viewInflated = LayoutInflater.from(this).inflate(R.layout.dialog_app_select, null);
        EditText etSearchApp = viewInflated.findViewById(R.id.etSearchApp);
        ProgressBar progressBar = viewInflated.findViewById(R.id.progressBar);
        RecyclerView rvAppList = viewInflated.findViewById(R.id.rvAppList);
        
        rvAppList.setLayoutManager(new LinearLayoutManager(this));
        
        final AlertDialog dialog = builder.setView(viewInflated).create();
        
        // Show dialog first
        dialog.show();
        
        // Start loading apps in background
        progressBar.setVisibility(View.VISIBLE);
        rvAppList.setVisibility(View.GONE);
        
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        
        executor.execute(() -> {
            List<AppInfo> appList = getInstalledApps();
            
            handler.post(() -> {
                progressBar.setVisibility(View.GONE);
                rvAppList.setVisibility(View.VISIBLE);
                
                if (appList.isEmpty()) {
                    Toast.makeText(SettingsActivity.this, "Không tìm thấy ứng dụng nào", Toast.LENGTH_SHORT).show();
                } else {
                    AppListAdapter adapter = new AppListAdapter(appList, appInfo -> {
                        // On Item Click
                        saveSelectedApp(appInfo);
                        dialog.dismiss();
                    });
                    rvAppList.setAdapter(adapter);
                    
                    // Setup Search
                    etSearchApp.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            adapter.filter(s.toString());
                        }
                        @Override
                        public void afterTextChanged(Editable s) {}
                    });
                }
            });
        });
    }

    private void saveSelectedApp(AppInfo appInfo) {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        prefs.edit().putString(KEY_TARGET_PACKAGE, appInfo.packageName).apply();
        Toast.makeText(this, "Đã chọn: " + appInfo.name, Toast.LENGTH_SHORT).show();
    }

    private List<AppInfo> getInstalledApps() {
        List<AppInfo> apps = new ArrayList<>();
        PackageManager pm = getPackageManager();
        // Get all installed apps
        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);

        for (ApplicationInfo appInfo : packages) {
            // Filter out system apps if desired, but some banking apps might be pre-installed?
            // Usually we want user apps. 
            // (appInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0 -> User App
            // (appInfo.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0 -> Updated System App (User updated)
            
            // Let's include everything that has a launch intent (openable apps)
            if (pm.getLaunchIntentForPackage(appInfo.packageName) != null) {
                String name = pm.getApplicationLabel(appInfo).toString();
                Drawable icon = pm.getApplicationIcon(appInfo);
                apps.add(new AppInfo(name, appInfo.packageName, icon));
            }
        }
        
        // Sort alphabetically
        Collections.sort(apps, (o1, o2) -> o1.name.compareToIgnoreCase(o2.name));
        
        return apps;
    }

    // --- Inner Classes for App Selection ---

    static class AppInfo {
        String name;
        String packageName;
        Drawable icon;

        public AppInfo(String name, String packageName, Drawable icon) {
            this.name = name;
            this.packageName = packageName;
            this.icon = icon;
        }
    }

    static class AppListAdapter extends RecyclerView.Adapter<AppListAdapter.ViewHolder> {
        private List<AppInfo> originalList;
        private List<AppInfo> displayedList;
        private OnAppSelectedListener listener;

        interface OnAppSelectedListener {
            void onAppSelected(AppInfo appInfo);
        }

        public AppListAdapter(List<AppInfo> list, OnAppSelectedListener listener) {
            this.originalList = list;
            this.displayedList = new ArrayList<>(list);
            this.listener = listener;
        }

        public void filter(String query) {
            displayedList.clear();
            if (query.isEmpty()) {
                displayedList.addAll(originalList);
            } else {
                String lowerQuery = query.toLowerCase();
                for (AppInfo app : originalList) {
                    if (app.name.toLowerCase().contains(lowerQuery) || 
                        app.packageName.toLowerCase().contains(lowerQuery)) {
                        displayedList.add(app);
                    }
                }
            }
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_app_select, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            AppInfo app = displayedList.get(position);
            holder.tvAppName.setText(app.name);
            holder.tvPackageName.setText(app.packageName);
            holder.ivAppIcon.setImageDrawable(app.icon);
            
            holder.itemView.setOnClickListener(v -> listener.onAppSelected(app));
        }

        @Override
        public int getItemCount() {
            return displayedList.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            ImageView ivAppIcon;
            TextView tvAppName, tvPackageName;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                ivAppIcon = itemView.findViewById(R.id.ivAppIcon);
                tvAppName = itemView.findViewById(R.id.tvAppName);
                tvPackageName = itemView.findViewById(R.id.tvPackageName);
            }
        }
    }
}