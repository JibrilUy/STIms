package com.example.stims_v9.Button;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.stims_v9.Login.Register;
import com.example.stims_v9.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class EditProfile extends Fragment {


    FirebaseUser currentUser;
    FirebaseAuth mAuth;

    EditText editTextProfileName, editTextProfileStudentNumber, editTextProfileSection, editTextProfileParentEmail, editTextProfileQuote;

    Button btnAddUserData, btnProfileEditExit;

    String userEmail, userName, userStudentNumber, userSection, userParentEmail, userQuote;

    final FirebaseDatabase studentDatabase = FirebaseDatabase.getInstance("https://stims-v9-default-rtdb.asia-southeast1.firebasedatabase.app/");
    DatabaseReference root = studentDatabase.getReference();




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_edit_profile, container, false);


        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        editTextProfileName = v.findViewById(R.id.editTextProfileName);
        editTextProfileStudentNumber = v.findViewById(R.id.editTextProfileStudentNumber);
        editTextProfileSection = v.findViewById(R.id.editTextProfileSection);
        editTextProfileParentEmail = v.findViewById(R.id.editTextProfileParentEmail);
        editTextProfileQuote = v.findViewById(R.id.editTextProfileQuote);

        btnAddUserData = v.findViewById(R.id.btnAddUserData);
        btnProfileEditExit = v.findViewById(R.id.btnProfileEditExit);


        btnAddUserData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addUserData();
            }
        });

        btnProfileEditExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openProfileActivity();
            }
        });








        return v;
    }

    public String getTextOutOfEditText(EditText editText){
       String text = editText.getText().toString();
       return text;
    }

    public void updateUserData(String value, DatabaseReference databaseReference,String pathString){
        if(!TextUtils.isEmpty(value)){
            databaseReference.child(pathString).setValue(value);
        }
    }

    public void showToast(String string){
        Toast.makeText(getActivity(), string, Toast.LENGTH_SHORT).show();
    }

    public void openProfileActivity(){
        Intent intent = new Intent (getActivity(), com.example.stims_v9.Button.ProfileActivity.class);
        startActivity(intent);
    }


    public void addUserData(){

        showToast("Info Updated");
        String userId = mAuth.getCurrentUser().getUid();
        DatabaseReference userDatabaseRef = root.child("UserData").child(userId);

        userEmail = currentUser.getEmail();
        userName = getTextOutOfEditText(editTextProfileName);
        userStudentNumber = getTextOutOfEditText(editTextProfileStudentNumber);
        userSection = getTextOutOfEditText(editTextProfileSection);
        userParentEmail = getTextOutOfEditText(editTextProfileParentEmail);
        userQuote = getTextOutOfEditText(editTextProfileQuote);

        userDatabaseRef.child("email").setValue(userEmail);
        userDatabaseRef.child("UID").setValue(userId);

        updateUserData(userName, userDatabaseRef,"name");
        updateUserData(userStudentNumber, userDatabaseRef,"student_number");
        updateUserData(userSection, userDatabaseRef,"section");
        updateUserData(userParentEmail, userDatabaseRef,"parent_email");
        updateUserData(userQuote, userDatabaseRef,"quote");


    }




}