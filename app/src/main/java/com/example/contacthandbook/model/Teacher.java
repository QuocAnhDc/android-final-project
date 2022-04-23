package com.example.contacthandbook.model;

public class Teacher extends  Common {
    String id = "";
    String name = "";
    String className = "";

    public Teacher(){

    }

    public Teacher(String id) {
        this.id = id;
    }

    public Teacher(String id, String name, String className) {
        this.id = id;
        this.name = name;
        this.className = className;
    }

    public String getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public String getClassName() { return  this.className;}
    public void setId(String id) {
        this.id = id;
    }
    public void setName(String name) {
        this.name = name;
    }
    public  void setClassName(String className) { this.className = className;}
}
