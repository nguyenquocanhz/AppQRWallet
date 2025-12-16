package com.nqatech.vqr.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.nqatech.vqr.R;
import com.nqatech.vqr.api.model.Bank;
import com.nqatech.vqr.util.ImageLoader;
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
        public String logoUrl; // To store logo URL

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
    // You might want to pass the list of banks to the adapter to lookup logos if logoUrl is not in item
    // Or assume the item is populated with logoUrl.
    // For simplicity, let's assume we might need to lookup or it's passed.
    // Ideally, when creating VietQRItem in Activity, we should populate logoUrl.
    
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
            // Fallback to default or try to find by bin if we had the bank list here
            holder.ivBankLogo.setImageResource(R.drawable.ic_launcher_foreground);
        }
        
        // Load QR Image if available
        if (item.qrDataURL != null && !item.qrDataURL.isEmpty()) {
            try {
                byte[] decodedString = android.util.Base64.decode(item.qrDataURL, android.util.Base64.DEFAULT);
                android.graphics.Bitmap decodedByte = android.graphics.BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                holder.ivQR.setImageBitmap(decodedByte);
            } catch (Exception e) {
                holder.ivQR.setImageResource(R.drawable.ic_qrcode);
            }
        } else {
             holder.ivQR.setImageResource(R.drawable.ic_qrcode);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(item);
            }
        });
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