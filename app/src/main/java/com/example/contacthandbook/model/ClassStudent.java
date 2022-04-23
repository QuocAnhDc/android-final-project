package com.example.contacthandbook.model;

import java.util.List;

public class ClassStudent extends Common{
    List<Student> studentList ;
    String classID;
    Teacher teacher;
    String className;

    public ClassStudent(){

    }
    public ClassStudent (String classID, List<Student> student, Teacher teacher, String className) {
        this.studentList = student;
        this.classID= classID;
        this.teacher = teacher;
        this.className= className;
    }

    public List<Student> getStudentList() {
        return studentList;
    }

    public void setStudentList(List<Student> studentList) {
        this.studentList = studentList;
    }

    public String getClassID() {
        return classID;
    }

    public Teacher getTeacher() {
        return teacher;
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
    }

    public void setClassID(String classID) {
        this.classID = classID;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }
}




