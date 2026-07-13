package com.chatbot.app.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.chatbot.app.R;
import com.chatbot.app.model.entity.Message;

import java.util.ArrayList;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_USER = 0;
    private static final int TYPE_ASSISTANT = 1;
    private static final int TYPE_TYPING = 2;

    private final List<Object> items = new ArrayList<>();
    private boolean showTyping;

    public void setMessages(List<Message> messages) {
        items.clear();
        items.addAll(messages);
        if (showTyping) {
            items.add("typing");
        }
        notifyDataSetChanged();
    }

    public void addMessage(Message message) {
        if (showTyping && !items.isEmpty() && "typing".equals(items.get(items.size() - 1))) {
            items.remove(items.size() - 1);
        }
        items.add(message);
        if (showTyping) {
            items.add("typing");
        }
        notifyDataSetChanged();
    }

    public void setTypingVisible(boolean visible) {
        showTyping = visible;
        if (visible) {
            if (items.isEmpty() || !"typing".equals(items.get(items.size() - 1))) {
                items.add("typing");
                notifyItemInserted(items.size() - 1);
            }
        } else {
            if (!items.isEmpty() && "typing".equals(items.get(items.size() - 1))) {
                items.remove(items.size() - 1);
                notifyItemRemoved(items.size());
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        Object item = items.get(position);
        if (item instanceof Message) {
            Message message = (Message) item;
            return Message.ROLE_USER.equals(message.getRole()) ? TYPE_USER : TYPE_ASSISTANT;
        }
        return TYPE_TYPING;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == TYPE_USER) {
            return new MessageViewHolder(inflater.inflate(R.layout.item_message_user, parent, false));
        } else if (viewType == TYPE_ASSISTANT) {
            return new MessageViewHolder(inflater.inflate(R.layout.item_message_assistant, parent, false));
        }
        return new TypingViewHolder(inflater.inflate(R.layout.item_message_typing, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof MessageViewHolder) {
            ((MessageViewHolder) holder).bind((Message) items.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class MessageViewHolder extends RecyclerView.ViewHolder {
        private final TextView messageText;

        MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.messageText);
        }

        void bind(Message message) {
            messageText.setText(message.getContent());
        }
    }

    static class TypingViewHolder extends RecyclerView.ViewHolder {
        TypingViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
