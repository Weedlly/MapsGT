package com.example.mapsgt.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "message")
public class Message {
    @ColumnInfo(name = "message_id")
    @PrimaryKey(autoGenerate = true)
    private int id;
    @ColumnInfo(name = "sender_id")
    private int senderId;
    @ColumnInfo(name = "receiver_id")
    private int receiverId;
    @ColumnInfo(name = "message_content")
    private String messageContent;
    @ColumnInfo(name = "date_sent")
    private Date dateSent;
    @ColumnInfo(name = "is_read")
    private boolean isRead;

    public Message(int id, int senderId, int receiverId, String messageContent, Date dateSent, boolean isRead) {
        this.id = id;
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

    public int getSenderId() {
        return senderId;
    }

    public void setSenderId(int senderId) {
        this.senderId = senderId;
    }

    public int getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(int receiverId) {
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
