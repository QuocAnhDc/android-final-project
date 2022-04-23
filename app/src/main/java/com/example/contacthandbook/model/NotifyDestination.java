package com.example.contacthandbook.model;

import java.lang.reflect.Array;
import java.util.ArrayList;

public enum NotifyDestination {
    ALL,
    TEACHER,
    STUDENT,
    PARENT;

    public String toString(){
        switch(this) {
            case ALL:
                return "ALL";
            case TEACHER:
                return "TEACHER";
            case PARENT:
                return "PARENT";
            default: return "STUDENT";
        }
    }

    public ArrayList<String> getAllCase() {
        NotifyDestination[] allCase = NotifyDestination.values();
        ArrayList<String> strings = new ArrayList<>();
        for (int i = 0; i < allCase.length; i++) {
            strings.add(allCase[i].name());
        }
        return  strings;
    }
}