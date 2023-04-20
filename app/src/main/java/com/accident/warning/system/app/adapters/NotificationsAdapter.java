package com.accident.warning.system.app.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.accident.warning.system.app.R;
import com.accident.warning.system.app.models.Message;
import com.accident.warning.system.app.models.Notification;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.ViewHolder> {
    private final List<Notification> notifications;
    private final OnItemClickListener listener;

    // data is passed into the constructor
    public NotificationsAdapter(List<Notification> notifications, OnItemClickListener listener) {
        this.notifications = notifications;
        this.listener = listener;
    }

    // inflates the row layout from xml when needed
    @Override
    public NotificationsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification, parent, false);
        return new NotificationsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(NotificationsAdapter.ViewHolder holder, int position) {
        Notification notification = notifications.get(position);

        holder.notification.setText(notification.getMessage());
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(notification.getTimestamp());
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
        holder.date.setText(format.format(calendar.getTime()));
    }

    private int getSize(String id) {
        return notifications.size();
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return notifications.size();
    }

    public interface OnItemClickListener {
        void onItemViewListener(int position);

        void onDeleteItemViewListener(int position);
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView notification;
        TextView date;
        TextView btnPin;
        TextView btnDelete;

        ViewHolder(View view) {
            super(view);
            notification = view.findViewById(R.id.notification);
            date = view.findViewById(R.id.date);
            btnDelete = view.findViewById(R.id.btn_delete);
            btnPin = view.findViewById(R.id.btn_pin);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) listener.onItemViewListener(getAdapterPosition());
                }
            });
            btnPin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) listener.onItemViewListener(getAdapterPosition());
                }
            });

            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) listener.onDeleteItemViewListener(getAdapterPosition());
                }
            });
        }
    }
}