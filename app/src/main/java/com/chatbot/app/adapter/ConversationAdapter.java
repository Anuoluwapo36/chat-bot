package com.chatbot.app.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.chatbot.app.R;
import com.chatbot.app.model.entity.Conversation;
import com.chatbot.app.utils.DateUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ConversationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    public interface OnConversationClickListener {
        void onConversationClick(Conversation conversation);

        void onConversationLongClick(Conversation conversation);
    }

    private final List<Object> items = new ArrayList<>();
    private final OnConversationClickListener listener;

    public ConversationAdapter(OnConversationClickListener listener) {
        this.listener = listener;
    }

    public void setConversations(List<Conversation> conversations) {
        items.clear();
        Map<String, List<Conversation>> grouped = new LinkedHashMap<>();
        for (Conversation conversation : conversations) {
            String label = DateUtils.getGroupLabel(conversation.getUpdatedAt());
            if (!grouped.containsKey(label)) {
                grouped.put(label, new ArrayList<>());
            }
            grouped.get(label).add(conversation);
        }
        for (Map.Entry<String, List<Conversation>> entry : grouped.entrySet()) {
            items.add(entry.getKey());
            items.addAll(entry.getValue());
        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return items.get(position) instanceof String ? TYPE_HEADER : TYPE_ITEM;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_conversation_header, parent, false);
            return new HeaderViewHolder(view);
        }
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_conversation, parent, false);
        return new ConversationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof HeaderViewHolder) {
            ((HeaderViewHolder) holder).bind((String) items.get(position));
        } else {
            ((ConversationViewHolder) holder).bind((Conversation) items.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class HeaderViewHolder extends RecyclerView.ViewHolder {
        private final TextView title;

        HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.headerTitle);
        }

        void bind(String label) {
            title.setText(label);
        }
    }

    class ConversationViewHolder extends RecyclerView.ViewHolder {
        private final TextView title;
        private final TextView time;

        ConversationViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.conversationTitle);
            time = itemView.findViewById(R.id.conversationTime);
        }

        void bind(Conversation conversation) {
            title.setText(conversation.getTitle());
            time.setText(DateUtils.formatConversationTime(conversation.getUpdatedAt()));
            itemView.setOnClickListener(v -> listener.onConversationClick(conversation));
            itemView.setOnLongClickListener(v -> {
                listener.onConversationLongClick(conversation);
                return true;
            });
        }
    }
}
