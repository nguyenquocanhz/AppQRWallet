package com.nqatech.vqr.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.nqatech.vqr.R;
import com.nqatech.vqr.util.ImageLoader;
import android.app.Dialog;
import android.view.Window;

import java.util.List;

public class VietQRAdapter extends RecyclerView.Adapter<VietQRAdapter.VietQRViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(VietQRItem item);
    }

    public static class VietQRItem {
        public int id;
        public String bankName;
        public String bankCode;
        public String bin;
        public String accountNumber;
        public String accountName;
        public String validThru;
        public String amount;
        public String content;
        public String qrDataURL;
        public String logoUrl;

        public VietQRItem(int id, String bankName, String bankCode, String bin, String accountNumber, String accountName, String validThru) {
            this.id = id;
            this.bankName = bankName;
            this.bankCode = bankCode;
            this.bin = bin;
            this.accountNumber = accountNumber;
            this.accountName = accountName;
            this.validThru = validThru;
        }
    }

    private List<VietQRItem> qrList;
    private OnItemClickListener listener;
    
    public VietQRAdapter(List<VietQRItem> qrList, OnItemClickListener listener) {
        this.qrList = qrList;
        this.listener = listener;
    }
    
    public VietQRAdapter(List<VietQRItem> qrList) {
        this(qrList, null);
    }

    @NonNull
    @Override
    public VietQRViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_vietqr_card, parent, false);
        return new VietQRViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VietQRViewHolder holder, int position) {
        VietQRItem item = qrList.get(position);
        holder.tvBankName.setText(item.bankName);
        holder.tvAccountNumber.setText(item.accountNumber);
        holder.tvAccountName.setText(item.accountName);

        // Load Bank Logo
        if (item.logoUrl != null && !item.logoUrl.isEmpty()) {
            ImageLoader.load(holder.ivBankLogo, item.logoUrl);
        } else {
            int logoResId = getBankLogoResId(holder.itemView.getContext(), item.bankCode);
            if (logoResId != 0) {
                 holder.ivBankLogo.setImageResource(logoResId);
            } else {
                 holder.ivBankLogo.setImageResource(R.drawable.ic_launcher_foreground);
            }
        }
        
        // Load QR Image
        if (item.qrDataURL != null && !item.qrDataURL.isEmpty()) {
            try {
                // Clean Base64 string if it has header
                String base64Data = item.qrDataURL;
                if (base64Data.contains("base64,")) {
                    base64Data = base64Data.substring(base64Data.indexOf("base64,") + 7);
                }
                
                byte[] decodedString = Base64.decode(base64Data, Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                holder.ivQR.setImageBitmap(decodedByte);
                
                // Add click listener to show dialog
                holder.ivQR.setOnClickListener(v -> showQRDialog(holder.itemView.getContext(), decodedByte));
                
            } catch (Exception e) {
                holder.ivQR.setImageResource(R.drawable.ic_qrcode);
                holder.ivQR.setOnClickListener(null);
            }
        } else {
             holder.ivQR.setImageResource(R.drawable.ic_qrcode);
             holder.ivQR.setOnClickListener(null);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(item);
            }
        });
    }

    private void showQRDialog(Context context, Bitmap qrBitmap) {
        Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        ImageView imageView = new ImageView(context);
        imageView.setImageBitmap(qrBitmap);
        imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        imageView.setAdjustViewBounds(true);
        imageView.setPadding(20, 20, 20, 20);
        imageView.setBackgroundColor(android.graphics.Color.WHITE);
        
        dialog.setContentView(imageView);
        
        Window window = dialog.getWindow();
        if (window != null) {
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            window.setBackgroundDrawableResource(android.R.color.transparent);
        }
        
        dialog.show();
    }

    private int getBankLogoResId(Context context, String bankCode) {
        if (bankCode == null) return 0;
        
        String formattedCode = "ic_bank_" + bankCode.toLowerCase().replace(" ", "_");
        switch (bankCode.toUpperCase()) {
            case "VCB": return R.drawable.ic_bank_vietcombank;
            case "TCB": return R.drawable.ic_bank_techcombank;
            case "MB": return R.drawable.ic_bank_mb_bank;
            case "ACB": return R.drawable.ic_bank_acb;
            case "VPB": return R.drawable.ic_bank_vpbank;
            case "TPB": return R.drawable.ic_bank_tpbank;
            case "STB": return R.drawable.ic_bank_scb;
            case "BIDV": return R.drawable.ic_bank_bidv;
            case "CTG": return R.drawable.ic_bank_vietinbank;
            case "ICB": return R.drawable.ic_bank_vietinbank;
            case "HDB": return R.drawable.ic_bank_hdbank;
            case "VIB": return R.drawable.ic_bank_vib;
            case "MSB": return R.drawable.ic_bank_msb;
            case "SHB": return R.drawable.ic_bank_shb;
            case "OCB": return R.drawable.ic_bank_ocb;
            case "BAB": return R.drawable.ic_bank_bac_a_bank;
            case "LPB": return R.drawable.ic_bank_lienviet_postbank;
            case "NAB": return R.drawable.ic_bank_nam_a_bank;
            case "SSB": return R.drawable.ic_bank_sea_bank;
            case "EIB": return R.drawable.ic_bank_eximbank;
            default:
                int resId = context.getResources().getIdentifier(formattedCode, "drawable", context.getPackageName());
                if (resId != 0) return resId;
                
                String cleanName = "ic_bank_" + bankCode.replaceAll("\\s+", "").toLowerCase();
                resId = context.getResources().getIdentifier(cleanName, "drawable", context.getPackageName());
                return resId;
        }
    }

    @Override
    public int getItemCount() {
        return qrList != null ? qrList.size() : 0;
    }

    static class VietQRViewHolder extends RecyclerView.ViewHolder {
        TextView tvBankName, tvAccountNumber, tvAccountName;
        ImageView ivBankLogo, ivQR;

        public VietQRViewHolder(@NonNull View itemView) {
            super(itemView);
            tvBankName = itemView.findViewById(R.id.tvBankName);
            tvAccountNumber = itemView.findViewById(R.id.tvAccountNumber);
            tvAccountName = itemView.findViewById(R.id.tvAccountName);
            ivBankLogo = itemView.findViewById(R.id.ivBankLogo);
            ivQR = itemView.findViewById(R.id.ivQR);
        }
    }
}