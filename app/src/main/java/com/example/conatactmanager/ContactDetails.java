package com.example.conatactmanager;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.conatactmanager.databinding.ActivityMainBinding;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class ContactDetails extends AppCompatActivity {
    DatabaseReference dbref;
    Button btnadd;
    TextInputLayout etNamead,etMailad,etPhonead;
    String name,username;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_contact_details);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        dbref= FirebaseDatabase.getInstance().getReference("Users");
        etNamead=findViewById(R.id.etNamead);
        etMailad=findViewById(R.id.etMailad);
        etPhonead=findViewById(R.id.etPhonead);
        btnadd=findViewById(R.id.btnadd);
        Intent intent=getIntent();
        username=intent.getStringExtra("username").toString();
        btnadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String coname = etNamead.getEditText().getText().toString().trim();
                String email = etMailad.getEditText().getText().toString().trim();
                String phone = etPhonead.getEditText().getText().toString().trim();

                // Check if the username is not null or empty
                if (username == null || username.isEmpty()) {
                    Toast.makeText(getBaseContext(), "Username is missing", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Validate other inputs (optional but recommended)
                if (coname.isEmpty() || email.isEmpty() || phone.isEmpty()) {
                    Toast.makeText(getBaseContext(), "Please fill all the fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Create a unique key for each contact


                Map<String, Object> contactData = new HashMap<>();
                contactData.put("name", coname);
                contactData.put("email", email);

                // Set the data in the database under the generated unique key
                dbref.child(username).child("contacts").child(phone).setValue(contactData).addOnSuccessListener(aVoid -> {
                            // Success callback
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(ContactDetails.this);
                    builder1.setTitle("Successful");
                    builder1.setMessage("Contact Created Successfully");
                    builder1.setPositiveButton("ok", (dialogInterface, i) -> {
                        onBackPressed();
                    });

                    builder1.show();

                }).addOnFailureListener(e -> {
                            // Failure callback
                            Toast.makeText(getBaseContext(), "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
                etNamead.getEditText().setText("");
                etMailad.getEditText().setText("");
                etPhonead.getEditText().setText("");
            }
        });



    }
}