package com.example.stims_v9.Login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.stims_v9.MainActivity;
import com.example.stims_v9.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignIn extends AppCompatActivity {

    EditText editTextLoginEmail, editTextLoginPassword;
    Button btnSignIn;
    TextView textViewRedirectToRegister;
    ProgressBar progressBarLogin;
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    String email, password;


    @Override
    public void onStart() {
        super.onStart();
        currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            openMainActivity();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        editTextLoginEmail = findViewById(R.id.editTextLoginEmail);
        editTextLoginPassword = findViewById(R.id.editTextLoginPassword);
        btnSignIn = findViewById(R.id.btnSignIn);
        textViewRedirectToRegister = findViewById(R.id.textViewRedirectToRegister);
        progressBarLogin = findViewById(R.id.progressBarLogin);
        mAuth = FirebaseAuth.getInstance();

        textViewRedirectToRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openRegisterActivity();
            }
        });

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email = editTextLoginEmail.getText().toString();
                password = editTextLoginPassword.getText().toString();

                progressBarLogin.setVisibility(View.VISIBLE);

                verifyAccount();
            }

        });

    }
    public void showToast(String string){
        Toast.makeText(SignIn.this, string, Toast.LENGTH_SHORT).show();
    }

    public void checkEditTextIfEmpty(String string, String text){
        if(TextUtils.isEmpty(string)){
            showToast(text);
        }
    }

    public void openRegisterActivity(){
        Intent intent = new Intent (getApplicationContext(), Register.class);
        startActivity(intent);
        finish();
    }


    public void openMainActivity(){
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
    }

    public void verifyAccount(){

        if(TextUtils.isEmpty(email) && TextUtils.isEmpty(password)){
            checkEditTextIfEmpty(email, "Enter Email");
            checkEditTextIfEmpty(password, "Enter Password");
        }else{
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    progressBarLogin.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        openMainActivity();
                        showToast("Sign In Successfully");
                    } else {
                        showToast("Wrong Password or Email");
                    }
                }
            });
        }
    }




}