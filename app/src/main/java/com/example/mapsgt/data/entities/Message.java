package com.example.mapsgt.data.entities;

import java.util.Date;

public class Message {
    private int id;
    private String senderId;
    private String receiverId;
    private String messageContent;
    private Date dateSent;
    private boolean isRead;

    public Message(String senderId, String receiverId, String messageContent, Date dateSent, boolean isRead) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.messageContent = messageContent;
        this.dateSent = dateSent;
        this.isRead = isRead;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public String getMessageContent() {
        return messageContent;
    }

    public void setMessageContent(String messageContent) {
        this.messageContent = messageContent;
    }

    public Date getDateSent() {
        return dateSent;
    }

    public void setDateSent(Date dateSent) {
        this.dateSent = dateSent;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }
}
