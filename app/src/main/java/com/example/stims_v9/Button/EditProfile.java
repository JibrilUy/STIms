package com.example.stims_v9.Button;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.stims_v9.Login.Register;
import com.example.stims_v9.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public class EditProfile extends Fragment {

    private static final int PICK_IMAGE_REQUEST_CODE = 11;

    FirebaseUser currentUser;
    FirebaseAuth mAuth;

    EditText editTextProfileName, editTextProfileStudentNumber, editTextProfileSection, editTextProfileParentEmail, editTextProfileQuote;

    Button btnAddUserData, btnProfileEditExit;

    ImageView imageViewEditProfilePic;

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

        imageViewEditProfilePic = v.findViewById(R.id.imageViewEditProfilePic);

        btnAddUserData = v.findViewById(R.id.btnAddUserData);
        btnProfileEditExit = v.findViewById(R.id.btnProfileEditExit);

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


    private void pickImage() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == PICK_IMAGE_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
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
        boolean saved = editor.commit();
        if (saved) {
            Log.d("SAVE_IMAGE_URI", "Image URI saved: " + imageUri);
        } else {
            Log.d("SAVE_IMAGE_URI", "Failed to save image URI");
        }
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




}