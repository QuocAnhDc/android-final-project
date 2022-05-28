package com.example.contacthandbook;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.crowdfire.cfalertdialog.CFAlertDialog;
import com.example.contacthandbook.firebaseManager.FirebaseManager;
import com.example.contacthandbook.firebaseManager.FirebaseCallBack.*;
import com.example.contacthandbook.model.User;
import com.google.firebase.database.DatabaseReference;
import com.kaopiz.kprogresshud.KProgressHUD;

import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LOGIN";
    private static final String PREFS_NAME = "USER_INFO" ;


    Spinner spinner_role;
    TextView textView;
    FirebaseManager firebaseManager = new FirebaseManager(this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getInfo()) {
            Intent dashboardIntent = new Intent(LoginActivity.this, MainActivity.class);
            LoginActivity.this.startActivity(dashboardIntent);
        }
        else {
            setContentView(R.layout.activity_login);
            String[] roleList = getResources().getStringArray(R.array.role);
            spinner_role = findViewById(R.id.spinner_role);
            ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>
                    (LoginActivity.this, android.R.layout.simple_spinner_dropdown_item, roleList);
            spinner_role.setAdapter(spinnerArrayAdapter);
            textView = findViewById(R.id.signUpButton);
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent dashboardIntent = new Intent(LoginActivity.this, RegisterActivity.class);
                    LoginActivity.this.startActivity(dashboardIntent);
                }
            });
        }
    }

    public void signIn(View view) {
        EditText usernameText = findViewById(R.id.emailEditText);
        String username = usernameText.getText().toString();
        EditText passwordText = findViewById(R.id.passwordEditText);
        String password = passwordText.getText().toString();
        String role = spinner_role.getSelectedItem().toString();
        if (!username.equals("") && !password.equals("")) {
            startLogin(username, password, role);
        }
        else {
            CFAlertDialog.Builder builder = new CFAlertDialog.Builder(LoginActivity.this)
                    .setDialogStyle(CFAlertDialog.CFAlertStyle.ALERT)
                    .setTitle("Warning!")
                    .setMessage("Please fill your username and password!")
                    .addButton("OK, I understand", -1, -1, CFAlertDialog.CFAlertActionStyle.DEFAULT, CFAlertDialog.CFAlertActionAlignment.END, (dialog, which) -> {
                        dialog.dismiss();
                    });

            builder.show();
        }

    }

    public void saveInfo(User user, boolean remember) {
        SharedPreferences.Editor editor = this.getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
        editor.putString("name", user.getName());
        editor.putString("role", user.getRole());
        editor.putString("username", user.getUsername());
        Log.w(TAG, user.getName());
        Log.w(TAG, user.getRole());
        editor.putBoolean("isRemember", remember);
        editor.apply();
    }

    public boolean getInfo() {
        SharedPreferences sharedPref = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean isRemember = sharedPref.getBoolean("isRemember", false);
        return  isRemember;
    }


    public  void startLogin(String username, String password, String role) {
        //show progressHUD
        KProgressHUD hud = KProgressHUD.create(LoginActivity.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f)
                .show();

        firebaseManager.checkUser(username, password, role, new ValidateCallBack() {
            @Override
            public void onCallBack(boolean isValidate, User user) {
                hud.dismiss();
                if (isValidate) {
                    // show dashboard
                    CheckBox remember = findViewById(R.id.remember);
                    saveInfo(user, true);
                    Intent dashboardIntent = new Intent(LoginActivity.this, MainActivity.class);
                    LoginActivity.this.startActivity(dashboardIntent);
                }
                else {
                    CFAlertDialog.Builder builder = new CFAlertDialog.Builder(LoginActivity.this)
                            .setDialogStyle(CFAlertDialog.CFAlertStyle.ALERT)
                            .setTitle("Invalid username or password!")
                            .setMessage("Please make sure your information is correct. Contact your admin if needed.")
                            .addButton("OK, I understand", -1, -1, CFAlertDialog.CFAlertActionStyle.DEFAULT, CFAlertDialog.CFAlertActionAlignment.END, (dialog, which) -> {
                                dialog.dismiss();
                            });

                    builder.show();
                }
            }

        });
    }
}