package com.helper.app.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.helper.app.R;
import com.helper.app.models.Message;
import com.helper.app.models.User;
import com.helper.app.utils.StorageHelper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MessagesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int VIEW_TYPE_ME = 1;
    private static final int VIEW_TYPE_OTHER = 2;

    private final List<Message> messages;
    private final User user;

    public MessagesAdapter(List<Message> messages) {
        this.messages = messages;
        user = StorageHelper.getCurrentUser();
    }

    public void add(Message messages) {
        this.messages.add(messages);
        notifyItemInserted(this.messages.size() - 1);
    }

    public void clear() {
        this.messages.clear();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        RecyclerView.ViewHolder holder = null;
        switch (viewType) {
            case VIEW_TYPE_ME:
                View viewChatMine = layoutInflater.inflate(R.layout.item_chat, parent, false);
                holder = new ChatViewHolder(viewChatMine);
                break;
            case VIEW_TYPE_OTHER:
                View viewChatOther = layoutInflater.inflate(R.layout.item_chat_other, parent, false);
                holder = new ChatOtherViewHolder(viewChatOther);
                break;
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (isOutComing(position)) {
            bindChatViewHolder((ChatViewHolder) holder, position);
        } else {
            bindChatOtherViewHolder((ChatOtherViewHolder) holder, position);
        }
    }

    private void bindChatViewHolder(ChatViewHolder holder, int position) {
        Message message = messages.get(position);
        String alphabet = message.getSenderName() != null && !message.getSenderName().isEmpty() ? message.getSenderName().substring(0, 1) : "N/A";
        holder.txtChatMessage.setText(message.getMessage());
        holder.time.setText(getTime(message.getTimestamp()).toUpperCase());
        holder.txtUserAlphabet.setText(alphabet);
        if (!isPreviousOutComing(position)) {
            holder.txtUserAlphabet.setText(alphabet);
            holder.txtUserAlphabet.setVisibility(View.VISIBLE);
        } else {
            holder.txtUserAlphabet.setVisibility(View.INVISIBLE);
        }
    }

    private void bindChatOtherViewHolder(ChatOtherViewHolder holder, int position) {
        Message message = messages.get(position);
        String alphabet = message.getSenderName() != null && !message.getSenderName().isEmpty() ? message.getSenderName().substring(0, 1) : "N/A";
        holder.txtChatMessage.setText(message.getMessage());
        holder.time.setText(getTime(message.getTimestamp()).toUpperCase());
        if (!isPreviousInComing(position)) {
            holder.txtUserAlphabet.setText(alphabet);
            holder.txtUserAlphabet.setVisibility(View.VISIBLE);
        } else {
            holder.txtUserAlphabet.setVisibility(View.INVISIBLE);
        }
    }

    private boolean isOutComing(int position) {
        return messages.get(position).getSenderId().equalsIgnoreCase(user.getId());
    }

    private boolean isPreviousOutComing(int position) {
        return position > 0 && isOutComing(position - 1);
    }

    private boolean isPreviousInComing(int position) {
        return position > 0 && !isOutComing(position - 1);
    }

    public String getTime(long milliseconds) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        return dateFormat.format(new Date(milliseconds));
    }

    @Override
    public int getItemCount() {
        if (messages != null) {
            return messages.size();
        }
        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        if (isOutComing(position)) {
            return VIEW_TYPE_ME;
        } else {
            return VIEW_TYPE_OTHER;
        }
    }

    public class ChatViewHolder extends RecyclerView.ViewHolder {
        TextView txtChatMessage;
        TextView txtUserAlphabet;
        TextView time;

        public ChatViewHolder(View view) {
            super(view);

            txtChatMessage = view.findViewById(R.id.text_view_chat_message);
            txtUserAlphabet = view.findViewById(R.id.text_view_user_alphabet);
            time = view.findViewById(R.id.time);
        }
    }

    public class ChatOtherViewHolder extends RecyclerView.ViewHolder {
        TextView txtChatMessage;
        TextView txtUserAlphabet;
        TextView time;

        public ChatOtherViewHolder(View view) {
            super(view);
            txtChatMessage = view.findViewById(R.id.text_view_chat_message);
            txtUserAlphabet = view.findViewById(R.id.text_view_user_alphabet);
            time = view.findViewById(R.id.time);
        }
    }
}

