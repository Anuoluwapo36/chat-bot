package com.chatbot.app.presenter;

import com.chatbot.app.contract.SettingsContract;
import com.chatbot.app.model.SessionManager;
import com.chatbot.app.model.ThemeManager;
import com.chatbot.app.model.entity.User;
import com.chatbot.app.model.repo.ChatRepository;
import com.chatbot.app.model.repo.UserRepository;
import com.chatbot.app.utils.MainThread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SettingsPresenter implements SettingsContract.Presenter {

    private SettingsContract.View view;
    private final UserRepository userRepository;
    private final ChatRepository chatRepository;
    private final SessionManager sessionManager;
    private final ThemeManager themeManager;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public SettingsPresenter(SettingsContract.View view,
                             UserRepository userRepository,
                             ChatRepository chatRepository,
                             SessionManager sessionManager,
                             ThemeManager themeManager) {
        this.view = view;
        this.userRepository = userRepository;
        this.chatRepository = chatRepository;
        this.sessionManager = sessionManager;
        this.themeManager = themeManager;
    }

    @Override
    public void loadSettings() {
        User user = userRepository.getUser(sessionManager.getUserId());
        if (user != null && view != null) {
            view.showProfile(user.getDisplayName(), user.getEmail(), sessionManager.getDefaultModel());
            view.showTheme(themeManager.getThemeMode());
        }
    }

    @Override
    public void saveDisplayName(String name) {
        if (name == null || name.trim().length() < 2) {
            view.showMessage("Name must be at least 2 characters");
            return;
        }
        executor.execute(() -> {
            userRepository.updateDisplayName(sessionManager.getUserId(), name.trim());
            sessionManager.setDisplayName(name.trim());
            MainThread.run(() -> {
                if (view != null) {
                    view.showMessage("Display name updated");
                    loadSettings();
                }
            });
        });
    }

    @Override
    public void saveModel(String model) {
        sessionManager.setDefaultModel(model);
        if (view != null) {
            view.showMessage("Default model updated");
            loadSettings();
        }
    }

    @Override
    public void saveTheme(int themeMode) {
        themeManager.setThemeMode(themeMode);
        if (view != null) {
            view.showTheme(themeMode);
            view.showMessage("Theme updated");
        }
    }

    @Override
    public void logout() {
        sessionManager.clearSession();
        if (view != null) {
            view.navigateToLogin();
        }
    }

    @Override
    public void clearAllChats() {
        executor.execute(() -> {
            chatRepository.deleteAllConversations(sessionManager.getUserId());
            MainThread.run(() -> {
                if (view != null) {
                    view.showMessage("All conversations cleared");
                }
            });
        });
    }

    @Override
    public void deleteAccount(String password) {
        executor.execute(() -> {
            User user = userRepository.getUser(sessionManager.getUserId());
            if (user == null) {
                MainThread.run(() -> {
                    if (view != null) view.showMessage("Account not found");
                });
                return;
            }
            User verified = userRepository.login(user.getEmail(), password);
            if (verified == null) {
                MainThread.run(() -> {
                    if (view != null) view.showMessage("Incorrect password");
                });
                return;
            }
            userRepository.deleteUser(sessionManager.getUserId());
            sessionManager.clearSession();
            MainThread.run(() -> {
                if (view != null) {
                    view.navigateToLogin();
                }
            });
        });
    }

    @Override
    public void destroy() {
        view = null;
        executor.shutdownNow();
    }
}
