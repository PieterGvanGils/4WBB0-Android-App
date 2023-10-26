package com.example.myapplication2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.HashMap;
import java.util.Map;

public class NotificationHistoryAdapter extends RecyclerView.Adapter<NotificationHistoryAdapter.NotificationViewHolder> {

    private HashMap<String, Object> notificationItems;

    public NotificationHistoryAdapter(HashMap<String, Object> notificationItems) {
        this.notificationItems = notificationItems;
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_item, parent, false);
        return new NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        Map.Entry<String, Object> item = (Map.Entry<String, Object>) notificationItems.entrySet().toArray()[position];
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return notificationItems.size();
    }

    static class NotificationViewHolder extends RecyclerView.ViewHolder {

        private TextView sessionKeyTextView;
        private TextView messageTextView;

        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            sessionKeyTextView = itemView.findViewById(R.id.notification_sessionKey);
            messageTextView = itemView.findViewById(R.id.notification_message);
        }

        public void bind(Map.Entry<String, Object> item) {
            String key = item.getKey();
            Object value = item.getValue();
            sessionKeyTextView.setText(key);
            messageTextView.setText(value.toString());
        }
    }
}


