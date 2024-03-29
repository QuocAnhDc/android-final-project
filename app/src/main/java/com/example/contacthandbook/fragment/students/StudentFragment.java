package com.example.contacthandbook.fragment.students;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.crowdfire.cfalertdialog.CFAlertDialog;
import com.example.contacthandbook.CommonFunction;
import com.example.contacthandbook.R;
import com.example.contacthandbook.firebaseManager.FirebaseCallBack;
import com.example.contacthandbook.firebaseManager.FirebaseManager;
import com.example.contacthandbook.model.Common;
import com.example.contacthandbook.model.Student;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class StudentFragment extends Fragment {

    MaterialSearchView searchView;
    StudentRecyclerAdapter adapter;
    FirebaseManager firebaseManager = new FirebaseManager(getContext());
    public StudentFragment(MaterialSearchView searchView) {
        this.searchView = searchView;
    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.filter(newText.trim());
                return false;
            }
        });

        return inflater.inflate(R.layout.student_fragment, container, false);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.main_menu, menu);
        MenuItem item = menu.findItem(R.id.search_button);
        searchView.setMenuItem(item);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        loadList();
        FloatingActionButton fab = getView().findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddDialog(false, new Student());
            }
        });

    }

    void showAddDialog(boolean isEdit, Student student) {
        View dialogLayout = LayoutInflater.from(getContext()).inflate(R.layout.student_dialog, null);
        TextInputEditText id = dialogLayout.findViewById(R.id.id_editText);
        TextInputEditText name = dialogLayout.findViewById(R.id.name_editText);
        TextInputEditText classStr = dialogLayout.findViewById(R.id.class_editText);
        id.setText(student.getId());
        name.setText(student.getName());

        String buttonTitle = !isEdit ? "Add" : "Edit";
        String message = !isEdit ? "Added Student" : "Edited Student";
        classStr.setText(student.getClassName());
        CFAlertDialog.Builder builder = new CFAlertDialog.Builder(getContext())
                .setDialogStyle(CFAlertDialog.CFAlertStyle.ALERT)
                .setHeaderView(dialogLayout)
                .addButton(buttonTitle, -1, -1, CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (dialog, which) -> {

                    String inputId = id.getText().toString();
                    String inputName = name.getText().toString();
                    String inputClass = classStr.getText().toString();

                    if (!inputId.equals("") && !inputName.equals("") && !inputClass.equals("")) {
                        student.setId(id.getText().toString());
                        student.setName(name.getText().toString());
                        student.setClassName(classStr.getText().toString());
                        firebaseManager.addStudent(student, new FirebaseCallBack.AddStudentCallBack() {
                            @Override
                            public void onCallback(boolean success) {
                                if (success) {
                                    dialog.dismiss();
                                    CommonFunction.showCommonAlert(getContext(), message, "OK");
                                    loadList();
                                }
                                else {
                                    CommonFunction.showCommonAlert(getContext(), "Something went wrong", "Let me check");
                                }
                            }
                        });
                    }
                    else{
                        CommonFunction.showCommonAlert(getContext(), "you must fulfill all required information", "Let me check");
                    }

                });
        builder.show();
    }

    void loadList() {
        //show progressHUD
        KProgressHUD hud = KProgressHUD.create(getContext())
                .setDetailsLabel("Loading students")
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f)
                .show();

        RecyclerView recyclerView = getView().findViewById(R.id.studentList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        firebaseManager.getAllStudent(new FirebaseCallBack.AllStudentCallBack() {
            @Override
            public void onCallback(List<Student> students) {
                adapter = new StudentRecyclerAdapter(getContext(), students);
                adapter.setOnItemListenerListener(new StudentRecyclerAdapter.OnItemListener() {
                    @Override
                    public void OnItemClickListener(View view, int position) {

                    }

                    @Override
                    public void OnItemLongClickListener(View view, int position) {
                        Student student_ = students.get(position);
                        CFAlertDialog.Builder builder = new CFAlertDialog.Builder(getContext())
                                .setDialogStyle(CFAlertDialog.CFAlertStyle.BOTTOM_SHEET)
                                .setTitle(student_.getName())
                                .setMessage("")
                                .addButton("Edit", -1, -1, CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (dialog, which) -> {
                                    dialog.dismiss();
                                    showAddDialog(true, student_);
                                })
                                .addButton("Delete", -1, -1, CFAlertDialog.CFAlertActionStyle.NEGATIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (dialog, which) -> {
                                    dialog.dismiss();
                                    deleteStudent(student_);
                                })
                                .addButton("CANCEL", -1, -1, CFAlertDialog.CFAlertActionStyle.DEFAULT, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (dialog, which) -> {
                                    dialog.dismiss();
                                });
                        builder.show();
                    }
                });
                recyclerView.setAdapter(adapter);
                hud.dismiss();
                adapter.notifyDataSetChanged();
            }
        });

    }


    void deleteStudent(Student student){
        firebaseManager.deleteStudent(student, new FirebaseCallBack.DeleteStudentCallBack() {
            @Override
            public void onCallback(boolean success) {
                if(success){
                    CommonFunction.showCommonAlert(getContext(), "Delete success", "OK");
                    adapter.notifyDataSetChanged();
                    loadList();
                }
                else{
                    CommonFunction.showCommonAlert(getContext(), "Some thing was wrong", "OK");
                }
            }
        });
    }
}