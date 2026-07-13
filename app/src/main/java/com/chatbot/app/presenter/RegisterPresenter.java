package com.chatbot.app.presenter;

import com.chatbot.app.contract.RegisterContract;
import com.chatbot.app.model.SessionManager;
import com.chatbot.app.model.repo.UserRepository;
import com.chatbot.app.utils.MainThread;
import com.chatbot.app.utils.ValidationUtils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RegisterPresenter implements RegisterContract.Presenter {

    private RegisterContract.View view;
    private final UserRepository userRepository;
    private final SessionManager sessionManager;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public RegisterPresenter(RegisterContract.View view, UserRepository userRepository, SessionManager sessionManager) {
        this.view = view;
        this.userRepository = userRepository;
        this.sessionManager = sessionManager;
    }

    @Override
    public void register(String name, String email, String password, String confirmPassword) {
        if (!ValidationUtils.isValidName(name)) {
            view.showError("Name must be at least 2 characters");
            return;
        }
        if (!ValidationUtils.isValidEmail(email)) {
            view.showError("Please enter a valid email address");
            return;
        }
        if (!ValidationUtils.isValidPassword(password)) {
            view.showError("Password must be at least 6 characters");
            return;
        }
        if (!password.equals(confirmPassword)) {
            view.showError("Passwords do not match");
            return;
        }

        view.showLoading(true);
        executor.execute(() -> {
            if (userRepository.emailExists(email)) {
                MainThread.run(() -> {
                    if (view != null) {
                        view.showLoading(false);
                        view.showError("An account with this email already exists");
                    }
                });
                return;
            }

            long userId = userRepository.register(email, password, name.trim());
            MainThread.run(() -> {
                if (view == null) return;
                view.showLoading(false);
                if (userId > 0) {
                    sessionManager.saveSession(userId, email.toLowerCase().trim(), name.trim());
                    view.onRegisterSuccess();
                } else {
                    view.showError("Registration failed. Please try again.");
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
