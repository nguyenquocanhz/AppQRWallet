package com.nqatech.vqr.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.nqatech.vqr.R;
import com.nqatech.vqr.database.entity.NotificationHistory;
import com.nqatech.vqr.utils.CurrencyUtils; // Giả sử đã có hoặc dùng MoneyReaderUtils

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class NotificationHistoryAdapter extends RecyclerView.Adapter<NotificationHistoryAdapter.ViewHolder> {

    private List<NotificationHistory> list;

    public NotificationHistoryAdapter(List<NotificationHistory> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        NotificationHistory item = list.get(position);
        
        holder.tvTitle.setText(item.title);
        holder.tvContent.setText(item.content);
        
        // Format amount
        // Giả sử dùng CurrencyUtils.formatVND(long)
        holder.tvAmount.setText("+" + String.format(Locale.getDefault(), "%,.0f", item.amount) + " đ");
        
        // Format time
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        holder.tvTime.setText(sdf.format(item.timestamp));
        
        holder.tvPackage.setText(item.packageName);
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvContent, tvAmount, tvTime, tvPackage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvContent = itemView.findViewById(R.id.tvContent);
            tvAmount = itemView.findViewById(R.id.tvAmount);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvPackage = itemView.findViewById(R.id.tvPackage);
        }
    }
}