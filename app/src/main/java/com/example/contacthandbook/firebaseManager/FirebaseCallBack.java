package com.example.contacthandbook.firebaseManager;

import com.example.contacthandbook.model.Classes;
import com.example.contacthandbook.model.Feedback;
import com.example.contacthandbook.model.Mark;
import com.example.contacthandbook.model.Notification;
import com.example.contacthandbook.model.Student;
import com.example.contacthandbook.model.Teacher;
import com.example.contacthandbook.model.User;

import java.util.List;

public interface FirebaseCallBack {

    //Common
    interface SuccessCallBack {
        void onCallback(boolean success);
    }

    //Users child
    interface UserCallBack {
        void onCallback(User user);
    }

    interface ValidateCallBack {
        void onCallBack(boolean isValidate, User user);
    }

    //Student child
    interface AllStudentCallBack {
        void onCallback(List<Student> students);
    }

    interface AddStudentCallBack {
        void onCallback(boolean success);
    }

    //Teacher child
    interface AllTeacherCallBack {
        void onCallback(List<Teacher> teachers);
    }

    interface AddTeacherCallBack {
        void onCallback(boolean success);
    }

    interface SingleTeacher {
        void onCallback(Teacher teacher);
    }

    //Notification child
    interface AddMessageCallBack {
        void onCallback(boolean success);
    }

    interface AllNotificationCallBack {
        void onCallback(List<Notification> notifications);
    }


    //Feedback child
    interface AllFeedBackCallBack {
        void onCallback(List<Feedback> Feedback);
    }

    //Classes child
    interface  AllClassName {
        void onCallback(List<Classes> classes);
    }

    interface SingleClass {
        void onCallback(String teacherId);
    }

    interface GetMarkCallback {
        void onCallback(Mark mark);
    }

    interface ClassNameCallback {
        void onCallback(String className);
    }
}


