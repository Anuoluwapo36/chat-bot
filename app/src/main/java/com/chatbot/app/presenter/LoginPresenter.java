package com.chatbot.app.presenter;

import com.chatbot.app.contract.LoginContract;
import com.chatbot.app.model.SessionManager;
import com.chatbot.app.model.entity.User;
import com.chatbot.app.model.repo.UserRepository;
import com.chatbot.app.utils.MainThread;
import com.chatbot.app.utils.ValidationUtils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LoginPresenter implements LoginContract.Presenter {

    private LoginContract.View view;
    private final UserRepository userRepository;
    private final SessionManager sessionManager;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public LoginPresenter(LoginContract.View view, UserRepository userRepository, SessionManager sessionManager) {
        this.view = view;
        this.userRepository = userRepository;
        this.sessionManager = sessionManager;
    }

    @Override
    public void login(String email, String password) {
        if (!ValidationUtils.isValidEmail(email)) {
            view.showError("Please enter a valid email address");
            return;
        }
        if (!ValidationUtils.isValidPassword(password)) {
            view.showError("Password must be at least 6 characters");
            return;
        }

        view.showLoading(true);
        executor.execute(() -> {
            User user = userRepository.login(email, password);
            MainThread.run(() -> {
                if (view == null) return;
                view.showLoading(false);
                if (user != null) {
                    sessionManager.saveSession(user.getId(), user.getEmail(), user.getDisplayName());
                    view.onLoginSuccess();
                } else {
                    view.showError("Invalid email or password");
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
