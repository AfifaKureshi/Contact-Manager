package com.example.conatactmanager;

import static com.example.conatactmanager.ContactFind.REQUEST_CALL_PERMISSION;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ContactDirectory extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ContactAdapter adapter;
    private List<Contact> contactList = new ArrayList<>();
    private DatabaseReference dbRef;
    private String username;
    ImageView add;
    @SuppressLint({"MissingInflatedId", "WrongViewCast"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_directory);
        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        add=findViewById(R.id.add);
        add.setOnClickListener(view -> {
            Intent intent1 =new Intent(getApplicationContext(),ContactDetails.class);
            intent1.putExtra("username",username);
            startActivity(intent1);
        });
        // Initialize the adapter with the OnItemClickListener
        adapter = new ContactAdapter(contactList, new ContactAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Contact contact) {
                // Handle item click event
               Intent intent=new Intent(getApplicationContext(),ContactProfile.class);
               intent.putExtra("phone",contact.getPhone());
               intent.putExtra("username",username);
               startActivity(intent);

            }

            @Override
            public void onCallIconClick(Contact contact) {
                // Handle call icon click event
                String phone = contact.getPhone();
                if (ContextCompat.checkSelfPermission(ContactDirectory.this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(ContactDirectory.this, new String[]{android.Manifest.permission.CALL_PHONE}, REQUEST_CALL_PERMISSION);
                } else {
                    makePhoneCall(phone);
                }
            }
        });

        recyclerView.setAdapter(adapter);

        // Load contacts from Firebase
        loadContacts();
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.home) {
                    Intent intent = new Intent(ContactDirectory.this, ContactDirectory.class);
                    intent.putExtra("username", username);
                    startActivity(intent);
                } else if (item.getItemId() == R.id.search) {
                    Intent intent1 = new Intent(ContactDirectory.this, ContactFind.class);
                    intent1.putExtra("username", username);
                    startActivity(intent1);
                } else if (item.getItemId() == R.id.profile) {
                    Intent intent2 = new Intent(ContactDirectory.this, Profile.class);
                    intent2.putExtra("username", username);
                    startActivity(intent2);
                }
                return true;

            }});
    }

    private void loadContacts() {
        dbRef = FirebaseDatabase.getInstance().getReference("Users");

        dbRef.child(username).child("contacts").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                contactList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String email = snapshot.child("email").getValue(String.class);
                    String name = snapshot.child("name").getValue(String.class);
                    String phone = snapshot.getKey(); // Using phone as key
                    Contact contact = new Contact(name, email,phone);
                    contactList.add(contact);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
            }
        });

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


    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults, String phoneNumber) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CALL_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                makePhoneCall(phoneNumber);
            } else {
                Toast.makeText(this, "Permission DENIED", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
