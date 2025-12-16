package com.nqatech.vqr.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.nqatech.vqr.R;
import java.util.List;

public class VietQRAdapter extends RecyclerView.Adapter<VietQRAdapter.VietQRViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(VietQRItem item);
    }

    public static class VietQRItem {
        public int id; // Added ID field
        public String bankName;
        public String bankCode;
        public String bin;
        public String accountNumber;
        public String accountName;
        public String validThru;
        public String amount;
        public String content;

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
        }
    }
}