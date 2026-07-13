package com.chatbot.app.model.entity;

public class Conversation {
    private long id;
    private long userId;
    private String title;
    private String model;
    private long createdAt;
    private long updatedAt;

    public Conversation() {
    }

    public Conversation(long id, long userId, String title, String model, long createdAt, long updatedAt) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.model = model;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }
}
