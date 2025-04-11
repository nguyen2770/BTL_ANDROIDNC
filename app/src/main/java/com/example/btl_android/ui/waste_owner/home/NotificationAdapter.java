package com.example.btl_android.ui.waste_owner.home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.btl_android.R;
import com.example.btl_android.data.model.Notification;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {
    private List<Notification> notifications = new ArrayList<>();
    private OnNotificationClickListener listener;

    public interface OnNotificationClickListener {
        void onNotificationClick(Notification notification);
        
        // Add long click functionality
        default void onNotificationLongClick(Notification notification) {
            // Optional implementation
        }
    }

    public void setOnNotificationClickListener(OnNotificationClickListener listener) {
        this.listener = listener;
    }

    public void setNotifications(List<Notification> notifications) {
        this.notifications = notifications;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_notification, parent, false);
        return new NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        Notification notification = notifications.get(position);
        holder.bind(notification);
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    class NotificationViewHolder extends RecyclerView.ViewHolder {
        private TextView titleText;
        private TextView messageText;
        private TextView timeText;
        private View unreadIndicator;

        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            titleText = itemView.findViewById(R.id.text_notification_title);
            messageText = itemView.findViewById(R.id.text_notification_message);
            timeText = itemView.findViewById(R.id.text_notification_time);
            unreadIndicator = itemView.findViewById(R.id.unread_indicator);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onNotificationClick(notifications.get(position));
                }
            });
            
            // Add long click listener
            itemView.setOnLongClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onNotificationLongClick(notifications.get(position));
                    return true;
                }
                return false;
            });
        }

        public void bind(Notification notification) {
            titleText.setText(notification.getTitle());
            messageText.setText(notification.getMessage());
            
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            String time = sdf.format(new Date(notification.getTimestamp()));
            timeText.setText(time);

            // Set read status visual indication
            if (notification.isRead()) {
                // Use context.getResources().getIdentifier for styles
                titleText.setAlpha(0.7f);
                if (unreadIndicator != null) {
                    unreadIndicator.setVisibility(View.GONE);
                }
                itemView.setBackgroundResource(R.drawable.bg_notification_read);
            } else {
                titleText.setAlpha(1.0f);
                if (unreadIndicator != null) {
                    unreadIndicator.setVisibility(View.VISIBLE);
                }
                itemView.setBackgroundResource(R.drawable.bg_notification_unread);
            }
        }
    }
} 