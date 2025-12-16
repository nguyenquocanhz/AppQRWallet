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

public class BankAdapter extends RecyclerView.Adapter<BankAdapter.BankViewHolder> {

    private List<Bank> bankList;
    private OnBankClickListener listener;
    private int selectedPosition = -1;

    public interface OnBankClickListener {
        void onBankClick(Bank bank);
    }

    public BankAdapter(List<Bank> bankList, OnBankClickListener listener) {
        this.bankList = bankList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public BankViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bank, parent, false);
        return new BankViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BankViewHolder holder, int position) {
        Bank bank = bankList.get(position);
        
        holder.tvBankShortName.setText(bank.getShortName());
        
        // Load logo using custom ImageLoader
        if (bank.getLogo() != null && !bank.getLogo().isEmpty()) {
            ImageLoader.load(holder.ivLogo, bank.getLogo());
        } else {
             holder.ivLogo.setImageResource(R.drawable.ic_launcher_foreground);
        }
        
        // Highlight selected item
        if (selectedPosition == position) {
            holder.itemView.setBackgroundResource(R.drawable.bg_bank_selected);
            holder.tvBankShortName.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.white));
        } else {
            holder.itemView.setBackgroundResource(R.drawable.bg_bank_unselected);
            holder.tvBankShortName.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.text_primary));
        }

        holder.itemView.setOnClickListener(v -> {
            int previousPos = selectedPosition;
            selectedPosition = holder.getAdapterPosition();
            notifyItemChanged(previousPos);
            notifyItemChanged(selectedPosition);
            
            if (listener != null) {
                listener.onBankClick(bank);
            }
        });
    }

    @Override
    public int getItemCount() {
        return bankList != null ? bankList.size() : 0;
    }

    public void updateData(List<Bank> newBankList) {
        this.bankList = newBankList;
        notifyDataSetChanged();
    }

    static class BankViewHolder extends RecyclerView.ViewHolder {
        ImageView ivLogo;
        TextView tvBankShortName;

        public BankViewHolder(@NonNull View itemView) {
            super(itemView);
            ivLogo = itemView.findViewById(R.id.ivLogo);
            tvBankShortName = itemView.findViewById(R.id.tvBankShortName);
        }
    }
}