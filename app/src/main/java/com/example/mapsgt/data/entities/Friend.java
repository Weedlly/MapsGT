package com.example.mapsgt.data.entities;

public class Friend {

    private String id;
    private String date;
    private String status;

    public Friend() {}

    public void Friend( String date, String status, String id) {
        this.date = date;
        this.status = status;
        this.id = id;
    }

    public String getID() { return id; }

    public String getDate() { return date;}

    public String getStatus() { return status; }
}
