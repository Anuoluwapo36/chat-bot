package com.chatbot.app.model;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {

    private static final String PREFS = "chatbot_session";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_DISPLAY_NAME = "display_name";
    private static final String KEY_DEFAULT_MODEL = "default_model";
    private static final String DEFAULT_MODEL = "qwen-plus";

    private final SharedPreferences prefs;

    public SessionManager(Context context) {
        prefs = context.getApplicationContext().getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    public void saveSession(long userId, String email, String displayName) {
        prefs.edit()
                .putLong(KEY_USER_ID, userId)
                .putString(KEY_EMAIL, email)
                .putString(KEY_DISPLAY_NAME, displayName)
                .apply();
    }

    public void clearSession() {
        String model = getDefaultModel();
        prefs.edit().clear().apply();
        setDefaultModel(model);
    }

    public boolean isLoggedIn() {
        return prefs.getLong(KEY_USER_ID, -1) != -1;
    }

    public long getUserId() {
        return prefs.getLong(KEY_USER_ID, -1);
    }

    public String getEmail() {
        return prefs.getString(KEY_EMAIL, "");
    }

    public String getDisplayName() {
        return prefs.getString(KEY_DISPLAY_NAME, "User");
    }

    public void setDisplayName(String displayName) {
        prefs.edit().putString(KEY_DISPLAY_NAME, displayName).apply();
    }

    public String getDefaultModel() {
        return prefs.getString(KEY_DEFAULT_MODEL, DEFAULT_MODEL);
    }

    public void setDefaultModel(String model) {
        prefs.edit().putString(KEY_DEFAULT_MODEL, model).apply();
    }
}
