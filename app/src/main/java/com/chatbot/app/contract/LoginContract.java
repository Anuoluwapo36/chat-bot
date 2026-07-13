package com.chatbot.app.contract;

public interface LoginContract {

    interface View {
        void showLoading(boolean loading);

        void showError(String message);

        void onLoginSuccess();
    }

    interface Presenter {
        void login(String email, String password);

        void destroy();
    }
}
