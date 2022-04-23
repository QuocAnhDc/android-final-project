package com.example.contacthandbook.model;

public class Student extends  Common {

    String id = "";
    String name = "";
    String className = "";
    public Student() {
    }
    public Student(String id) {
        this.id = id;
    }

    public Student(String id, String name, String className) {
        this.id = id;
        this.name = name;
        this.className = className;
    }

    public String getId() {
        return id;
    }
    public String getClassName() { return  this.className;}
    public  void setClassName(String className) { this.className = className;}
    public String getName() {
        return name;
    }
    public void setId(String id) {
        this.id = id;
    }
    public void setName(String name) {
        this.name = name;
    }
}
