package com.example.contacthandbook.fragment.classes;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.crowdfire.cfalertdialog.CFAlertDialog;
import com.example.contacthandbook.CommonFunction;
import com.example.contacthandbook.R;
import com.example.contacthandbook.firebaseManager.FirebaseCallBack;
import com.example.contacthandbook.firebaseManager.FirebaseManager;
import com.example.contacthandbook.fragment.students.StudentFragment;
import com.example.contacthandbook.fragment.students.StudentRecyclerAdapter;
import com.example.contacthandbook.model.Classes;
import com.example.contacthandbook.model.Teacher;
import com.example.contacthandbook.model.User;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.kaopiz.kprogresshud.KProgressHUD;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import static android.content.Context.MODE_PRIVATE;

public class ClassFragment extends Fragment {
    ClassRecyclerAdapter adapter;
    private static final String PREFS_NAME = "USER_INFO";
    FirebaseManager firebaseManager = new FirebaseManager(getContext());
    public ClassFragment() { }

    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // TODO: Use the ViewModel
        FloatingActionButton fab = getView().findViewById(R.id.fab);
        User user = getSavedInfo();
        if (!user.getRole().equals("Admin")) {
            fab.setVisibility(View.GONE);
        }
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog(new Classes(), true);
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


    //load or reload classes
    public void loadList() {
        //show progressHUD
        KProgressHUD hud = KProgressHUD.create(getContext())
                .setDetailsLabel("Loading classes")
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f)
                .show();
        RecyclerView recyclerView = getView().findViewById(R.id.studentList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        User user = getSavedInfo();
        if (user.getRole().equals("Admin")) {
            getClasses("All", hud, recyclerView);
        }
        else {
            firebaseManager.getClassName(user.getUsername(), user.getRole(), new FirebaseCallBack.ClassNameCallback() {
                @Override
                public void onCallback(String className) {
                    getClasses(className, hud, recyclerView);
                }
            });
        }
    }

    public void getClasses(String className, KProgressHUD hud, RecyclerView recyclerView) {
        firebaseManager.loadClasses(className, new FirebaseCallBack.AllClassName() {
            @Override
            public void onCallback(List<Classes> classes) {
                adapter = new ClassRecyclerAdapter(getContext(), classes);

                adapter.setOnItemListenerListener(new ClassRecyclerAdapter.OnItemListener() {
                    @SuppressLint("ResourceType")
                    @Override
                    public void OnItemClickListener(View view, int position) {
                        Intent classDetail = new Intent(getContext(), ClassDetailActivity.class);
                        classDetail.putExtra("className", classes.get(position).getClassName());
                        classDetail.putExtra("teacherId", classes.get(position).getTeacher().getId());
                        getContext().startActivity(classDetail);
                    }

                    @Override
                    public void OnItemLongClickListener(View view, int position) {
                        User user = getSavedInfo();
                        //Only admin can edit and delete class
                        if (user.getRole().equals("Admin")) {
                            String className = classes.get(position).getClassName();
                            CFAlertDialog.Builder builder = new CFAlertDialog.Builder(getContext())
                                    .setDialogStyle(CFAlertDialog.CFAlertStyle.BOTTOM_SHEET)
                                    .setTitle(className)
                                    .setMessage("")
                                    .addButton("Edit", -1, -1, CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (dialog, which) -> {
                                        dialog.dismiss();
                                        showDialog(classes.get(position), false);
                                    })
                                    .addButton("Delete", -1, -1, CFAlertDialog.CFAlertActionStyle.NEGATIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (dialog, which) -> {
                                        dialog.dismiss();
                                        Toast.makeText(getContext(), "Deleted", Toast.LENGTH_LONG).show();
                                    })
                                    .addButton("CANCEL", -1, -1, CFAlertDialog.CFAlertActionStyle.DEFAULT, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (dialog, which) -> {
                                        dialog.dismiss();
                                    });
                            builder.show();
                        }
                    }
                });
                recyclerView.setAdapter(adapter);
                hud.dismiss();
                adapter.notifyDataSetChanged();
            }
        });
    }


    //Show dialog for adding or editing class
    public void showDialog(Classes classes, boolean isAdd) {
        String buttonTitle = isAdd ? "Add" : "Edit";
        String message = isAdd ? "Added Class" : "Edited Class";
        firebaseManager.getAllTeacher(new FirebaseCallBack.AllTeacherCallBack() {
            @Override
            public void onCallback(List<Teacher> teachers) {
                View dialogLayout = LayoutInflater.from(getContext()).inflate(R.layout.class_dialog, null);
                TextInputEditText name = dialogLayout.findViewById(R.id.name_editText);
                name.setText(classes.getClassName());
                Spinner teacherSpinner = dialogLayout.findViewById(R.id.spinner_teacher);
                List<String> teacherList = new ArrayList<>();

                for (int i = 0; i< teachers.size(); i++) {
                    teacherList.add(teachers.get(i).getName());
                }

                ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>
                        (getContext(), android.R.layout.simple_spinner_dropdown_item, teacherList);
                teacherSpinner.setAdapter(spinnerArrayAdapter);

                if (!isAdd) {
                    teacherSpinner.setSelection(teacherList.indexOf(classes.getTeacher().getName()));
                }

                CFAlertDialog.Builder builder = new CFAlertDialog.Builder(getContext())
                        .setDialogStyle(CFAlertDialog.CFAlertStyle.ALERT)
                        .setHeaderView(dialogLayout)
                        .addButton(buttonTitle, -1, -1, CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (dialog, which) -> {
                            String teacherName = teacherSpinner.getSelectedItem().toString();
                            int index = teacherList.indexOf(teacherName);
                            Log.w("aaa", teachers.get(index).getId());
                            firebaseManager.addTeacherToClass(name.getText().toString(), teachers.get(index), new FirebaseCallBack.AddTeacherCallBack() {
                                @Override
                                public void onCallback(boolean success) {
                                    if (success) {
                                        dialog.dismiss();
                                        CommonFunction.showCommonAlert(getContext(), message, "OK");
                                        loadList(); //reload data
                                    }
                                    else {
                                        CommonFunction.showCommonAlert(getContext(), "Something went wrong", "Let me check");
                                    }
                                }
                            });
                        });

                builder.show();
            }
        });


    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_class, container, false);
    }
}