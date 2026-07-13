package com.chatbot.app.model.repo;

import com.chatbot.app.model.db.DatabaseHelper;
import com.chatbot.app.model.entity.User;
import com.chatbot.app.utils.PasswordUtils;

public class UserRepository {

    private final DatabaseHelper db;

    public UserRepository(DatabaseHelper db) {
        this.db = db;
    }

    public long register(String email, String password, String displayName) {
        String hash = PasswordUtils.hashPassword(password);
        return db.registerUser(email, hash, displayName);
    }

    public User login(String email, String password) {
        User user = db.getUserByEmail(email);
        if (user != null && PasswordUtils.verifyPassword(password, user.getPasswordHash())) {
            return user;
        }
        return null;
    }

    public User getUser(long userId) {
        return db.getUserById(userId);
    }

    public boolean updateDisplayName(long userId, String displayName) {
        return db.updateDisplayName(userId, displayName);
    }

    public boolean deleteUser(long userId) {
        return db.deleteUser(userId);
    }

    public boolean emailExists(String email) {
        return db.getUserByEmail(email) != null;
    }
}
