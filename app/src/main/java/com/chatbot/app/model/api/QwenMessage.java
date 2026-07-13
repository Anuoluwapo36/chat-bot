package com.chatbot.app.model.api;

public class QwenMessage {
    public String role;
    public String content;

    public QwenMessage(String role, String content) {
        this.role = role;
        this.content = content;
    }
}
