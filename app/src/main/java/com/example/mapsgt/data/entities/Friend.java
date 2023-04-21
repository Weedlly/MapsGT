package com.example.mapsgt.data.entities;

public class Friend {
    public String senderId;

    public String receiveId;
    public String date;
    public String status;

    public Friend()
    {

    }

    public void Friend(String senderId, String receiveId,String date, String status) {
        this.senderId = senderId;
        this.receiveId = receiveId;
        this.date = date;
        this.status = status;
    }

    public String getSenderId() { return senderId; }

    public String getReceiveId() { return receiveId; }

    public String getDate() { return date;}

    public String getStatus() { return status; }
}
