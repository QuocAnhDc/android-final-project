package com.example.contacthandbook.fragment.feedback;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.crowdfire.cfalertdialog.CFAlertDialog;
import com.example.contacthandbook.CommonFunction;
import com.example.contacthandbook.R;
import com.example.contacthandbook.firebaseManager.FirebaseCallBack;
import com.example.contacthandbook.firebaseManager.FirebaseManager;
import com.example.contacthandbook.model.Classes;
import com.example.contacthandbook.model.Feedback;
import com.example.contacthandbook.model.Notification;
import com.example.contacthandbook.model.NotifyDestination;
import com.example.contacthandbook.model.Student;
import com.example.contacthandbook.model.Teacher;
import com.example.contacthandbook.model.User;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.kaopiz.kprogresshud.KProgressHUD;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class FeedbackFragment  extends Fragment {
    private static final String PREFS_NAME = "USER_INFO";
    FirebaseManager firebaseManager = new FirebaseManager(getContext());
    FeedbackAdapter adapter;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.feedback_fragment, container, false);
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        FloatingActionButton fab = getView().findViewById(R.id.fab);
        User user = getSavedInfo();
        if (user.getRole().equals("Admin")) {
            fab.setVisibility(View.GONE);
        }
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddFeedBack();
            }
        });
        loadList();
    }

    public User getSavedInfo() {
        User user = new User();
        SharedPreferences sharedPref = getContext().getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        user.setName(sharedPref.getString("name", "Contact Handbook"));
        user.setRole(sharedPref.getString("role", "student"));
        user.setUsername(sharedPref.getString("username", "1"));
        return  user;
    }

    void showAddFeedBack() {
        View dialogLayout = LayoutInflater.from(getContext()).inflate(R.layout.add_feedback_dialog, null);
        TextView getSentTo = dialogLayout.findViewById(R.id.teacherName);
        TextInputEditText titleFeedback = dialogLayout.findViewById(R.id.title_feedback);
        TextInputEditText FeedbackEditText = dialogLayout.findViewById(R.id.feedback_editText);
        User user = getSavedInfo();

        Spinner spinnerDestination = dialogLayout.findViewById(R.id.spinner_student);
        ArrayList<String> arraymData = new ArrayList<String>();
        firebaseManager.getClassName(user.getUsername(), user.getRole(), new FirebaseCallBack.ClassNameCallback() {
            @Override
            public void onCallback(String className) {
                firebaseManager.getAllStudent(new FirebaseCallBack.AllStudentCallBack() {
                    public void onCallback(List<Student> students) {
                        for (Student student : students) {
                            if (student.getClassName().contains(className)) {
                                arraymData.add(student.getId());
                            }
                        }
                        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>
                                (getContext(), android.R.layout.simple_spinner_dropdown_item, arraymData);
                        spinnerDestination.setAdapter(spinnerArrayAdapter);
                    }
                });
            }
        });

        final String[] receiveID = new String[1];
        final String[] sendToStu = new String[1];

        if(user.getRole().equals("Student")) {
            spinnerDestination.setVisibility(View.GONE);
            firebaseManager.getClassName(user.getUsername(), user.getRole(), new FirebaseCallBack.ClassNameCallback() {
                @Override
                public void onCallback(String className) {
                    firebaseManager.getClass(className, new FirebaseCallBack.SingleClass() {
                        @Override
                        public void onCallback(String teacherId) {
                            receiveID[0] = teacherId;

                            firebaseManager.getTeacher(teacherId, new FirebaseCallBack.SingleTeacher() {
                                @Override
                                public void onCallback(Teacher teacher) {
                                    if (teacher.getName().equals("")) {
                                        getSentTo.setText("No teacher");
                                    } else {
                                        getSentTo.setText(teacher.getName());
                                    }
                                }
                            });

                        }
                    });

                }
            });
        }

        //Teacher can select Student, student only give feedback to their teacher
        if(user.getRole().equals("Teacher")){
            spinnerDestination.setVisibility(View.VISIBLE);
            getSentTo.setVisibility(View.GONE);
        }

        CFAlertDialog.Builder builder = new CFAlertDialog.Builder(getContext())
                .setDialogStyle(CFAlertDialog.CFAlertStyle.ALERT)
                .setHeaderView(dialogLayout)
                .addButton("SEND", -1, -1, CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (dialog, which) -> {
                    if (!titleFeedback.getText().toString().equals("") && !FeedbackEditText.getText().toString().equals("")) {
                        if(user.getRole().equals("Student")) {
                            Feedback feedback = new Feedback(titleFeedback.getText().toString(), FeedbackEditText.getText().toString(), receiveID[0], user.getUsername());
                            firebaseManager.addFeedBack(feedback, new FirebaseCallBack.AddMessageCallBack() {
                                @Override
                                public void onCallback(boolean success) {
                                    dialog.dismiss();
                                    resultAddAction(success);
                                }
                            });
                        }
                            if(user.getRole().equals("Teacher")){
                                if (spinnerDestination.getSelectedItem() != null) {
                                    sendToStu[0] =  spinnerDestination.getSelectedItem().toString();
                                }
                                Feedback feedback = new Feedback(titleFeedback.getText().toString(), FeedbackEditText.getText().toString(), sendToStu[0], user.getUsername());
                                firebaseManager.addFeedBack(feedback, new FirebaseCallBack.AddMessageCallBack() {
                                    @Override
                                    public void onCallback(boolean success) {
                                        dialog.dismiss();
                                        resultAddAction(success);
                                    }
                                });
                            }
                        }
                })
                .addButton("CANCEL", -1, -1, CFAlertDialog.CFAlertActionStyle.DEFAULT, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, ((dialog, which) -> {
                    dialog.dismiss();
                }));

        builder.show();
    }

    //show added feedback result depend on success value
    void resultAddAction(boolean success) {
        if (success) {
            CommonFunction.showCommonAlert(getContext(), "Message Sent", "OK");
            loadList();
        } else {
            CommonFunction.showCommonAlert(getContext(), "Something went wrong", "Let me check");
        }
    }


    //load or reload list
    void loadList() {
        User user = getSavedInfo();
        ArrayList<Feedback> arrayData = new ArrayList<Feedback>();
        //show progressHUD
        KProgressHUD hud = KProgressHUD.create(getContext())
                .setDetailsLabel("Loading feedback...")
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f)
                .show();

        RecyclerView recyclerView = getView().findViewById(R.id.feedbackList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        firebaseManager.loadFeedbackAll(new FirebaseCallBack.AllFeedBackCallBack() {
            @Override
            public void onCallback(List<Feedback> feedbacks) {
                for (Feedback feedback : feedbacks) {
                    if (feedback.getSender().contains(user.getUsername()) || feedback.getReciver().contains(user.getUsername())) {
                        arrayData.add(feedback);
                    }
                }
                adapter = new FeedbackAdapter(getContext(), arrayData);
                adapter.setOnItemListenerListener(new FeedbackAdapter.OnItemListener() {
                    @Override
                    public void OnItemClickListener(View view, int position) {

                    }

                    @Override
                    public void OnItemLongClickListener(View view, int position) {

                    }
                });
                recyclerView.setAdapter(adapter);
                hud.dismiss();
            }
        });
    }


}
