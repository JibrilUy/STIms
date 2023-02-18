package com.example.stims_v9.Button;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.stims_v9.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class EditProfile extends Fragment {


    FirebaseUser currentUser;
    FirebaseAuth mAuth;

    EditText editTextProfileName, editTextProfileStudentNumber, editTextProfileSection, editTextProfileParentEmail, editTextProfileQuote;

    Button btnAddUserData;

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


        btnAddUserData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addUserData();
            }
        });








        return v;
    }

    public String getTextOutOfEditText(EditText editText){
       String text = editText.getText().toString();
       return text;
    }
    public void addUserData(){
        String userId = mAuth.getCurrentUser().getUid();

        userEmail = currentUser.getEmail();
        userName = getTextOutOfEditText(editTextProfileName);
        userStudentNumber = getTextOutOfEditText(editTextProfileStudentNumber);
        userSection = getTextOutOfEditText(editTextProfileSection);
        userParentEmail = getTextOutOfEditText(editTextProfileParentEmail);
        userQuote = getTextOutOfEditText(editTextProfileQuote);

        DatabaseReference userDatabaseRef = root.child("UserData").child(userId);
        userDatabaseRef.child("email").setValue(userEmail);
        userDatabaseRef.child("name").setValue(userName);
        userDatabaseRef.child("student_number").setValue(userStudentNumber);
        userDatabaseRef.child("section").setValue(userSection);
        userDatabaseRef.child("parent_email").setValue(userParentEmail);
        userDatabaseRef.child("quote").setValue(userQuote);

    }




}