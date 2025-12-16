package com.nqatech.vqr;

import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.nqatech.vqr.theme.ThemeManager;

import org.jmrtd.BACKey;
import org.jmrtd.BACKeySpec;
import org.jmrtd.PassportService;
import org.jmrtd.lds.CardAccessFile;
import org.jmrtd.lds.PACEInfo;
import org.jmrtd.lds.SecurityInfo;
import org.jmrtd.lds.icao.DG1File;
import org.jmrtd.lds.icao.DG2File;
import org.jmrtd.lds.iso19794.FaceImageInfo;
import org.jmrtd.lds.iso19794.FaceInfo;
import org.jmrtd.lds.icao.MRZInfo;

import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import net.sf.scuba.smartcards.CardService;

public class NfcReaderActivity extends AppCompatActivity {

    private TextInputEditText etCccdNumber, etBirthDate, etExpiryDate;
    private TextView btnScan, tvStatus;
    private FrameLayout layoutLoading;
    private LinearLayout layoutResult;
    private TextView tvFullName, tvGender, tvNationality;
    private ImageView ivPortrait;

    private NfcAdapter nfcAdapter;
    private PendingIntent pendingIntent;
    private boolean isScanning = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        ThemeManager.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfc_reader);

        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        etCccdNumber = findViewById(R.id.etCccdNumber);
        etBirthDate = findViewById(R.id.etBirthDate);
        etExpiryDate = findViewById(R.id.etExpiryDate);
        btnScan = findViewById(R.id.btnScan);
        tvStatus = findViewById(R.id.tvStatus);
        layoutLoading = findViewById(R.id.layoutLoading);
        layoutResult = findViewById(R.id.layoutResult);
        tvFullName = findViewById(R.id.tvFullName);
        tvGender = findViewById(R.id.tvGender);
        tvNationality = findViewById(R.id.tvNationality);
        ivPortrait = findViewById(R.id.ivPortrait);

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (nfcAdapter == null) {
            Toast.makeText(this, "Thiết bị không hỗ trợ NFC", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Intent intent = new Intent(this, getClass());
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_MUTABLE);

        btnScan.setOnClickListener(v -> startScanning());
    }

    private void startScanning() {
        String cccd = etCccdNumber.getText().toString();
        String dob = etBirthDate.getText().toString();
        String exp = etExpiryDate.getText().toString();

        if (cccd.length() != 12 || dob.length() != 6 || exp.length() != 6) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ và đúng định dạng thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        isScanning = true;
        layoutLoading.setVisibility(View.VISIBLE);
        tvStatus.setText("Vui lòng áp thẻ CCCD vào mặt sau điện thoại...");
        Toast.makeText(this, "Đang chờ thẻ...", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (nfcAdapter != null) {
            nfcAdapter.enableForegroundDispatch(this, pendingIntent, null, null);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (nfcAdapter != null) {
            nfcAdapter.disableForegroundDispatch(this);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (isScanning && NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {
            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            if (tag != null) {
                processTag(tag);
            }
        }
    }

    private void processTag(Tag tag) {
        tvStatus.setText("Đang đọc dữ liệu... Giữ nguyên thẻ.");
        
        String cccd = etCccdNumber.getText().toString();
        String dob = etBirthDate.getText().toString();
        String exp = etExpiryDate.getText().toString();
        
        BACKeySpec bacKey = new BACKey(cccd, dob, exp);

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            try {
                IsoDep isoDep = IsoDep.get(tag);
                if (isoDep == null) {
                    throw new Exception("Thẻ không hỗ trợ IsoDep");
                }
                isoDep.setTimeout(5000);

                CardService cardService = CardService.getInstance(isoDep);
                cardService.open();

                PassportService service = new PassportService(cardService, PassportService.NORMAL_MAX_TRANCEIVE_LENGTH, PassportService.DEFAULT_MAX_BLOCKSIZE, false, false);
                service.open();

                boolean paceSucceeded = false;
                try {
                    CardAccessFile cardAccessFile = new CardAccessFile(service.getInputStream(PassportService.EF_CARD_ACCESS));
                    Collection<SecurityInfo> securityInfos = cardAccessFile.getSecurityInfos();
                    for (SecurityInfo securityInfo : securityInfos) {
                        if (securityInfo instanceof PACEInfo) {
                            service.doPACE(bacKey, securityInfo.getObjectIdentifier(), PACEInfo.toParameterSpec(((PACEInfo) securityInfo).getParameterId()), null);
                            paceSucceeded = true;
                        }
                    }
                } catch (Exception e) {
                   // Fallback to BAC if PACE fails or not present
                }

                service.sendSelectApplet(paceSucceeded);

                if (!paceSucceeded) {
                    try {
                        service.getInputStream(PassportService.EF_COM).read();
                    } catch (Exception e) {
                        service.doBAC(bacKey);
                    }
                }

                // Read DG1 (Personal Info)
                InputStream dg1In = service.getInputStream(PassportService.EF_DG1);
                DG1File dg1File = new DG1File(dg1In);
                MRZInfo mrzInfo = dg1File.getMRZInfo();
                
                String fullName = mrzInfo.getPrimaryIdentifier().replace("<", " ") + " " + mrzInfo.getSecondaryIdentifier().replace("<", " ");
                String gender = mrzInfo.getGender().toString();
                String nationality = mrzInfo.getNationality();

                // Read DG2 (Face Image)
                InputStream dg2In = service.getInputStream(PassportService.EF_DG2);
                DG2File dg2File = new DG2File(dg2In);
                
                List<FaceInfo> faceInfos = dg2File.getFaceInfos();
                Bitmap portrait = null;
                for (FaceInfo faceInfo : faceInfos) {
                    for (FaceImageInfo faceImageInfo : faceInfo.getFaceImageInfos()) {
                        InputStream faceStream = faceImageInfo.getImageInputStream();
                        portrait = BitmapFactory.decodeStream(faceStream);
                        if (portrait != null) break;
                    }
                    if (portrait != null) break;
                }

                final Bitmap finalPortrait = portrait;
                
                new Handler(Looper.getMainLooper()).post(() -> {
                    layoutLoading.setVisibility(View.GONE);
                    isScanning = false;
                    showResult(fullName, gender, nationality, finalPortrait);
                });

            } catch (Exception e) {
                e.printStackTrace();
                new Handler(Looper.getMainLooper()).post(() -> {
                    tvStatus.setText("Lỗi: " + e.getMessage());
                    Toast.makeText(NfcReaderActivity.this, "Đọc thẻ thất bại. Kiểm tra lại thông tin nhập.", Toast.LENGTH_LONG).show();
                    layoutLoading.setVisibility(View.GONE);
                    isScanning = false;
                });
            }
        });
    }

    private void showResult(String name, String gender, String nationality, Bitmap portrait) {
        layoutResult.setVisibility(View.VISIBLE);
        tvFullName.setText(name);
        tvGender.setText("Giới tính: " + gender);
        tvNationality.setText("Quốc tịch: " + nationality);
        
        if (portrait != null) {
            ivPortrait.setImageBitmap(portrait);
        } else {
            ivPortrait.setImageResource(R.drawable.ic_launcher_foreground);
        }
    }
}
