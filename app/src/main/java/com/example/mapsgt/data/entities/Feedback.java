package com.example.mapsgt.data.entities;

public class Feedback {
    private String name;

    private String email;

    private String message;

    public  Feedback(String name, String email ,String message){
        this.name = name;
        this.email = email;
        this.message = message;
    }

    public String getName() { return name;}

    public String getMessage() {return message;}

    public String getEmail() {return email;}

}
