package com.example.contacthandbook.model;

public class Common {
    String id = "";
    String name = "";
    public Common() {

    }

    public Common(String id, String name) {
        this.name = name;
        this.id = id;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
