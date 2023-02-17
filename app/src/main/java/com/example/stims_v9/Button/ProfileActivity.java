package com.example.stims_v9.Button;

import androidx.appcompat.app.AppCompatActivity;

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

public class ProfileActivity extends AppCompatActivity {

    Button btnSignOut, btnExit;

    TextView textViewProfileEmail, textViewUserNameProfile;
    FirebaseUser currentUser;

    ImageView imageViewProfilePic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        btnSignOut = findViewById(R.id.btnSignOut);
        btnExit = findViewById(R.id.btnExit);

        textViewProfileEmail = findViewById(R.id.textViewProfileEmail);
        textViewUserNameProfile = findViewById(R.id.textViewUserNameProfile);

        imageViewProfilePic = findViewById(R.id.imageViewProfilePic);

        currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            String userEmail = currentUser.getEmail();
            textViewProfileEmail.setText(userEmail);

        }





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
}