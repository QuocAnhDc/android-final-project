package com.example.contacthandbook.model;

import java.util.Date;

public class Feedback {
    String title = "";
    String content;
    String reciver;
    String sender;
    String dateStr = "";

    Date date;
    public Feedback(){

    }

    public Feedback(String title,String content,String reciver,String sender){
        this.title = title;
        this.content = content;
        this.reciver = reciver;
        this.sender = sender;
        this.dateStr = new Date().toString();
    }

    public String getContent(){return content;}
    public String getReciver(){return reciver;}
    public void setTeacher(String reciver) {
        this.reciver = reciver;
    }
    public String getSender(){return sender;}
    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public Date getDate() {
        return date;
    }

    public String getDateStr() {
        return dateStr;
    }


    public void setDate(Date date) {
        this.date = date;
        this.dateStr = date.toString();
    }
}
