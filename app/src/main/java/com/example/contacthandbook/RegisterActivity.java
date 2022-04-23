package com.example.contacthandbook;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.contacthandbook.model.User;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {
    Spinner spinner_role;
    EditText txtMSSV,txtName,txtPass;
    Button btnRegister;

    private DatabaseReference mDatabase;
    //We will implement this function later
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        String[] roleList = getResources().getStringArray(R.array.role);
        spinner_role = findViewById(R.id.spinner_role);
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>
                (RegisterActivity.this, android.R.layout.simple_spinner_dropdown_item, roleList);
        spinner_role.setAdapter(spinnerArrayAdapter);
        txtMSSV = findViewById(R.id.txtMSSV);
        txtName = findViewById(R.id.txtName);
        txtPass = findViewById(R.id.txtPass);
        btnRegister = findViewById(R.id.signInButton);

        //dang ky
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String role = spinner_role.getSelectedItem().toString();
                writeNewUser(txtMSSV.getText().toString(),txtPass.getText().toString(),txtName.getText().toString(),role);
            }
        });
    }
    public void writeNewUser(String username, String password, String name, String role) {
        User user = new User(username, password,name,role);

        mDatabase.child("users").child(username).setValue(user);
    }
}