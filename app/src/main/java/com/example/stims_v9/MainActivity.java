package com.example.stims_v9;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.stims_v9.Login.Register;
import com.example.stims_v9.Login.SignIn;
import com.example.stims_v9.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;

    FirebaseAuth mAuth;
    FirebaseUser currentUser;

    Uri savedImageUri;


    @Override
    public void onStart() {
        super.onStart();
        currentUser = mAuth.getCurrentUser();
        if(currentUser == null){
            openSignInActivity();
        }
        savedImageUri = getImageUriFromSharedPreferences();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setLogo(R.mipmap.stims_logo_round);

        View customView = getLayoutInflater().inflate(R.layout.action_bar_custom_view,null);
        actionBar.setCustomView(customView);



        ImageButton imageButton = customView.findViewById(R.id.imageButton);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openProfileActivity();

            }
        });

            binding = ActivityMainBinding.inflate(getLayoutInflater());
            setContentView(binding.getRoot());
            replaceFragment(new StatFragment());

            mAuth = FirebaseAuth.getInstance();


            binding.bottomNavigationView.setOnItemSelectedListener(item -> {

                switch (item.getItemId()) {

                    case R.id.stats:
                        replaceFragment(new StatFragment());
                        item.setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.baseline_auto_graph_24_selected));
                        break;
                    case R.id.qr:
                        replaceFragment(new QRFragment());
                        item.setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.baseline_qr_code_24_selected));
                        break;
                    case R.id.scan:
                        replaceFragment(new ScanFragment());
                        item.setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.baseline_qr_code_scanner_24_selected));
                        break;
                }
                return true;
            });
        }




        //so that the activity page passes the result to the fragment
        @Override
        protected void onActivityResult ( int requestCode, int resultCode, @Nullable Intent data){
            super.onActivityResult(requestCode, resultCode, data);
        }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }
    public void openRegisterActivity(){
        Intent intent = new Intent (getApplicationContext(), Register.class);
        startActivity(intent);
        finish();
    }


    public void openProfileActivity(){
        Intent intent = new Intent (getApplicationContext(), com.example.stims_v9.Button.ProfileActivity.class);
        startActivity(intent);
        finish();
    }

    public void openSignInActivity(){
        if(currentUser == null){
            Intent intent = new Intent (this, com.example.stims_v9.Login.SignIn.class);
            startActivity(intent);
            finish();
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
