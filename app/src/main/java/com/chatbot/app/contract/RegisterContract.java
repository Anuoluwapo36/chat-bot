package com.chatbot.app.contract;

public interface RegisterContract {

    interface View {
        void showLoading(boolean loading);

        void showError(String message);

        void onRegisterSuccess();
    }

    interface Presenter {
        void register(String name, String email, String password, String confirmPassword);

        void destroy();
    }
}
