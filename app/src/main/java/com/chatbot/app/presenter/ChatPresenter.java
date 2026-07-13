package com.chatbot.app.presenter;

import com.chatbot.app.contract.ChatContract;
import com.chatbot.app.model.api.QwenClient;
import com.chatbot.app.model.entity.Conversation;
import com.chatbot.app.model.entity.Message;
import com.chatbot.app.model.repo.ChatRepository;
import com.chatbot.app.utils.MainThread;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChatPresenter implements ChatContract.Presenter {

    private ChatContract.View view;
    private final ChatRepository chatRepository;
    private final long conversationId;
    private final long userId;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private boolean isSending;

    public ChatPresenter(ChatContract.View view, ChatRepository chatRepository, long conversationId, long userId) {
        this.view = view;
        this.chatRepository = chatRepository;
        this.conversationId = conversationId;
        this.userId = userId;
    }

    @Override
    public void loadMessages() {
        executor.execute(() -> {
            Conversation conversation = chatRepository.getConversation(conversationId, userId);
            List<Message> messages = chatRepository.getMessages(conversationId);
            MainThread.run(() -> {
                if (view == null) return;
                if (conversation != null) {
                    view.setTitle(conversation.getTitle());
                }
                view.showMessages(messages);
            });
        });
    }

    @Override
    public void sendMessage(String text) {
        if (isSending) return;
        if (text == null || text.trim().isEmpty()) {
            view.showError("Please enter a message");
            return;
        }
        if (!QwenClient.isApiKeyConfigured()) {
            view.showError("API key not configured. Add QWEN_API_KEY in local.properties");
            return;
        }

        final String messageText = text.trim();
        isSending = true;
        view.showLoading(true);
        view.clearInput();

        executor.execute(() -> {
            Message userMessage = chatRepository.saveUserMessage(conversationId, messageText);
            MainThread.run(() -> {
                if (view != null) {
                    view.addMessage(userMessage);
                    view.showTypingIndicator(true);
                }
            });

            Conversation conversation = chatRepository.getConversation(conversationId, userId);
            String model = conversation != null ? conversation.getModel() : "qwen-plus";

            if (conversation != null && "New Chat".equals(conversation.getTitle())) {
                String autoTitle = messageText.length() > 40
                        ? messageText.substring(0, 40) + "..."
                        : messageText;
                chatRepository.renameConversation(conversationId, userId, autoTitle);
                MainThread.run(() -> {
                    if (view != null) {
                        view.setTitle(autoTitle);
                    }
                });
            }

            try {
                String aiReply = chatRepository.sendToAi(conversationId, model);
                Message assistantMessage = chatRepository.saveAssistantMessage(conversationId, aiReply);
                MainThread.run(() -> {
                    if (view != null) {
                        view.showTypingIndicator(false);
                        view.addMessage(assistantMessage);
                        view.showLoading(false);
                    }
                });
            } catch (Exception e) {
                MainThread.run(() -> {
                    if (view != null) {
                        view.showTypingIndicator(false);
                        view.showLoading(false);
                        view.showError("Failed to get AI response. Check internet and API key.");
                    }
                });
            } finally {
                isSending = false;
            }
        });
    }

    @Override
    public void renameConversation(String newTitle) {
        if (newTitle == null || newTitle.trim().isEmpty()) {
            view.showError("Title cannot be empty");
            return;
        }
        executor.execute(() -> {
            chatRepository.renameConversation(conversationId, userId, newTitle.trim());
            MainThread.run(() -> {
                if (view != null) {
                    view.setTitle(newTitle.trim());
                }
            });
        });
    }

    @Override
    public void deleteConversation() {
        executor.execute(() -> chatRepository.deleteConversation(conversationId, userId));
    }

    @Override
    public void destroy() {
        view = null;
        executor.shutdownNow();
    }
}
