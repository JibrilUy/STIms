package com.example.stims_v9.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.stims_v9.MainActivity;
import com.example.stims_v9.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;

public class ProfileActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 10;


    Button btnSignOut, btnExit, btnEditProfile, btnTrial;

    TextView textViewProfileBlank1,textViewProfileBlank2,textViewProfileBlank3,textViewProfileBlank4,textViewProfileBlank5,textViewProfileBlank6;
    TextView textViewProfileEmail, textViewProfileName, textViewProfileStudentNumber, textViewProfileSection, textViewProfileParentEmail, textViewProfileQuote;
    FirebaseUser currentUser;
    FirebaseAuth mAuth;

    Uri savedImageUri;
    ImageView imageViewProfilePic;

    String userEmail;

    final FirebaseDatabase studentDatabase = FirebaseDatabase.getInstance("https://stims-v9-default-rtdb.asia-southeast1.firebasedatabase.app/");
    DatabaseReference root = studentDatabase.getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            // Permission is granted




        } else {
            // Request permission
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    PERMISSION_REQUEST_CODE);
        }

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        initializeVariables();

        displayUserData();

        if (savedImageUri != null) {
        }


        btnTrial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                savedImageUri = getImageUriFromSharedPreferences();
                imageViewProfilePic.setImageURI(savedImageUri);

            }
        });





















        imageViewProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });

        btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideButton(btnSignOut, btnExit, btnEditProfile, btnTrial);
                hideText(textViewProfileEmail,textViewProfileName,textViewProfileSection,textViewProfileStudentNumber,textViewProfileParentEmail
                        , textViewProfileQuote, textViewProfileBlank1,textViewProfileBlank2,textViewProfileBlank3,textViewProfileBlank4,textViewProfileBlank5,textViewProfileBlank6);
                replaceFragment(new EditProfile());
            }
        });

        btnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openSignInActivity();
            }
        });

        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openMainActivity();
            }
        });








    }

    public void openSignInActivity(){
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent (getApplicationContext(), com.example.stims_v9.Login.SignIn.class);
        startActivity(intent);
        finish();
    }

    public void openMainActivity() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void initializeVariables(){
        textViewProfileEmail = findViewById(R.id.textViewProfileEmail);
        textViewProfileName = findViewById(R.id.textViewProfileName);
        textViewProfileStudentNumber = findViewById(R.id.textViewProfileStudentNumber);
        textViewProfileSection = findViewById(R.id.textViewProfileSection);
        textViewProfileParentEmail = findViewById(R.id.textViewProfileParentEmail);
        textViewProfileQuote = findViewById(R.id.textViewProfileQuote);

        textViewProfileBlank1 = findViewById(R.id.textViewProfileBlank1);
        textViewProfileBlank2 = findViewById(R.id.textViewProfileBlank2);
        textViewProfileBlank3= findViewById(R.id.textViewProfileBlank3);
        textViewProfileBlank4 = findViewById(R.id.textViewProfileBlank4);
        textViewProfileBlank5 = findViewById(R.id.textViewProfileBlank5);
        textViewProfileBlank6 = findViewById(R.id.textViewProfileBlank6);

        btnSignOut = findViewById(R.id.btnSignOut);
        btnExit = findViewById(R.id.btnExit);
        btnEditProfile = findViewById(R.id.btnEditProfile);
        btnTrial = findViewById(R.id.btnTrial);

        imageViewProfilePic = findViewById(R.id.imageViewProfilePic);
    }
    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }

    public void hideButton(Button button,Button button2,Button button3,Button button4) {
        button.setVisibility(View.GONE);
        button2.setVisibility(View.GONE);
        button3.setVisibility(View.GONE);
        button4.setVisibility(View.GONE);
        imageViewProfilePic.setVisibility(View.GONE);
    }

    public void hideText(TextView textView1, TextView textView2, TextView textView3, TextView textView4, TextView textView5, TextView textView6
        ,TextView textview7, TextView textview8, TextView textview9, TextView textview10, TextView textview11, TextView textview12){
        textView1.setVisibility(View.GONE);
        textView2.setVisibility(View.GONE);
        textView3.setVisibility(View.GONE);
        textView4.setVisibility(View.GONE);
        textView5.setVisibility(View.GONE);
        textView6.setVisibility(View.GONE);
        textview7.setVisibility(View.GONE);
        textview8.setVisibility(View.GONE);
        textview9.setVisibility(View.GONE);
        textview10.setVisibility(View.GONE);
        textview11.setVisibility(View.GONE);
        textview12.setVisibility(View.GONE);
    }

    public void displayTextOnTextView(TextView textView, String string){
        textView.setText(string);
    }

    public void displayUserData(){
        if(currentUser != null) {
            getValueFromUserData("email", textViewProfileEmail);
            getValueFromUserData("name", textViewProfileName);
            getValueFromUserData("student_number", textViewProfileStudentNumber);
            getValueFromUserData("section", textViewProfileSection);
            getValueFromUserData("parent_email", textViewProfileParentEmail);
            getValueFromUserData("quote", textViewProfileQuote);
        }
    }
    public void getValueFromUserData(String childNode, TextView textView){

        String userId = mAuth.getCurrentUser().getUid();
        DatabaseReference userDatabaseRef = root.child("UserData").child(userId);
        userDatabaseRef.child(childNode).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String userData = snapshot.getValue(String.class);
                    displayTextOnTextView(textView, userData);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, access media files

            }
        }
    }
    private Uri getImageUriFromSharedPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences("my_preferences", Context.MODE_PRIVATE);
        String imageUriString = sharedPreferences.getString("image_uri", "");
        if (!imageUriString.equals("")) {
            Log.d("SAVE_IMAGE_URI", "Retrieved image URI from shared preferences: " + imageUriString);
            return Uri.parse(imageUriString);
        } else {
            return null;
        }
    }




}