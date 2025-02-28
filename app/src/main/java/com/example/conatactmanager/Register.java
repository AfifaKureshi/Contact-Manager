package com.example.conatactmanager;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.conatactmanager.databinding.ActivityMainBinding;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Register extends AppCompatActivity {
    String CHANNEL_ID = "channel_id";
    String CHANNEL_NAME = "channel_name";
    int NOTIFICATION_ID = 0;
    ActivityMainBinding binding;
    TextInputLayout etName,etMail,etUniqid,etPass;
    String name,password,uniqid,mail;
    DatabaseReference dbref;
    Button btnregister;
    TextView txtLogin;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        createNotification();
        Intent intent=new Intent(this,Register.class);
        PendingIntent pendingIntent=PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_IMMUTABLE);

        Notification notifiaction= new NotificationCompat.Builder(this,CHANNEL_ID).
                setContentTitle("Congratulations")
                .setContentText("Resgistered Successfully")
                .setSmallIcon(R.drawable.baseline_insert_emoticon_24)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent).build();
        Intent i=getIntent();
        dbref= FirebaseDatabase.getInstance().getReference("Users");
        etName=findViewById(R.id.etName);
        etPass=findViewById(R.id.etPass);
        etUniqid=findViewById(R.id.etUniqid);
        etMail=findViewById(R.id.etMail);
        txtLogin=findViewById(R.id.txtLogin);
        txtLogin.setOnClickListener(view -> {
            Intent intent1=new Intent(this,MainActivity.class);
            startActivity(intent1);
        });
        NotificationManagerCompat notificationManager=  NotificationManagerCompat.from(this);
        btnregister=findViewById(R.id.btnregister);
        btnregister.setOnClickListener(view -> {
            name=etName.getEditText().getText().toString();
            password=etPass.getEditText().getText().toString();
            uniqid=etUniqid.getEditText().getText().toString();
            mail=etMail.getEditText().getText().toString();
            User user=new User(name,mail,password);
            if (uniqid != null) {
                if(validateRegistration(name,mail,password)) {
                    dbref.child(uniqid).setValue(user).addOnSuccessListener(aVoid -> {
                        // Success callback
                        Toast.makeText(getBaseContext(), "Registered Successfully", Toast.LENGTH_SHORT).show();
                        Intent intent2 = new Intent(this, ContactDirectory.class);
                        intent2.putExtra("name", name);
                        intent2.putExtra("username",uniqid);
                        startActivity(intent2);

                    }).addOnFailureListener(e -> {
                        // Failure callback
                        Toast.makeText(getBaseContext(), "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
                }
            }else{
                Toast.makeText(getBaseContext(), "Uniqe Id is Required" , Toast.LENGTH_SHORT).show();
            }
            etName.getEditText().setText("");
            etMail.getEditText().setText("");
            etUniqid.getEditText().setText("");
            etPass.getEditText().setText("");
            if(ActivityCompat.checkSelfPermission(this,"android.permission.POST_NOTIFICATIONS")== PackageManager.PERMISSION_GRANTED){
                notificationManager.notify(NOTIFICATION_ID,notifiaction);
            }else{
                ActivityCompat.requestPermissions(this,new String[]{android.Manifest.permission.POST_NOTIFICATIONS},0);
            }

        });
    }
    public boolean validateRegistration(String name, String email, String password) {
        if (name == null || name.trim().isEmpty()) {
            Toast.makeText(this, "Please enter your Name", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (email == null || email.trim().isEmpty()) {
            Toast.makeText(this, "Please enter your Email", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (password == null || password.trim().isEmpty()) {
            Toast.makeText(this, "Please enter your Password", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Add more validation checks as needed (e.g., email format, password strength)

        return true; // All validations passed
    }
    @SuppressLint("NewApi")
    protected void createNotification(){
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.BASE){
            NotificationChannel channel=new NotificationChannel(CHANNEL_ID,CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("This is my Notification");
            channel.setLightColor(Color.rgb(250,0,0));
            channel.enableLights(true);
            NotificationManager manager= (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            manager.createNotificationChannel(channel);
        }
    }

}