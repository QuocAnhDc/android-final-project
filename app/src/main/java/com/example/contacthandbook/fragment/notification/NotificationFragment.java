package com.example.contacthandbook.fragment.notification;

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

import com.crowdfire.cfalertdialog.CFAlertDialog;
import com.example.contacthandbook.CommonFunction;
import com.example.contacthandbook.R;
import com.example.contacthandbook.firebaseManager.FirebaseCallBack;
import com.example.contacthandbook.firebaseManager.FirebaseManager;
import com.example.contacthandbook.model.Feedback;
import com.example.contacthandbook.model.Notification;
import com.example.contacthandbook.model.User;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.example.contacthandbook.model.NotifyDestination;
import com.kaopiz.kprogresshud.KProgressHUD;

import java.util.List;
import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;

public class NotificationFragment extends Fragment {
    private static final String PREFS_NAME = "USER_INFO";
    FirebaseManager firebaseManager = new FirebaseManager(getContext());
    NotificationAdapter adapter;

    public NotificationFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_notification, container, false);
    }

    @Override
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
                showAddMessage(false, new Notification());
            }
        });
        loadList();

    }

    void showAddMessage(boolean isEdit, Notification nt) {
        String buttonTitle = "";
        if(isEdit){
            buttonTitle = "Edit";
        }
        else{
            buttonTitle = "Send";
        }

        View dialogLayout = LayoutInflater.from(getContext()).inflate(R.layout.add_notification_dialog, null);
        Spinner spinnerDestination = dialogLayout.findViewById(R.id.spinner_destination);

        final List<String> destinations = NotifyDestination.ALL.getAllCase();
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>
                (getContext(), android.R.layout.simple_spinner_dropdown_item, destinations);
        spinnerDestination.setAdapter(spinnerArrayAdapter);

        //set text for view
        TextInputEditText titleEditText = dialogLayout.findViewById(R.id.title_editText);
        TextInputEditText messageEditText = dialogLayout.findViewById(R.id.message_editText);
        titleEditText.setText(nt.getTitle());
        messageEditText.setText(nt.getContent());
        int index = destinations.indexOf(nt.getDesitnation().toString().toUpperCase());
        spinnerDestination.setSelection(index);

        //show dialog for edit or create new message
        CFAlertDialog.Builder builder = new CFAlertDialog.Builder(getContext())
                .setDialogStyle(CFAlertDialog.CFAlertStyle.ALERT)
                .setHeaderView(dialogLayout)
                .addButton(buttonTitle, -1, -1, CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (dialog, which) -> {
                    if (!titleEditText.getText().toString().equals("") && !messageEditText.getText().toString().equals("")) {
                        String destination = spinnerDestination.getSelectedItem().toString();
                        //Notification notification = new Notification(NotifyDestination.valueOf(destination), titleEditText.getText().toString(), messageEditText.getText().toString());
                        nt.setTitle(titleEditText.getText().toString());
                        nt.setContent(messageEditText.getText().toString());
                        nt.setDesitnation(NotifyDestination.valueOf(destination));
                        firebaseManager.addMessage(nt, new FirebaseCallBack.AddMessageCallBack() {
                            @Override
                            public void onCallback(boolean success) {
                                if (success) {
                                    dialog.dismiss();
                                    CommonFunction.showCommonAlert(getContext(), "Message Sent", "OK");
                                    loadList();
                                }
                                else {
                                    CommonFunction.showCommonAlert(getContext(), "Something went wrong", "Let me check");
                                }
                            }
                        });
                    }
                })
                .addButton("CANCEL", -1, -1, CFAlertDialog.CFAlertActionStyle.DEFAULT, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, ((dialog, which) -> {
                    dialog.dismiss();
                }));

        builder.show();
    }

    void loadList() {
        NotifyDestination role = NotifyDestination.ALL;
        User user = getSavedInfo();
        if (user.getRole().equals("Admin")) {
            role = NotifyDestination.ALL;
        }
        if (user.getRole().equals("Student")) {
            role=NotifyDestination.STUDENT;
        }
        if (user.getRole().equals("Parent")) {
            role=NotifyDestination.PARENT;
        }
        if (user.getRole().equals("Teacher")) {
            role=NotifyDestination.TEACHER;
        }
        //show progressHUD
        KProgressHUD hud = KProgressHUD.create(getContext())
                .setDetailsLabel("Loading notification")
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f)
                .show();

        RecyclerView recyclerView = getView().findViewById(R.id.notificationList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        firebaseManager.loadNotification(role, new FirebaseCallBack.AllNotificationCallBack() {
            @Override
            public void onCallback(List<Notification> notifications) {
                adapter = new NotificationAdapter(getContext(), notifications);
                adapter.setOnItemListenerListener(new NotificationAdapter.OnItemListener() {
                    @Override
                    public void OnItemClickListener(View view, int position) {

                    }

                    @Override
                    public void OnItemLongClickListener(View view, int position) {
                        if(user.getRole().equals("Admin")){
                            CFAlertDialog.Builder builder = new CFAlertDialog.Builder(getContext())
                                    .setDialogStyle(CFAlertDialog.CFAlertStyle.BOTTOM_SHEET)
                                    .setTitle("Your Notification")
                                    .setMessage("")
                                    .addButton("Edit", -1, -1, CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (dialog, which) -> {
                                        dialog.dismiss();
                                        showAddMessage(true, notifications.get(position));

                                    })
                                    .addButton("Delete", -1, -1, CFAlertDialog.CFAlertActionStyle.NEGATIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (dialog, which) -> {
                                        dialog.dismiss();
                                        deleteNotification(notifications.get(position));

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
    public User getSavedInfo() {
        User user = new User();
        SharedPreferences sharedPref = getContext().getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        user.setName(sharedPref.getString("name", "Contact Handbook"));
        user.setRole(sharedPref.getString("role", "student"));
        return  user;
    }

    void result(boolean success) {
        if (success) {
            CommonFunction.showCommonAlert(getContext(), "Message Sent", "OK");
            loadList();
        } else {
            CommonFunction.showCommonAlert(getContext(), "Something were wrong", "Let me check");
        }
    }

    public void deleteNotification(Notification nt){
        firebaseManager.deleteNotification(nt, new FirebaseCallBack.SuccessCallBack() {
            @Override
            public void onCallback(boolean success) {
                result(success);
            }
        });
    }



}