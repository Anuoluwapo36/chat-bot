package com.chatbot.app.contract;

public interface SettingsContract {

    interface View {
        void showProfile(String displayName, String email, String model);

        void showTheme(int themeMode);

        void showMessage(String message);

        void navigateToLogin();
    }

    interface Presenter {
        void loadSettings();

        void saveDisplayName(String name);

        void saveModel(String model);

        void saveTheme(int themeMode);

        void logout();

        void clearAllChats();

        void deleteAccount(String password);

        void destroy();
    }
}
