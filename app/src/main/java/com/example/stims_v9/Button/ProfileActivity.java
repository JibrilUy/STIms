package com.example.stims_v9.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.stims_v9.MainActivity;
import com.example.stims_v9.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ProfileActivity extends AppCompatActivity {

    Button btnSignOut, btnExit, btnEditProfile;

    TextView textViewProfileEmail, textViewUserNameProfile;
    FirebaseUser currentUser;
    FirebaseAuth mAuth;

    ImageView imageViewProfilePic;

    String userEmail;

    final FirebaseDatabase studentDatabase = FirebaseDatabase.getInstance("https://stims-v9-default-rtdb.asia-southeast1.firebasedatabase.app/");
    DatabaseReference root = studentDatabase.getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        btnSignOut = findViewById(R.id.btnSignOut);
        btnExit = findViewById(R.id.btnExit);
        btnEditProfile = findViewById(R.id.btnEditProfile);

        textViewProfileEmail = findViewById(R.id.textViewProfileEmail);
        textViewUserNameProfile = findViewById(R.id.textViewUserNameProfile);

        imageViewProfilePic = findViewById(R.id.imageViewProfilePic);


        if (currentUser != null) {
            userEmail = currentUser.getEmail();
            textViewProfileEmail.setText(userEmail);
        }


        btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideButton(btnSignOut, btnExit, btnEditProfile);
                hideText(textViewProfileEmail);
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


    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }

    public void hideButton(Button button,Button button2,Button button3) {
        button.setVisibility(View.GONE);
        button2.setVisibility(View.GONE);
        button3.setVisibility(View.GONE);
    }

    public void hideText(TextView textView){
        textView.setVisibility(View.GONE);
    }





}