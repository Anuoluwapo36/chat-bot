package com.chatbot.app.contract;

import com.chatbot.app.model.entity.Message;

import java.util.List;

public interface ChatContract {

    interface View {
        void showMessages(List<Message> messages);

        void addMessage(Message message);

        void showTypingIndicator(boolean visible);

        void showLoading(boolean loading);

        void showError(String message);

        void setTitle(String title);

        void clearInput();
    }

    interface Presenter {
        void loadMessages();

        void sendMessage(String text);

        void renameConversation(String newTitle);

        void deleteConversation();

        void destroy();
    }
}
