package com.medyi.whoisup.model;

import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.ServerTimestamp;

import java.io.Serializable;
import java.util.Date;

public class Message implements Serializable {

    private User user;
    private String text;
    @ServerTimestamp
    private Date timestamp;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "Message{" +
                "user=" + user +
                ", text='" + text + '\'' +
                '}';
    }
}
