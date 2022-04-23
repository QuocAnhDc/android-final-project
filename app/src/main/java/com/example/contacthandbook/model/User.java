package com.example.contacthandbook.model;


public class User extends Common {
    String username;
    String password;
    String name = "";
    String role = "";

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String username, String password, String name, String role) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.role = role;
    }

    public String getName() { return this.name;}
    public String getPassword() {return  this.password;}
    public String getRole() { return  this.role;}
    public String getUsername() { return  this.username;}

    public void setUsername(String username) {
        this.username = username;
    }

    public void setName(String name) { this.name = name;}
    public  void setRole(String role) { this.role = role;}

}
