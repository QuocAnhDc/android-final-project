package com.example.contacthandbook.model;

import java.util.Date;

public class Notification {
    NotifyDestination desitnation = NotifyDestination.ALL;
    String id;
    String title = "";
    String content = "";
    String dateStr = "";

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    Date date;

    public Notification() {
        this.dateStr = new Date().toString();
    }
    public Notification(NotifyDestination notifyDestination, String title, String content) {
        this.desitnation = notifyDestination;
        this.title = title;
        this.content = content;
        this.dateStr = new Date().toString();
    }

    public Notification(String id, NotifyDestination notifyDestination, String title, String content) {
        this.desitnation = notifyDestination;
        this.title = title;
        this.content = content;
        this.dateStr = new Date().toString();
        this.id = id;
    }


    public NotifyDestination getDesitnation() {
        return desitnation;
    }

    public String getContent() {
        return content;
    }

    public String getTitle() {
        return title;
    }

    public void setDestination(NotifyDestination notifyDestination) {
        this.desitnation = notifyDestination;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDesitnation(NotifyDestination desitnation) {
        this.desitnation = desitnation;
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


