package com.example.stims_v9;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.stims_v9.Login.SignIn;
import com.example.stims_v9.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    FirebaseAuth mAuth;

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser == null){
            Intent intent = new Intent (this, com.example.stims_v9.Login.SignIn.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

            binding = ActivityMainBinding.inflate(getLayoutInflater());
            setContentView(binding.getRoot());
            replaceFragment(new StatFragment());

            mAuth = FirebaseAuth.getInstance();

            binding.bottomNavigationView.setOnItemSelectedListener(item -> {

                switch (item.getItemId()) {

                    case R.id.stats:
                        replaceFragment(new StatFragment());
                        break;
//                case R.id.profile:
//                    replaceFragment(new ProfileFragment());
//                    break;
                    case R.id.qr:
                        replaceFragment(new QRFragment());
                        break;
                    case R.id.scan:
                        replaceFragment(new ScanFragment());
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
}
