package com.example.contacthandbook.model;

import java.util.List;

public class Classes {
    String className;
    Teacher teacher;
    List<Student> students;

    public Classes() {

    }

    public  Classes(String className, Teacher teacher, List<Student> students) {
        this.className = className;
        this.teacher = teacher;
        this.students = students;
    }

    public String getClassName() {
        return className;
    }



    public Teacher getTeacher() {
        return teacher;
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public List<Student> getStudents() {
        return students;
    }

    public void setStudents(List<Student> students) {
        this.students = students;
    }
}

