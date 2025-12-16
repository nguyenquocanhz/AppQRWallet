package com.nqatech.vqr.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.nqatech.vqr.HomeActivity;
import com.nqatech.vqr.R;
import com.nqatech.vqr.database.AppDatabase;
import com.nqatech.vqr.database.entity.NotificationHistory;
import com.nqatech.vqr.service.parser.BankParserFactory;
import com.nqatech.vqr.service.parser.IBankParser;
import com.nqatech.vqr.utils.MoneyReaderUtils;

import java.util.Locale;

public class BankNotificationListenerService extends NotificationListenerService implements TextToSpeech.OnInitListener {

    private static final String TAG = "BankNotifListener";
    private TextToSpeech tts;
    private static final String PREFS_NAME = "vqr_prefs";
    private static final String KEY_TARGET_PACKAGE = "target_bank_package";
    private static final String CHANNEL_ID = "vqr_balance_channel";
    private static final String CHANNEL_NAME = "Biến động số dư";

    @Override
    public void onCreate() {
        super.onCreate();
        tts = new TextToSpeech(this, this);
        createNotificationChannel();
        Log.d(TAG, "Service Created - Ready to listen");
    }

    @Override
    public void onDestroy() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            int result = tts.setLanguage(new Locale("vi", "VN"));
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e(TAG, "Vietnamese language not supported or missing data");
            }
        } else {
            Log.e(TAG, "TTS Init failed");
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Thông báo khi nhận tiền từ ngân hàng");
            channel.enableLights(true);
            channel.setLightColor(Color.GREEN);
            channel.enableVibration(true);
            
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        String packageName = sbn.getPackageName();
        
        // Log all notifications to debug visibility
        Log.d(TAG, "RECEIVED NOTIF FROM: " + packageName);

        // Ignore own notifications to prevent loop
        if (packageName.equals(getPackageName())) return;

        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String targetPackage = prefs.getString(KEY_TARGET_PACKAGE, ""); 
        
        boolean isTarget = false;
        if (targetPackage.isEmpty()) {
            if (isCommonBank(packageName)) isTarget = true;
        } else {
            // Support partial match or exact match
            if (packageName.toLowerCase().contains(targetPackage.toLowerCase())) isTarget = true;
        }

        if (!isTarget) return;

        Notification notification = sbn.getNotification();
        if (notification == null) return;

        Bundle extras = notification.extras;
        String title = extras.getString(Notification.EXTRA_TITLE, "");
        String text = extras.getString(Notification.EXTRA_TEXT, "");
        
        if (title.isEmpty() && text.isEmpty()) return;

        Log.d(TAG, "Processing Bank Notif: " + title + " | " + text);

        processNotification(packageName, title, text);
    }

    private boolean isCommonBank(String packageName) {
        String pkg = packageName.toLowerCase();
        return pkg.contains("com.vietcombank") || 
               pkg.contains("com.vcb") || 
               pkg.contains("com.mbmobile") ||
               pkg.contains("com.techcombank") ||
               pkg.contains("vn.com.vietinbank") ||
               pkg.contains("com.vnpay") ||
               pkg.contains("com.bidv.smartbanking");
    }

    private void processNotification(String packageName, String title, String content) {
        // Sử dụng Factory để lấy Parser phù hợp
        IBankParser parser = BankParserFactory.getParser(packageName);
        
        double amount = parser.parseAmount(title, content);

        if (amount > 0) {
            Log.d(TAG, "Parsed Amount: " + amount);

            // 1. Save to DB
            NotificationHistory history = new NotificationHistory(packageName, title, content, amount, System.currentTimeMillis());
            AppDatabase.getDatabase(getApplicationContext()).notificationHistoryDao().insert(history);

            // 2. Read aloud
            String speakText = MoneyReaderUtils.readMoney(amount);
            speak(speakText);
            
            // 3. Send App Notification
            sendAppNotification(packageName, speakText, amount);
        } else {
            Log.d(TAG, "No money pattern found or amount invalid");
        }
    }
    
    private void sendAppNotification(String sourcePackage, String speakText, double amount) {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 
                PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification_alert) 
                .setContentTitle("Nhận tiền thành công!")
                .setContentText("+" + String.format("%,.0f", amount) + " đ (" + speakText + ")")
                .setSubText("Từ: " + getAppName(sourcePackage))
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify((int) System.currentTimeMillis(), builder.build());
    }
    
    private String getAppName(String packageName) {
        try {
            return getPackageManager().getApplicationLabel(
                    getPackageManager().getApplicationInfo(packageName, 0)).toString();
        } catch (Exception e) {
            return packageName;
        }
    }

    private void speak(String text) {
        if (tts != null) {
            tts.speak("Ví Q R. Nhận " + text, TextToSpeech.QUEUE_FLUSH, null, "UtteranceId");
        }
    }
}