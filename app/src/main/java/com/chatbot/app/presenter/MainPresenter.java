package com.chatbot.app.presenter;

import com.chatbot.app.contract.MainContract;
import com.chatbot.app.model.SessionManager;
import com.chatbot.app.model.entity.Conversation;
import com.chatbot.app.model.repo.ChatRepository;
import com.chatbot.app.utils.MainThread;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainPresenter implements MainContract.Presenter {

    private MainContract.View view;
    private final ChatRepository chatRepository;
    private final SessionManager sessionManager;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private List<Conversation> cachedConversations;

    public MainPresenter(MainContract.View view, ChatRepository chatRepository, SessionManager sessionManager) {
        this.view = view;
        this.chatRepository = chatRepository;
        this.sessionManager = sessionManager;
    }

    @Override
    public void loadConversations() {
        view.showLoading(true);
        executor.execute(() -> {
            cachedConversations = chatRepository.getConversations(sessionManager.getUserId());
            MainThread.run(() -> {
                if (view == null) return;
                view.showLoading(false);
                view.showConversations(cachedConversations);
                view.showEmptyState(cachedConversations.isEmpty());
            });
        });
    }

    @Override
    public void createNewConversation() {
        view.showLoading(true);
        executor.execute(() -> {
            String model = sessionManager.getDefaultModel();
            long id = chatRepository.createConversation(sessionManager.getUserId(), "New Chat", model);
            MainThread.run(() -> {
                if (view == null) return;
                view.showLoading(false);
                if (id > 0) {
                    view.openNewChat(id);
                } else {
                    view.showError("Could not create conversation");
                }
            });
        });
    }

    @Override
    public void openConversation(long conversationId) {
        Conversation conversation = chatRepository.getConversation(conversationId, sessionManager.getUserId());
        if (conversation != null && view != null) {
            view.openChat(conversationId, conversation.getTitle());
        }
    }

    @Override
    public void deleteConversation(long conversationId) {
        executor.execute(() -> {
            chatRepository.deleteConversation(conversationId, sessionManager.getUserId());
            if (view != null) {
                loadConversations();
            }
        });
    }

    @Override
    public void renameConversation(long conversationId, String newTitle) {
        if (newTitle == null || newTitle.trim().isEmpty()) {
            view.showError("Title cannot be empty");
            return;
        }
        executor.execute(() -> {
            chatRepository.renameConversation(conversationId, sessionManager.getUserId(), newTitle.trim());
            if (view != null) {
                loadConversations();
            }
        });
    }

    @Override
    public void destroy() {
        view = null;
        executor.shutdownNow();
    }
}
