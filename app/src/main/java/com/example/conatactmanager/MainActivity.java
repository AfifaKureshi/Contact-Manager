package com.example.conatactmanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {
    TextInputLayout etuserName,etPassword;
    Button btnlogin;
    TextView txtRegister;
    DatabaseReference dbref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Intent i=getIntent();
        etuserName=findViewById(R.id.etuserName);
        etPassword=findViewById(R.id.etPassword);
        btnlogin=findViewById(R.id.btnlogin);
        txtRegister=findViewById(R.id.txtRegister);
        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = etuserName.getEditText().getText().toString().trim();
                String password = etPassword.getEditText().getText().toString().trim();

                // Check if the username and password are not empty
                if (!username.isEmpty() && !password.isEmpty()) {
                    readData(username, password);
                    etuserName.getEditText().setText("");
                    etPassword.getEditText().setText("");
                } else {
                    Toast.makeText(MainActivity.this, "Please enter both username and password", Toast.LENGTH_SHORT).show();
                }
            }
        });
        txtRegister.setOnClickListener(view -> {
            Intent intent=new Intent(this,Register.class);
            startActivity(intent);
        });

    }
    private void readData(String username, String password) {
        dbref= FirebaseDatabase.getInstance().getReference("Users");
        dbref.child(username).get().addOnSuccessListener(snapshot -> {
            if (snapshot.exists()) {
                String name = snapshot.child("name").getValue(String.class);
                String pass = snapshot.child("password").getValue(String.class);
                if(pass!=null){
                    if(pass.equals(password)){
                        Intent intent=new Intent(getApplicationContext(),ContactDirectory.class);
                        intent.putExtra("username",username);
                        intent.putExtra("name",name);
                        startActivity(intent);
                    }else{
                        Toast.makeText(this, "Wrong Password", Toast.LENGTH_SHORT).show();
                    }
                    Toast.makeText(this, "Welcome " + name, Toast.LENGTH_SHORT).show();}
            }else{
                Toast.makeText(this, "User Does Not Exist", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Failed" + e.getMessage(), Toast.LENGTH_SHORT).show();
        });

    }}