package com.example.conatactmanager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ContactEdit extends AppCompatActivity {
    Button btnedit;
    TextInputLayout etnameet,etemailet,etphoneet;
    DatabaseReference dbref;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_contact_edit);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        btnedit=findViewById(R.id.btnedit);
        etnameet=findViewById(R.id.etNameet);
        etemailet=findViewById(R.id.etMailet);
        etphoneet=findViewById(R.id.etPhoneet);
        Intent intent=getIntent();
        String username=intent.getStringExtra("username").toString();
        String phone=intent.getStringExtra("phone").toString();
        String name=intent.getStringExtra("name").toString();
        String email=intent.getStringExtra("email").toString();
        etnameet.getEditText().setText(name);
        etemailet.getEditText().setText(email);
        etphoneet.getEditText().setText(phone);
        dbref= FirebaseDatabase.getInstance().getReference("Users");
        btnedit.setOnClickListener(view -> {
            String coname=etnameet.getEditText().getText().toString();
            String coemail=etemailet.getEditText().getText().toString();
            String cophone=etphoneet.getEditText().getText().toString();
            dbref.child(username).child("contacts").child(cophone).child("name").setValue(coname);
            dbref.child(username).child("contacts").child(cophone).child("email").setValue(coemail).addOnSuccessListener(aVoid -> {
                // Success callback
                AlertDialog.Builder builder = new AlertDialog.Builder(ContactEdit.this);
                builder.setTitle("Success");
                builder.setMessage("Contact Edited Successfully");
                builder.setPositiveButton("ok", (dialogInterface1, i1) -> {
                    onBackPressed();
                });
                builder.show();

            }).addOnFailureListener(e -> {
                // Failure callback
                Toast.makeText(this, "Failed to delete contact: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
        });
    }
}