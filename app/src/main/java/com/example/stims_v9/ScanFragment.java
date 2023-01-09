package com.example.stims_v9;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ScanFragment extends Fragment {

    //Initialize variable
    String currentDate = new SimpleDateFormat("yyyy, MMMM, d,EEEE", Locale.getDefault()).format(new Date());
    String currentTime = new SimpleDateFormat("h:mm:a", Locale.getDefault()).format(new Date());
    String time = currentTime;
    String date = currentDate;

    Button btn_scan ;

    //Initialize FirebaseDatabase
    private final FirebaseDatabase studentDatabase = FirebaseDatabase.getInstance("https://stims-v9-default-rtdb.asia-southeast1.firebasedatabase.app/");
    private final DatabaseReference root = studentDatabase.getReference();
    private final DatabaseReference userRootRef = root.child("Users");
    List<String> childNodeValues = new ArrayList<>();


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    //something to view in git
    // trying to make a branch
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_scan, container, false);
        // Register the launcher and result handler
        final ActivityResultLauncher<ScanOptions> barcodeLauncher = registerForActivityResult(new ScanContract(),
                result -> {
                    result.getClass();
                    //Initialize Bob the Builder the Dialog
                    AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                    // Just the title for the Alert Dialog Box
                    builder.setTitle("Result");
                    // Set the Result from the Scanner on the Screen
                    builder.setMessage(result.getContents());
                    //for the button CANCEL
                    builder.setPositiveButton("CANCEL", (dialogInterface, i) -> dialogInterface.dismiss());
                    //for the button Check In
                    builder.setNegativeButton("CHECK IN/OUT", (dialogInterface, i) -> {
                        String scanResult = result.getContents();

                        DatabaseReference scanRes = root.child(scanResult);
                        DatabaseReference dateNodeRef = scanRes.child(date);

                        dateNodeRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.child("Check_In").exists()){
                                    dateNodeRef.child("Check_Out").setValue(time).addOnSuccessListener(unused -> Toast.makeText(getActivity(), "Check Out Successfully", Toast.LENGTH_SHORT).show());

                                }else{
                                    dateNodeRef.child("Date").setValue(date);
                                    userRootRef.push().child("student_name").setValue(scanResult);
                                    dateNodeRef.child("Name").setValue(scanResult);
                                    dateNodeRef.child("Check_In").setValue(time).addOnSuccessListener(unused -> Toast.makeText(getActivity(), "Check In Successfully", Toast.LENGTH_SHORT).show());
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                            }
                        });


//                        userRootRef.addValueEventListener(new ValueEventListener() {
//                            @Override
//                            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
//                                    String childNodeValue = dataSnapshot.getValue(String.class);
//                                    childNodeValues.add(childNodeValue);
//                                }
//                                if(!snapshot.hasChild(String.valueOf(childNodeValues)));
//                                userRootRef.push().child("student_name").setValue(scanResult);
//                            }
//
//                            @Override
//                            public void onCancelled(@NonNull DatabaseError error) {
//
//                            }
//                        });

                    });
                    //to show it duh
                    builder.show();
                });
        //Assign Button DON'T FUCKING FORGET THIS
        btn_scan = v.findViewById(R.id.btn_scan);
        btn_scan.setOnClickListener(view -> {
            Toast.makeText(getActivity(), "CAMERA ON", Toast.LENGTH_SHORT).show();
            //Initialize Scan
            barcodeLauncher.launch(new ScanOptions());
            //Initialize Intent Integrator/Scan Options
            ScanOptions options = new ScanOptions();
            //Set Prompt Text
            options.setPrompt("For Flash use Volume up Key");
            //Set Beep
            options.setBeepEnabled(true);
            //Locked Orientation
            options.setOrientationLocked(true);
            //Set Capture Activity
            options.setCaptureActivity(Capture.class);
        });
        return v;
    }
}


