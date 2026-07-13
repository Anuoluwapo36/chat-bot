package com.chatbot.app;

import android.app.Application;
import com.chatbot.app.model.db.DatabaseHelper;
import com.chatbot.app.model.SessionManager;
import com.chatbot.app.model.ThemeManager;

public class ChatBotApp extends Application {

    private static ChatBotApp instance;
    private DatabaseHelper databaseHelper;
    private SessionManager sessionManager;
    private ThemeManager themeManager;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        databaseHelper = new DatabaseHelper(this);
        sessionManager = new SessionManager(this);
        themeManager = new ThemeManager(this);
        themeManager.applyTheme();
    }

    public static ChatBotApp getInstance() {
        return instance;
    }

    public DatabaseHelper getDatabaseHelper() {
        return databaseHelper;
    }

    public SessionManager getSessionManager() {
        return sessionManager;
    }

    public ThemeManager getThemeManager() {
        return themeManager;
    }
}
