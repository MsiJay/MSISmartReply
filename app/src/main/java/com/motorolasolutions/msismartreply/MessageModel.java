package com.motorolasolutions.msismartreply;

public class MessageModel {
    String message;
    boolean fromMe;

    public MessageModel(String message, boolean fromMe) {
        this.message = message;
        this.fromMe = fromMe;
    }

    public String getMessage() {
        return message;
    }

    public boolean isFromMe() {
        return fromMe;
    }
}
