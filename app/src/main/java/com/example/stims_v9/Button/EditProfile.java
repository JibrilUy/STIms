package com.example.stims_v9.Button;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.stims_v9.Adapters.spinnerAdapter;
import com.example.stims_v9.Login.Register;
import com.example.stims_v9.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
import java.util.ArrayList;


public class EditProfile extends Fragment {

    private static final int PICK_IMAGE_REQUEST_CODE = 11;

    DatabaseReference root = FirebaseDatabase.getInstance("https://stims-v9-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference();

    DatabaseReference everySectionRef = root.child("Sections");

    FirebaseUser currentUser;
    FirebaseAuth mAuth;

    Uri imageUri;

    EditText editTextProfileName, editTextProfileStudentNumber, editTextProfileSection, editTextProfileParentEmail, editTextProfileQuote;

    Button btnAddUserData, btnProfileEditExit;
    ArrayList<String> sectionList = new ArrayList<>();

    ArrayList<String> everyStudent = new ArrayList<>();



    ImageView imageViewEditProfilePic;
    
    Spinner spinnerEditProfileFragSection;

    String userEmail, userName, userStudentNumber, userSection, userParentEmail, userQuote, selectedSection, userId;





    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_edit_profile, container, false);


        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        userId = mAuth.getUid();

        editTextProfileName = v.findViewById(R.id.editTextProfileName);
        editTextProfileStudentNumber = v.findViewById(R.id.editTextProfileStudentNumber);
        editTextProfileParentEmail = v.findViewById(R.id.editTextProfileParentEmail);
        editTextProfileQuote = v.findViewById(R.id.editTextProfileQuote);

        imageViewEditProfilePic = v.findViewById(R.id.imageViewEditProfilePic);
        spinnerEditProfileFragSection = v.findViewById(R.id.spinnerEditProfileFragSection);

        btnAddUserData = v.findViewById(R.id.btnAddUserData);
        btnProfileEditExit = v.findViewById(R.id.btnProfileEditExit);

        updateSectionSpinner();

        imageViewEditProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickImage();
            }
        });

        btnAddUserData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addUserData();
                addNameToDatabase();
                if(imageUri != null) {
                    saveImageUriToSharedPreferences(imageUri);
                    saveImageToInternalStorage(imageUri);
                }
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
        userSection = selectedSection;
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


    private void pickImage() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST_CODE);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == PICK_IMAGE_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            // Save the image URI in shared preferences
            saveImageUriToSharedPreferences(imageUri);
            // Set the image in the ImageView
            imageViewEditProfilePic.setImageURI(imageUri);
        }
    }

    private void saveImageUriToSharedPreferences(Uri imageUri) {
        if (imageUri == null) {
            Log.d("SAVE_IMAGE_URI", "Image URI is null");
            return;
        }
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("my_preferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("image_uri", imageUri.toString());
        editor.apply();
    }


    private Uri getImageUriFromSharedPreferences() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("my_preferences", Context.MODE_PRIVATE);
        String imageUriString = sharedPreferences.getString("image_uri", "");
        if (!imageUriString.equals("")) {
            return Uri.parse(imageUriString);
        } else {
            return null;
        }
    }

    private void saveImageToInternalStorage(Uri imageUri) {
        String imageName = "STIms_profile_image.jpg";
        try {
            // Open a stream to read the image file
            InputStream inputStream = getActivity().getContentResolver().openInputStream(imageUri);
            // Create a new file in internal storage
            File internalFile = new File(getActivity().getFilesDir(), imageName);
            // Open a stream to write the image file to internal storage
            OutputStream outputStream = new FileOutputStream(internalFile);
            // Copy the image to internal storage
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
            // Close the streams
            outputStream.flush();
            outputStream.close();
            inputStream.close();

            // Save the path of the new file to a shared preference or a database
            SharedPreferences preferences = getActivity().getSharedPreferences("MyPrefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("image_path", internalFile.getAbsolutePath());
            editor.apply();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void updateSectionList(DataSnapshot dataSnapshot) {
        sectionList.clear();
        for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
            String value = childSnapshot.getValue(String.class);
            sectionList.add(value);
        }
    }
    public void updateSectionSpinner(){
        everySectionRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                updateSectionList(dataSnapshot);
                spinnerAdapter sectionAdapter = new spinnerAdapter(getActivity(), sectionList);
                spinnerEditProfileFragSection.setAdapter(sectionAdapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {   }});
        spinnerEditProfileFragSection.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedSection = spinnerEditProfileFragSection.getSelectedItem().toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) { }});
    }

    public void addNameToDatabase(){
        DatabaseReference userDataRef = root.child("UserData").child(userId);
        userDataRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userName = dataSnapshot.child("name").getValue(String.class);
            }
            @Override
            public void onCancelled (@NonNull DatabaseError error){   }});

        DatabaseReference everyStudentRef = root.child("Students").child(selectedSection);
        everyStudentRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                everyStudent.clear();
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    String nameModel = childSnapshot.getValue().toString();
                    everyStudent.add(nameModel);
                }
                if(!everyStudent.contains(userName)){
                    String name = userName;
                    everyStudentRef.child(name).child("student_name").setValue(name);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {  }
        });

    }








}