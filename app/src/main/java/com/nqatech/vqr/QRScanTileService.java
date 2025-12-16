package com.nqatech.vqr;

import android.content.Intent;
import android.service.quicksettings.TileService;

public class QRScanTileService extends TileService {

    @Override
    public void onClick() {
        super.onClick();
        Intent intent = new Intent(this, ScanQRActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        
        // Unlock device and start activity
        unlockAndRun(() -> startActivityAndCollapse(intent));
    }
}