package com.example.contacthandbook.fragment.classes;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.crowdfire.cfalertdialog.CFAlertDialog;
import com.example.contacthandbook.CommonFunction;
import com.example.contacthandbook.LoginActivity;
import com.example.contacthandbook.R;
import com.example.contacthandbook.firebaseManager.FirebaseCallBack;
import com.example.contacthandbook.firebaseManager.FirebaseManager;
import com.example.contacthandbook.fragment.students.StudentFragment;
import com.example.contacthandbook.model.Mark;
import com.example.contacthandbook.model.Student;
import com.example.contacthandbook.model.Teacher;
import com.example.contacthandbook.model.User;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.core.Context;

import org.w3c.dom.Text;

import java.net.ContentHandler;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ClassDetailActivity extends AppCompatActivity {
    private static final String PREFS_NAME = "USER_INFO";
    TextView textViewTeacher;
    TextView textViewNumberStudent;
    TextView textViewYear;
    RecyclerView recyclerViewList;
    FirebaseManager firebaseManager = new FirebaseManager(this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        String className = intent.getStringExtra("className");
        String teacherId = intent.getStringExtra("teacherId");
        setTitle("Class:" + className);
        setContentView(R.layout.activity_class_detail);
        textViewTeacher = findViewById(R.id.homeroomTeacher);

        firebaseManager.getTeacher(teacherId, new FirebaseCallBack.SingleTeacher() {
            @Override
            public void onCallback(Teacher teacher) {
                if (teacher.getName().equals("")) {
                    textViewTeacher.setText("No Teacher");
                }
                else {
                    textViewTeacher.setText( teacher.getName());
                }
            }
        });

        int year =Calendar.getInstance().get(Calendar.YEAR);
        recyclerViewList = findViewById(R.id.studentInClass);
        recyclerViewList.setLayoutManager(new LinearLayoutManager(this));
        textViewNumberStudent = findViewById(R.id.numberOfStudent);
        textViewYear = findViewById(R.id.schoolYear);
        ArrayList<Student> arraymData = new ArrayList<Student>();
        firebaseManager.getAllStudent(new FirebaseCallBack.AllStudentCallBack() {
            @Override
            public void onCallback(List<Student> students) {

                    for (Student student : students) {
                        if (student.getClassName().contains(className)) {
                            arraymData.add(student);
                        }
                     }
                ClassDetailAdapter adapter = new ClassDetailAdapter(ClassDetailActivity.this,arraymData);
                    Log.e("STU", String.valueOf(arraymData.size()));
                adapter.setOnItemListenerListener(new ClassDetailAdapter.OnItemListener() {
                    @Override
                    public void OnItemClickListener(View view, int position) {
                        Student student_ = arraymData.get(position);
                        User user = getSavedInfo();
                        if (user.getRole().equals("Teacher")) {
                            showAddDialog(student_, true);
                        } else {
                            showAddDialog(student_, false);
                        }
                    }
                    @Override
                    public void OnItemLongClickListener(View view, int position) {

                    }
                });
                recyclerViewList.setAdapter(adapter);
                textViewYear.setText(String.valueOf(year));
                textViewNumberStudent.setText(String.valueOf(arraymData.size()));

            }
        });

    }

    void showAddDialog(Student student, boolean editable) {
        //Get child view
        View dialogLayout = LayoutInflater.from(ClassDetailActivity.this).inflate(R.layout.mark_dialog, null, false);
        TextInputEditText math = dialogLayout.findViewById(R.id.math_editText);
        TextInputEditText physic = dialogLayout.findViewById(R.id.physic_editText);
        TextInputEditText chemistry = dialogLayout.findViewById(R.id.chemistry_editText);
        TextInputEditText literature = dialogLayout.findViewById(R.id.literature_editText);
        TextView title = dialogLayout.findViewById(R.id.title);
        TextView name = dialogLayout.findViewById(R.id.name);
        TextView dateUpdated = dialogLayout.findViewById(R.id.date);
        Spinner spinnerYear = dialogLayout.findViewById(R.id.spinnerYear);

        int currentYear = Calendar.getInstance().get(Calendar.YEAR); //current Year
        String[] yearList = {String.valueOf(currentYear), String.valueOf(currentYear - 1), String.valueOf(currentYear - 2)}; // 3 years
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>
                (ClassDetailActivity.this, android.R.layout.simple_spinner_dropdown_item, yearList);
        spinnerYear.setAdapter(spinnerArrayAdapter);
        name.setText(student.getName());
        String okTitle = "Confirm";
        if (!editable) {
            math.setFocusable(false);
            math.setClickable(false);
            physic.setClickable(false);
            physic.setFocusable(false);
            chemistry.setClickable(false);
            chemistry.setFocusable(false);
            literature.setClickable(false);
            literature.setFocusable(false);
            okTitle = "OK";
            title.setText("View Mark");
        }

        //load mark in current year
        loadMark(student, currentYear, math, physic, chemistry, literature, dateUpdated);

        //update mark when change year
        spinnerYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                int selectedYear = Integer.parseInt(spinnerYear.getSelectedItem().toString());
                if (selectedYear != currentYear) {
                    loadMark(student, selectedYear, math, physic, chemistry, literature, dateUpdated);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {

            }
        });

        Mark mark = new Mark(); //init mark
        Date date = new Date(); //current date
        if (editable) {
            math.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    math.setText("");
                }
            });
            physic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    physic.setText("");
                }
            });
            chemistry.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    chemistry.setText("");
                }
            });
            literature.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    literature.setText("");
                }
            });
        }

        CFAlertDialog.Builder builder = new CFAlertDialog.Builder(ClassDetailActivity.this);
        builder
                .setDialogStyle(CFAlertDialog.CFAlertStyle.ALERT)
                .setHeaderView(dialogLayout)
                .addButton(okTitle, -1, -1, CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (dialog, which) -> {
                    if (editable) {
                        mark.setDate(date.toString());
                        mark.setMath(getMark(math.getText().toString()));
                        mark.setPhysic(getMark(physic.getText().toString()));
                        mark.setChemistry(getMark(chemistry.getText().toString()));
                        mark.setLiterature(getMark(literature.getText().toString()));
                        mark.setStudentID(student.getId());
                        mark.setYear(Integer.parseInt(spinnerYear.getSelectedItem().toString()));
                        firebaseManager.addMark(student, mark, new FirebaseCallBack.SuccessCallBack() {
                            @Override
                            public void onCallback(boolean success) {
                                dialog.dismiss();
                                if (success) {
                                    CommonFunction.showCommonAlert(ClassDetailActivity.this, "Updated mark for " + student.getName(), "OK");
                                } else {
                                    CommonFunction.showCommonAlert(ClassDetailActivity.this, "Something went wrong", "Let me check");
                                }
                            }
                        });
                    } else {
                        dialog.dismiss();
                    }
                });
        builder.show();

    }


    //load and update mark
    void loadMark(Student student, int selectedYear, TextInputEditText math, TextInputEditText physic, TextInputEditText chemistry, TextInputEditText literature, TextView dateUpdated) {
        firebaseManager.getMark(student, selectedYear, new FirebaseCallBack.GetMarkCallback() {
            @Override
            public void onCallback(Mark mark) {
                Log.e("MARK", "");
                dateUpdated.setText(mark.getDate());
                if (mark.getMath() != -1.0) {
                    math.setText(mark.getMath().toString());
                }
                if (mark.getPhysic() != -1.0)  {
                    physic.setText(mark.getPhysic().toString());
                }
                if (mark.getChemistry() != -1.0) {
                    chemistry.setText(mark.getChemistry().toString());
                }
                if (mark.getLiterature() != -1.0) {
                    literature.setText(mark.getLiterature().toString());
                }
            }
        });
    }


    //get user information in shared pref
    public User getSavedInfo() {
        User user = new User();
        SharedPreferences sharedPref = this.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        user.setName(sharedPref.getString("name", "Contact Handbook"));
        user.setRole(sharedPref.getString("role", "student"));
        user.setUsername(sharedPref.getString("username", "1"));
        return  user;
    }

    //parse String to double, return -1 when it give error
    Double getMark(String text) {
        try {
            return Double.parseDouble(text);
        } catch (NumberFormatException e) {
        }
        return -1.0;
    }
}



