package com.example.mapsgt.data.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "message",
        foreignKeys = {
                @ForeignKey(entity = User.class,
                        parentColumns = "user_id",
                        childColumns = "sender_id"),
                @ForeignKey(entity = User.class,
                        parentColumns = "user_id",
                        childColumns = "receiver_id")},
        indices = {@Index(value = {"sender_id", "receiver_id"})}
)
public class Message {
    @ColumnInfo(name = "message_id")
    @PrimaryKey(autoGenerate = true)
    private int id;
    @ColumnInfo(name = "sender_id")
    private String senderId;
    @ColumnInfo(name = "receiver_id")
    private String receiverId;
    @ColumnInfo(name = "message_content")
    private String messageContent;
    @ColumnInfo(name = "date_sent")
    private Date dateSent;
    @ColumnInfo(name = "is_read")
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
