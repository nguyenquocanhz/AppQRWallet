package com.nqatech.vqr.adapter;

import android.content.Context;
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
        Context context = holder.itemView.getContext();
        
        holder.tvBankShortName.setText(bank.getShortName());
        
        // Logic to load logo: try to find drawable matching "ic_bank_" + bank code
        // Priority:
        // 1. Dynamic drawable lookup: ic_bank_{code}
        // 2. Dynamic drawable lookup: ic_bank_{shortName}
        // 3. Fallback to ImageLoader if URL (though usually we use local resources now)
        // 4. Default icon
        
        boolean logoSet = false;
        
        String bankCode = bank.getCode(); // e.g. "VCB", "MB", etc. OR "Vietcombank" depending on API
        // If code is "VCB", we look for "ic_bank_vcb" or "ic_bank_vietcombank"?
        // Based on user request: "vietcombank thì hiển ic_bank_vietcombank"
        // And "ic_bank_*{bankName}.xml ví dụ"
        // Let's assume we map the bank code/shortname to the drawable name pattern found in project.
        
        // Try mapping commonly known codes if they don't match file names directly
        String resourceName = getBankLogoResourceName(bank);
        
        int resId = context.getResources().getIdentifier(resourceName, "drawable", context.getPackageName());
        if (resId != 0) {
            holder.ivLogo.setImageResource(resId);
            logoSet = true;
        } else {
             // Try fallback to cleaner name
             String cleanName = "ic_bank_" + bank.getShortName().replaceAll("\\s+", "").toLowerCase();
             resId = context.getResources().getIdentifier(cleanName, "drawable", context.getPackageName());
             if (resId != 0) {
                 holder.ivLogo.setImageResource(resId);
                 logoSet = true;
             }
        }

        if (!logoSet && bank.getLogo() != null && bank.getLogo().startsWith("http")) {
             ImageLoader.load(holder.ivLogo, bank.getLogo());
        } else if (!logoSet) {
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
    
    private String getBankLogoResourceName(Bank bank) {
        String code = bank.getCode() != null ? bank.getCode().toUpperCase() : "";
        
        // Map specific codes to the drawable names existing in the project
        switch (code) {
            case "VCB": return "ic_bank_vietcombank";
            case "TCB": return "ic_bank_techcombank";
            case "MB": return "ic_bank_mb_bank";
            case "ACB": return "ic_bank_acb";
            case "VPB": return "ic_bank_vpbank";
            case "TPB": return "ic_bank_tpbank";
            case "STB": return "ic_bank_scb"; // Sacombank
            case "BIDV": return "ic_bank_bidv";
            case "CTG": return "ic_bank_vietinbank";
            case "ICB": return "ic_bank_vietinbank";
            case "HDB": return "ic_bank_hdbank";
            case "VIB": return "ic_bank_vib";
            case "MSB": return "ic_bank_msb";
            case "SHB": return "ic_bank_shb";
            case "OCB": return "ic_bank_ocb";
            case "BAB": return "ic_bank_bac_a_bank";
            case "LPB": return "ic_bank_lienviet_postbank";
            case "NAB": return "ic_bank_nam_a_bank";
            case "SSB": return "ic_bank_sea_bank";
            case "EIB": return "ic_bank_eximbank";
            case "VCCB": return "ic_bank_viet_capital_bank"; // Ban Viet
            // Add other mappings based on the drawable list
            default:
                // Default pattern: ic_bank_ + lowercase code/shortname
                // Use ShortName as it is often "VCB", "MB" etc, but here we want the name part often?
                // Actually user said: "ic_bank_*{bankName}.xml"
                // If bank.getName() is "Vietcombank", then "ic_bank_vietcombank"
                // But bank name often has spaces or is long. 
                // Let's try to use the code or shortname first as it is more stable, but if not found, rely on manual mapping above.
                // If the manual mapping fails, we return a constructed string that might match if the code is descriptive (e.g. "Vietcombank" as code).
                if (bank.getCode() != null) {
                     return "ic_bank_" + bank.getCode().toLowerCase().replace(" ", "_");
                }
                return "";
        }
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