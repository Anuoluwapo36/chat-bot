package com.chatbot.app.model.repo;

import com.chatbot.app.model.api.QwenClient;
import com.chatbot.app.model.api.QwenMessage;
import com.chatbot.app.model.api.QwenRequest;
import com.chatbot.app.model.api.QwenResponse;
import com.chatbot.app.model.db.DatabaseHelper;
import com.chatbot.app.model.entity.Conversation;
import com.chatbot.app.model.entity.Message;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Response;

public class ChatRepository {

    private final DatabaseHelper db;

    public ChatRepository(DatabaseHelper db) {
        this.db = db;
    }

    public long createConversation(long userId, String title, String model) {
        return db.createConversation(userId, title, model);
    }

    public List<Conversation> getConversations(long userId) {
        return db.getConversations(userId);
    }

    public Conversation getConversation(long conversationId, long userId) {
        return db.getConversation(conversationId, userId);
    }

    public boolean renameConversation(long conversationId, long userId, String title) {
        return db.updateConversationTitle(conversationId, userId, title);
    }

    public boolean deleteConversation(long conversationId, long userId) {
        return db.deleteConversation(conversationId, userId);
    }

    public void deleteAllConversations(long userId) {
        db.deleteAllConversationsForUser(userId);
    }

    public List<Message> getMessages(long conversationId) {
        return db.getMessages(conversationId);
    }

    public Message saveUserMessage(long conversationId, String content) {
        long id = db.saveMessage(conversationId, Message.ROLE_USER, content);
        return new Message(id, conversationId, Message.ROLE_USER, content, System.currentTimeMillis());
    }

    public Message saveAssistantMessage(long conversationId, String content) {
        long id = db.saveMessage(conversationId, Message.ROLE_ASSISTANT, content);
        return new Message(id, conversationId, Message.ROLE_ASSISTANT, content, System.currentTimeMillis());
    }

    public String sendToAi(long conversationId, String model) throws IOException {
        List<Message> messages = db.getMessages(conversationId);
        List<QwenMessage> apiMessages = new ArrayList<>();
        apiMessages.add(new QwenMessage(Message.ROLE_SYSTEM,
                "You are a helpful AI assistant in the Chat Bot app. Be concise, friendly, and accurate."));

        for (Message message : messages) {
            if (Message.ROLE_USER.equals(message.getRole()) ||
                    Message.ROLE_ASSISTANT.equals(message.getRole())) {
                apiMessages.add(new QwenMessage(message.getRole(), message.getContent()));
            }
        }

        QwenRequest request = new QwenRequest(model, apiMessages);
        Response<QwenResponse> response = QwenClient.getService()
                .chat(QwenClient.getAuthHeader(), request)
                .execute();

        if (!response.isSuccessful() || response.body() == null) {
            String error = response.errorBody() != null ? response.errorBody().string() : "Unknown error";
            throw new IOException("API error: " + error);
        }

        String content = response.body().getContent();
        if (content == null || content.trim().isEmpty()) {
            throw new IOException("Empty response from AI");
        }
        return content.trim();
    }
}
