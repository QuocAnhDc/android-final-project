package com.example.contacthandbook;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.crowdfire.cfalertdialog.CFAlertDialog;
import com.example.contacthandbook.firebaseManager.FirebaseCallBack;
import com.example.contacthandbook.firebaseManager.FirebaseManager;
import com.example.contacthandbook.model.User;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kaopiz.kprogresshud.KProgressHUD;

public class RegisterActivity extends AppCompatActivity {
    FirebaseManager firebaseDatabase;
    Spinner spinner_role;
    EditText txtMSSV,txtName,txtPass;
    Button btnRegister;
    TextView textView;

    private DatabaseReference mDatabase;
    //We will implement this function later
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        firebaseDatabase = new FirebaseManager(this);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        String[] roleList = getResources().getStringArray(R.array.role2);
        spinner_role = findViewById(R.id.spinner_role);
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>
                (RegisterActivity.this, android.R.layout.simple_spinner_dropdown_item, roleList);
        spinner_role.setAdapter(spinnerArrayAdapter);
        txtMSSV = findViewById(R.id.txtMSSV);
        txtName = findViewById(R.id.txtName);
        txtPass = findViewById(R.id.txtPass);
        btnRegister = findViewById(R.id.signInButton);
        textView = findViewById(R.id.logInButton);

        //dang ky
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String role = spinner_role.getSelectedItem().toString();
                writeNewUser(txtMSSV.getText().toString(),txtPass.getText().toString(),txtName.getText().toString(),role);
            }
        });

        // login neu co san tai khoan
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent dashboardIntent = new Intent(RegisterActivity.this, LoginActivity.class);
                RegisterActivity.this.startActivity(dashboardIntent);
            }
        });
    }
    public void writeNewUser(String username, String password, String name, String role) {

        KProgressHUD hud = KProgressHUD.create(RegisterActivity.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f)
                .show();
        User user = new User(username, password,name,role);
        firebaseDatabase.AddUser(user, new FirebaseCallBack.ValidateCallBack() {
            @Override
            public void onCallBack(boolean isValidate, User user) {
                hud.dismiss();
                if(isValidate){
                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                    RegisterActivity.this.startActivity(intent);
                }
                else{
                    CFAlertDialog.Builder builder = new CFAlertDialog.Builder(RegisterActivity.this)
                            .setDialogStyle(CFAlertDialog.CFAlertStyle.ALERT)
                            .setTitle("Error")
                            .setMessage("User existed, please try again")
                            .addButton("OK, I understand", -1, -1, CFAlertDialog.CFAlertActionStyle.DEFAULT, CFAlertDialog.CFAlertActionAlignment.END, (dialog, which) -> {
                                dialog.dismiss();
                            });

                    builder.show();
                }

            }
        });
    }
}