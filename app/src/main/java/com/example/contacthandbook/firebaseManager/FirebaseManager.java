package com.example.contacthandbook.firebaseManager;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.contacthandbook.MainActivity;
import com.example.contacthandbook.fragment.home.HomeFragment;
import com.example.contacthandbook.model.Classes;
import com.example.contacthandbook.model.Feedback;
import com.example.contacthandbook.model.Mark;
import com.example.contacthandbook.model.Notification;
import com.example.contacthandbook.model.NotifyDestination;
import com.example.contacthandbook.model.Student;
import com.example.contacthandbook.model.Teacher;
import com.example.contacthandbook.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirebaseManager {
    private static final String TAG = "Firebase manager";
    private static final String USERS_CHILD = "users";
    private static final String STUDENT_CHILD = "students";
    private static final String CLASS_CHILD = "classes";
    private static final String TEACHER_CHILD = "teachers";
    private static final String NOTIFICATION_CHILD = "notifications";
    private static final String MARK_CHILD = "marks";
    private static final String FEEDBACK_CHILD = "feedback";

    private Context context;
    public FirebaseManager(Context context){
        this.context=context;
    }

    //-- Users child


    //Change password

    public void changePassword(String username, String currentPassword, String newPassword, FirebaseCallBack.SuccessCallBack callBack) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference userRef = database.getReference(USERS_CHILD).child(username).child("password");
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    String pwd = snapshot.getValue().toString();
                    Log.e(pwd, currentPassword);
                    if (pwd.equals(currentPassword)) {
                        userRef.setValue(newPassword);
                        callBack.onCallback(true);
                    }
                    else {

                        callBack.onCallback(false);
                    }
                }
                else {
                    callBack.onCallback(false);
                }

            }
            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }
    // Get user from username/id
    public void getUser(String username, FirebaseCallBack.UserCallBack callBack) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference userRef = database.getReference(USERS_CHILD).child(username);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                callBack.onCallback(user);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }

    // Check valid user login information
    public void checkUser(String username, String password, String role, FirebaseCallBack.ValidateCallBack callBack) {
        getUser(username, new FirebaseCallBack.UserCallBack() {
            @Override
            public void onCallback(User user) {
                if (user != null && username.equals(user.getUsername()) && password.equals(user.getPassword()) && role.equals(user.getRole())) {
                    callBack.onCallBack(true, user);
                }
                else {
                    callBack.onCallBack(false, user);
                }
            }

        });
    }

    // --- Students child

    //get list of student
    public void getAllStudent(FirebaseCallBack.AllStudentCallBack callBack) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        Query studentQuery = database.getReference(STUDENT_CHILD).limitToLast(1000);
        studentQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                List<Student> students = new ArrayList<>();
                for (DataSnapshot studentSnapshot: snapshot.getChildren()) {
                    Student student = studentSnapshot.getValue(Student.class);
                    students.add(student);
                }
                callBack.onCallback(students);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }

    // Add student to studentList, add student account, parents account
    public void addStudent(Student student, FirebaseCallBack.AddStudentCallBack callBack) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        // Add student to student list
        DatabaseReference addStudentRef = database.getReference(STUDENT_CHILD).child(student.getId());
        addStudentRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                addStudentRef.setValue(student);
                callBack.onCallback(true);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callBack.onCallback(false);
            }
        });

        // Add student account
        DatabaseReference addStudentAccountRef = database.getReference(USERS_CHILD).child(student.getId());
        User stu = new User(student.getId(), "1", student.getName(), "Student");
        addStudentAccountRef.setValue(stu);

        // Add student to class
        DatabaseReference addStudentClass = database.getReference(CLASS_CHILD).child(student.getClassName()).child(student.getId());
        addStudentClass.setValue("student");

        //Add parent account
        DatabaseReference addParentAccountRef = database.getReference(USERS_CHILD).child(student.getId());
        User parents = new User(student.getId(), "1", student.getName(), "Parents");
        addParentAccountRef.setValue(parents);
    }


    // ----- Classes child

    public void loadClasses(String classNameParam, FirebaseCallBack.AllClassName callBack) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        Query classQuery = database.getReference(CLASS_CHILD).limitToLast(1000);
        classQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                List<Classes> classes = new ArrayList<>();
                for (DataSnapshot classSnapshot: snapshot.getChildren()) {
                    String className = classSnapshot.getKey();
                    if (className != null && classNameParam != null) {
                        if ((classNameParam.equals("All") || className.equals(classNameParam))) {
                            Teacher teacher = new Teacher();
                            List<Student> studentList = new ArrayList<>();
                            for (DataSnapshot child : classSnapshot.getChildren()) {
                                Log.w("CLASS", child.getValue().toString());
                                if (child.getKey().toString().equals("Teacher")) {
                                    teacher = new Teacher(child.getValue().toString());
                                } else {
                                    Student student = new Student(child.getKey());
                                    studentList.add(student);
                                }
                            }
                            Classes classes1 = new Classes(className, teacher, studentList);
                            classes.add(classes1);
                        }
                    }
                }
                callBack.onCallback(classes);
            }
            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }

    public void getClass(String className, FirebaseCallBack.SingleClass callBack) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference getClassRef = database.getReference(CLASS_CHILD).child(className).child("Teacher");
        getClassRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    callBack.onCallback(snapshot.getValue().toString());
                }
                else {
                    callBack.onCallback(null);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callBack.onCallback(null);
            }
        });
    }

    public void getClassName(String userId, String role, FirebaseCallBack.ClassNameCallback callback) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        Query userQuery = null;
        if (role.equals("Student")) {
            Log.e("USERNAME", userId);
            userQuery = database.getReference(STUDENT_CHILD).child(userId).child("className");
        }
        else if (role.equals("Teacher")) {
            userQuery = database.getReference(TEACHER_CHILD).child(userId).child("className");
        }
        else if (role.equals("Parent")) {

        }
        userQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    callback.onCallback(snapshot.getValue().toString());
                }
                else {
                    callback.onCallback(null);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onCallback(null);
            }
        });
    }


    //---Teachers child

    //Get list of all teachers
    public void getAllTeacher(FirebaseCallBack.AllTeacherCallBack callBack) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        Query teacherQuery = database.getReference(TEACHER_CHILD).limitToLast(1000);
        teacherQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                List<Teacher> teachers = new ArrayList<>();
                for (DataSnapshot teacherSnapshot: snapshot.getChildren()) {
                    Teacher teacher = teacherSnapshot.getValue(Teacher.class);
                    teachers.add(teacher);
                }
                callBack.onCallback(teachers);

            }
            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }

    public void getTeacher(String id, FirebaseCallBack.SingleTeacher callBack) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference getTeacherRef = database.getReference(TEACHER_CHILD).child(id);
        getTeacherRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Teacher teacher = snapshot.getValue(Teacher.class);
                callBack.onCallback(teacher);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callBack.onCallback(new Teacher());
            }
        });
    }

    //add teacher to teacher list, add teacher account, add teacher to class
    public void addTeacher(Teacher teacher, FirebaseCallBack.AddTeacherCallBack callBack) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(TEACHER_CHILD).child(teacher.getId());

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                myRef.setValue(teacher);

                callBack.onCallback(true);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callBack.onCallback(false);
            }
        });

        DatabaseReference addTeacherAccountRef = database.getReference(USERS_CHILD).child(teacher.getId());
        User tea = new User(teacher.getId(), "1", teacher.getName(), "Teacher");
        addTeacherAccountRef.setValue(tea);
        addTeacherToClass(teacher.getClassName(), teacher, new FirebaseCallBack.AddTeacherCallBack() {
            @Override
            public void onCallback(boolean success) {
            }
        });
    }

    public  void addTeacherToClass(String className, Teacher teacher, FirebaseCallBack.AddTeacherCallBack callBack) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference addTeacherRef = database.getReference(CLASS_CHILD).child(className).child("Teacher");
        addTeacherRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                addTeacherRef.setValue(teacher.getId());
                callBack.onCallback(true);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callBack.onCallback(false);
            }
        });
    }


    // ----Notification child

    public void addMessage(Notification notification, FirebaseCallBack.AddMessageCallBack callBack ) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(NOTIFICATION_CHILD).child(new Date().toString());
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                myRef.setValue(notification);
                callBack.onCallback(true);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callBack.onCallback(false);
            }
        });
    }

    public void loadNotification(NotifyDestination destination, FirebaseCallBack.AllNotificationCallBack callBack) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        Query studentQuery = database.getReference(NOTIFICATION_CHILD).limitToLast(1000);
        studentQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                List<Notification> notifications = new ArrayList<>();
                for (DataSnapshot notiSnapshot: snapshot.getChildren()) {
                    Notification notification = notiSnapshot.getValue(Notification.class);
                    if (notification.getDesitnation()== destination || destination == NotifyDestination.ALL )
                    {
                        notifications.add(notification);
                    }
                }
                callBack.onCallback(notifications);
            }
            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }


    //-- Marks child

    public void addMark(Student student, Mark mark, FirebaseCallBack.SuccessCallBack callBack) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference markRef = database.getReference(MARK_CHILD).child(String.valueOf(mark.getYear())).child(student.getId());
        markRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                markRef.setValue(mark);
                callBack.onCallback(true);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callBack.onCallback(false);
            }
        });
    }

    public void getMark(Student student, int year, FirebaseCallBack.GetMarkCallback callback) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference markRef = database.getReference(MARK_CHILD).child(String.valueOf(year)).child(student.getId());
        markRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    callback.onCallback(snapshot.getValue(Mark.class));
                }
                else {
                    callback.onCallback(new Mark());
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onCallback(new Mark());
            }
        });
    }


    // Feedback child

    public void addFeedBack( Feedback feedBack, FirebaseCallBack.AddMessageCallBack callBack ) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(FEEDBACK_CHILD).child(new Date().toString());
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                myRef.setValue(feedBack);
                callBack.onCallback(true);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callBack.onCallback(false);
            }
        });
    }


    public void loadFeedbackAll(FirebaseCallBack.AllFeedBackCallBack callBack) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        Query teacherQuery = database.getReference(FEEDBACK_CHILD).limitToLast(1000);
        teacherQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                List<Feedback> feedbacks = new ArrayList<>();
                for (DataSnapshot feedbackSnapshot: snapshot.getChildren()) {
                    Feedback feedback = feedbackSnapshot.getValue(Feedback.class);
                    feedbacks.add(feedback);
                }
                callBack.onCallback(feedbacks);

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }

}



