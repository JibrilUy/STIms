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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Register extends AppCompatActivity {

    final FirebaseDatabase studentDatabase = FirebaseDatabase.getInstance("https://stims-v9-default-rtdb.asia-southeast1.firebasedatabase.app/");
    DatabaseReference root = studentDatabase.getReference();

    EditText editTextRegisterEmail, editTextRegisterPassword;
    Button btnRegister;
    TextView textViewRedirectToLogin;
    ProgressBar progressBarRegister;
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    String email, password;

    @Override
    public void onStart() {
        super.onStart();
         currentUser = mAuth.getCurrentUser();
         openMainActivity();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        editTextRegisterEmail = findViewById(R.id.editTextRegisterEmail);
        editTextRegisterPassword = findViewById(R.id.editTextRegisterPassword);
        btnRegister = findViewById(R.id.btnRegister);
        textViewRedirectToLogin = findViewById(R.id.textViewRedirectToLogin);
        progressBarRegister = findViewById(R.id.progressBarRegister);

        mAuth = FirebaseAuth.getInstance();

        textViewRedirectToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openSignInActivity();
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email = editTextRegisterEmail.getText().toString();
                password = editTextRegisterPassword.getText().toString();

                progressBarRegister.setVisibility(View.VISIBLE);

                createAccount(email, password);


//                String userId = mAuth.getCurrentUser().getUid();
//                DatabaseReference userDatabaseRef = root.child("UserData").child(userId);
//                userDatabaseRef.child("email").setValue(email);


            }
        });
    }

    public void openSignInActivity(){
        Intent intent = new Intent (getApplicationContext(), SignIn.class);
        startActivity(intent);
        finish();
    }

    public void openMainActivity(){
        if(currentUser != null) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    public void showToast(String string){
        Toast.makeText(Register.this, string, Toast.LENGTH_SHORT).show();
    }

    public void checkEditTextIfEmpty(String string, String text){
        if(TextUtils.isEmpty(string)){
            showToast(text);
        }

    }

    public void createAccount(String email, String password){

        if(TextUtils.isEmpty(email) && TextUtils.isEmpty(password)) {
            checkEditTextIfEmpty(email, "Enter Email");
            checkEditTextIfEmpty(password, "Create Password");
        }else{
            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    progressBarRegister.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        showToast("Account Created");
                    } else {
                        showToast("Email Already Used");
                    }
                }
            });
        }
    }




}