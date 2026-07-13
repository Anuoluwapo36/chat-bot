package com.chatbot.app.model.api;

import java.util.List;

public class QwenRequest {
    public String model;
    public List<QwenMessage> messages;

    public QwenRequest(String model, List<QwenMessage> messages) {
        this.model = model;
        this.messages = messages;
    }
}
