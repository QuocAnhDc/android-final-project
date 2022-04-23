package com.example.contacthandbook.model;

public class Mark {
    String studentID = "";
    int year = 2021;
    Double math = -1.0;
    Double physic = -1.0;
    Double chemistry = -1.0;
    Double literature = -1.0;
    String date = "";
    public Mark() {

    }

    public Double getLiterature() {
        return literature;
    }

    public void setLiterature(Double literature) {
        this.literature = literature;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Double getChemistry() {
        return chemistry;
    }

    public void setChemistry(Double chemistry) {
        this.chemistry = chemistry;
    }

    public Double getPhysic() {
        return physic;
    }

    public void setPhysic(Double physic) {
        this.physic = physic;
    }

    public Double getMath() {
        return math;
    }

    public void setMath(Double math) {
        this.math = math;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getStudentID() {
        return studentID;
    }

    public void setStudentID(String studentID) {
        this.studentID = studentID;
    }

    public double getAveragePoint() {
        return (math + physic + chemistry + literature) / 4;
    }
}
