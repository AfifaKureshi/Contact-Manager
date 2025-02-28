package com.example.conatactmanager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ContactFind extends AppCompatActivity {
    public static final int REQUEST_CALL_PERMISSION = 1;
    Button btnFind;
    TextView detail;
    DatabaseReference dbref;
    private RecyclerView recyclerView;
    private ContactAdapter adapter;
    private List<Contact> contactList = new ArrayList<>();
    private DatabaseReference dbRef;
    TextInputLayout phone;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_contact_find);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Intent intent=getIntent();
        phone=findViewById(R.id.etPhone);
        detail=findViewById(R.id.detail);
        btnFind=findViewById(R.id.btnFind);
        String username=intent.getStringExtra("username").toString();

        btnFind.setOnClickListener(view -> {

            dbref= FirebaseDatabase.getInstance().getReference("Users");
            dbref.child(username).child("contacts").child(phone.getEditText().getText().toString()).get().addOnSuccessListener(snapshot -> {
                if (snapshot.exists()) {
                    String coname = snapshot.child("name").getValue(String.class);
                    String coem = snapshot.child("email").getValue(String.class);
                    detail.setText("Name:" + coname + "\n" + "Email:" + coem + "\n"+"Phone Number:" + phone.getEditText().getText().toString());
                }else{
                        Toast.makeText(this, "Contact Does Not Exist", Toast.LENGTH_SHORT).show();
                }
                }).addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed" + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
        });
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.home) {
                    Intent intent = new Intent(ContactFind.this, ContactDirectory.class);
                    intent.putExtra("username", username);
                    startActivity(intent);
                } else if (item.getItemId() == R.id.search) {
                    Intent intent1 = new Intent(ContactFind.this, ContactFind.class);
                    intent1.putExtra("username", username);
                    startActivity(intent1);
                } else if (item.getItemId() == R.id.profile) {
                    Intent intent2 = new Intent(ContactFind.this, Profile.class);
                    intent2.putExtra("username", username);
                    startActivity(intent2);
                }
                return true;

            }});

    }
    public void makePhoneCall(String phoneNumber) {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + phoneNumber));
        try {
            startActivity(callIntent);
        } catch (SecurityException e) {
            Toast.makeText(this, "Permission denied to make phone call", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CALL_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                String phoneNumber = phone.getEditText().getText().toString(); // Phone number should be a String
                makePhoneCall(phoneNumber);
            } else {
                Toast.makeText(this, "Permission DENIED", Toast.LENGTH_SHORT).show();
            }
        }
    }

}