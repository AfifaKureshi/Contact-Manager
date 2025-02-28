package com.example.conatactmanager;

import static com.example.conatactmanager.ContactFind.REQUEST_CALL_PERMISSION;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import de.hdodenhof.circleimageview.CircleImageView;

public class ContactProfile extends AppCompatActivity {
    DatabaseReference dbref;
    String phone;
    TextView name,email,txtphone;
    de.hdodenhof.circleimageview.CircleImageView call,edit,delete;
    String coname,coemail,cophone;
    String username;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_contact_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        name=findViewById(R.id.name);
        email=findViewById(R.id.email);
        txtphone=findViewById(R.id.phone);
        call=findViewById(R.id.call);
        edit=findViewById(R.id.edit);
        delete=findViewById(R.id.delete);
        Intent intent=getIntent();
        phone=intent.getStringExtra("phone").toString();
        username=intent.getStringExtra("username").toString();
        dbref= FirebaseDatabase.getInstance().getReference("Users");
        dbref.child(username).child("contacts").child(phone).get().addOnSuccessListener(snapshot -> {
            if (snapshot.exists()) {
                cophone = snapshot.getKey();
                coname= snapshot.child("name").getValue(String.class);
                coemail = snapshot.child("email").getValue(String.class);
                name.setText(coname);
                email.setText("Email: "+coemail);

                txtphone.setText("Phone: "+cophone);
            }
        });
        call.setOnClickListener(view -> {
            String phone = cophone;
            if (ContextCompat.checkSelfPermission(ContactProfile.this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(ContactProfile.this, new String[]{android.Manifest.permission.CALL_PHONE}, REQUEST_CALL_PERMISSION);
            } else {
                makePhoneCall(phone);
            }
        });
        delete.setOnClickListener(view -> {
            AlertDialog.Builder builder1 = new AlertDialog.Builder(ContactProfile.this);
            builder1.setTitle("Are You Sure?");
            builder1.setMessage("This Contact Will Be Deleted Permanently");
            builder1.setPositiveButton("Delete", (dialogInterface, i) -> {
                dbref.child(username).child("contacts").child(phone).removeValue().addOnSuccessListener(aVoid -> {
                    // Success callback
                    AlertDialog.Builder builder2 = new AlertDialog.Builder(ContactProfile.this);
                    builder2.setTitle("Deleted");
                    builder2.setMessage("Contact Deleted Successfully");
                    builder2.setPositiveButton("ok", (dialogInterface1, i1) -> {});
                    builder2.show();

                }).addOnFailureListener(e -> {
                    // Failure callback
                    Toast.makeText(this, "Failed to delete contact: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }).setNegativeButton("Cancel", (dialogInterface, i) -> {
            });

            builder1.show();


        });
        edit.setOnClickListener(view -> {
            Intent intent1=new Intent(getApplicationContext(),ContactEdit.class);
            intent1.putExtra("username",username);
            intent1.putExtra("phone",phone);
            intent1.putExtra("name",coname);
            intent1.putExtra("email",coemail);
            startActivity(intent1);
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