package com.example.conatactmanager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Profile extends AppCompatActivity {
    DatabaseReference dbref;
    TextView name,email,txtusername;
    String coname,coemail;
    String username;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        name=findViewById(R.id.name);
        email=findViewById(R.id.email);
        txtusername=findViewById(R.id.username);
        Intent intent=getIntent();
        username=intent.getStringExtra("username").toString();
        dbref= FirebaseDatabase.getInstance().getReference("Users");
        dbref.child(username).get().addOnSuccessListener(snapshot -> {
            if (snapshot.exists()) {
                username= snapshot.getKey();
                coname= snapshot.child("name").getValue(String.class);
                coemail = snapshot.child("email").getValue(String.class);
                name.setText(coname);
                email.setText("Email: "+coemail);
                txtusername.setText(username);
            }
        });
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.home) {
                    Intent intent = new Intent(Profile.this, ContactDirectory.class);
                    intent.putExtra("username", username);
                    startActivity(intent);
                } else if (item.getItemId() == R.id.search) {
                    Intent intent1 = new Intent(Profile.this, ContactFind.class);
                    intent1.putExtra("username", username);
                    startActivity(intent1);
                } else if (item.getItemId() == R.id.profile) {
                    Intent intent2 = new Intent(Profile.this, Profile.class);
                    intent2.putExtra("username", username);
                    startActivity(intent2);
                }
                return true;

            }});
    }
}