package com.chatbot.app.contract;

import com.chatbot.app.model.entity.Conversation;

import java.util.List;

public interface MainContract {

    interface View {
        void showConversations(List<Conversation> conversations);

        void showEmptyState(boolean empty);

        void showLoading(boolean loading);

        void showError(String message);

        void openChat(long conversationId, String title);

        void openNewChat(long conversationId);
    }

    interface Presenter {
        void loadConversations();

        void createNewConversation();

        void openConversation(long conversationId);

        void deleteConversation(long conversationId);

        void renameConversation(long conversationId, String newTitle);

        void destroy();
    }
}
