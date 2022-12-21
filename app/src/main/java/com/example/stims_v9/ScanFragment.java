package com.example.stims_v9;

import static android.content.ContentValues.TAG;

import android.content.DialogInterface;
import android.os.Bundle;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.client.android.Intents;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ScanFragment extends Fragment {
    //    Date currentTime = Calendar.getInstance().getTime();
    String currentDate = new SimpleDateFormat("EEEE,d", Locale.getDefault()).format(new Date());
    String currentTime = new SimpleDateFormat("h:mm:a", Locale.getDefault()).format(new Date());
    String currentMonth = new SimpleDateFormat("MMMM, yyyy", Locale.getDefault()).format(new Date());

    String time = currentTime;
    String date = currentDate;
    String month = currentMonth;

    //Initialize variable
    Button btn_scan ;
    //Initialize FirebaseDatabase
    private final FirebaseDatabase studentDatabase = FirebaseDatabase.getInstance("https://stims-v9-default-rtdb.asia-southeast1.firebasedatabase.app/");
    private final DatabaseReference root = studentDatabase.getReference().child(month).child(date);
    //Initialize Date and Time



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_scan, container, false);
        // Register the launcher and result handler
        final ActivityResultLauncher<ScanOptions> barcodeLauncher = registerForActivityResult(new ScanContract(),
                result -> {

                    result.getClass();
                    Toast.makeText(getActivity(), "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
                    //Initialize Bob the Builder the Dialog
                    AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                    // Just the title for the Alert Dialog Box
                    builder.setTitle("Result");
                    // Set the Result from the Scanner on the Screen
                    builder.setMessage(result.getContents());
                    //for the button CANCEL
                    builder.setPositiveButton("CANCEL", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });


                    //for the button Check In
                    builder.setNegativeButton("CHECK IN", new DialogInterface.OnClickListener() {
//                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

//                            String scanResult = result.getContents();
//                            String time = currentTime;
//                            root.child("Student_Number").setValue(scanResult);
//                            root.child("Check_In").setValue(time);
//                            root.child("Check_Out").setValue(time);

                            root.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot snapshot) {
                                    String scanResult = result.getContents();

                                if(snapshot.child(scanResult).exists()){
                                    //Checks if the Month child has a Value, Try to find a solution this is trash
                                    //trash but it works dunno headache inducing paradox
                                }else{
                                    if (snapshot.child(scanResult).hasChild("Check_In")) {
                                        root.child(scanResult).child("Check_Out").setValue(time);

                                    } else {
//                                        root.child("Student_Number").setValue(scanResult);
                                        root.child(scanResult).child("Check_In").setValue(time);
                                    }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                    });
                    //to show it duh
                    builder.show();
                });
        //Assign Button DON'T FUCKING FORGET THIS
        btn_scan = v.findViewById(R.id.btn_scan);
        btn_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
            }
        });
        return v;
    }





}


