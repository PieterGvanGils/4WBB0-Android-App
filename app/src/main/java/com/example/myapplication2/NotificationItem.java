package com.example.myapplication2;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Date;

public class NotificationItem {

    private String message;
    private String sessionKey;

    public NotificationItem() {
        // Needed for Firestore
    }

    public NotificationItem(String sessionKey, String message) {
        this.message = message;
        this.sessionKey = sessionKey;
    }

    @NonNull
    @Override
    public String toString() {
        return  "message='" + message +
                ", sessionKey='" + sessionKey;
    }

    public NotificationItem(String key, Object value) {
    }

    public String getMessage() {
        return message;
    }

    public String getSessionKey() {
        return sessionKey;
    }
}

