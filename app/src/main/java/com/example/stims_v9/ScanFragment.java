package com.example.stims_v9;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.button.MaterialButton;
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
    String nameModel, selectedSubject, selectedViolations;
    Button btn_scan ;
    EditText edit_text_subject_add, edit_text_violations_add;
    MaterialButton btn_add_subjects, btn_add_violation, btn_subjects;
    Spinner spinner_subjects, spinner_violations;
    TextView text_view_subject_selected,text_view_violation_selected;

    ArrayList<String> list2 = new ArrayList<>();
    ArrayList <String> subjectList = new ArrayList<>();
    ArrayList <String> violationList = new ArrayList<>();


    //Initialize FirebaseDatabase
    private final FirebaseDatabase studentDatabase = FirebaseDatabase.getInstance("https://stims-v9-default-rtdb.asia-southeast1.firebasedatabase.app/");
    private final DatabaseReference root = studentDatabase.getReference();

    DatabaseReference subjectsRef = studentDatabase.getReference("Subjects");
    DatabaseReference violationsRef = studentDatabase.getReference("Violations");


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_scan, container, false);


        text_view_subject_selected = v.findViewById(R.id.text_view_subject_selected);
        text_view_violation_selected = v.findViewById(R.id.text_view_violation_selected);

        spinner_subjects = v.findViewById(R.id.spinner_subjects);
        spinner_violations = v.findViewById(R.id.spinner_violations);

        btn_subjects = v.findViewById(R.id.btn_subjects);
        btn_scan = v.findViewById(R.id.btn_scan);

        subjectsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                updateSubjectSpinner(dataSnapshot);

                if(isAdded()) {
                    ArrayAdapter<String> subjectAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, subjectList);
                    spinner_subjects.setAdapter(subjectAdapter);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {   }});

        spinner_subjects.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedSubject = spinner_subjects.getSelectedItem().toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) { }});

        violationsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                updateViolationSpinner(dataSnapshot);

                if(isAdded()) {
                    ArrayAdapter<String> violationAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, violationList);
                    spinner_violations.setAdapter(violationAdapter);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {   }
        });

        spinner_violations.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedViolations = spinner_violations.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        final ActivityResultLauncher<ScanOptions> barcodeLauncher = registerForActivityResult(new ScanContract(),
                result -> {
                    result.getClass();
                    String scanResult = result.getContents();
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle(scanResult);
                    builder.setMessage("Selected Subject: "+ selectedSubject + "\nSelected Violation: " + selectedViolations);
                    builder.setPositiveButton("CANCEL", (dialogInterface, i) -> {
                        Toast.makeText(getActivity(), "Cancelled", Toast.LENGTH_SHORT).show();
                        dialogInterface.dismiss();
                    });
                    builder.setNegativeButton("CHECK IN/OUT", (dialogInterface, i) -> {

                            DatabaseReference logsRef = root.child("Logs").child(date).child(selectedSubject).child(scanResult);
                            DatabaseReference scansRef = root.child("Scans").child(scanResult).child(selectedSubject).child(date);

                            DatabaseReference suggestionRef = root.child("Suggestions");
                            DatabaseReference userRes = root.child("Users").child(scanResult);

                            logsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.child("Check_In").exists()) {
                                        checkOutData(scansRef, time);
                                        checkOutData(logsRef, time);
                                    } else {
                                        checkInData(logsRef, scanResult, date, time, selectedSubject, selectedViolations);
                                        checkInData(scansRef, scanResult, date, time, selectedSubject, selectedViolations);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                }
                            });

                        userRes.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                                    nameModel = childSnapshot.getValue().toString();
                                    list2.add(nameModel);
                                }
                                if (!list2.contains(scanResult)) {
                                    userRes.child("student_name").setValue(scanResult);
                                    suggestionRef.push().setValue(scanResult);
                                }
                            }
                                @Override
                                public void onCancelled (@NonNull DatabaseError error){   }
                        });
                });
        builder.show();
    });

        btn_scan.setOnClickListener(view -> {
            barcodeLauncher.launch(new ScanOptions());
            startScan();
        });

        btn_subjects.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                replaceFragment(new SubjectFragment());
                hideButtons(btn_scan, btn_subjects, spinner_subjects, spinner_violations, text_view_subject_selected,text_view_violation_selected);
            }
        });

        return v;
    }


    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getChildFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }

    public void hideButtons(View btnScan, View btnSubjects, View spinnerSubjects, View spinnerViolations, View text_view_subject_selected,View text_view_violation_selected) {
        btnScan.setVisibility(View.GONE);
        btnSubjects.setVisibility(View.GONE);
        spinnerSubjects.setVisibility(View.GONE);
        spinnerViolations.setVisibility(View.GONE);
        text_view_subject_selected.setVisibility(View.GONE);
        text_view_violation_selected.setVisibility(View.GONE);
    }

    public void updateSubjectSpinner(DataSnapshot dataSnapshot) {
        subjectList.clear();
        for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
            String value = childSnapshot.getValue(String.class);
            subjectList.add(value);
        }
    }
    public void updateViolationSpinner(DataSnapshot dataSnapshot) {
        violationList.clear();
        for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
            String value = childSnapshot.getValue(String.class);
            violationList.add(value);
        }
    }

    public void checkInData(DatabaseReference databaseReferenceRef, String name, String date, String check_in, String subject, String violation){
        databaseReferenceRef.child("Name").setValue(name).addOnSuccessListener(unused -> Toast.makeText(getActivity(), "Check In Successfully", Toast.LENGTH_SHORT).show());
        databaseReferenceRef.child("Date").setValue(date);
        databaseReferenceRef.child("Check_In").setValue(check_in);
        databaseReferenceRef.child("Subject").setValue(subject);
        databaseReferenceRef.child("Violation").setValue(violation);

    }

    public void checkOutData(DatabaseReference databaseReferenceRef, String time){
        databaseReferenceRef.child("Check_Out").setValue(time).addOnSuccessListener(unused -> Toast.makeText(getActivity(), "Check Out Successfully", Toast.LENGTH_SHORT).show());
    }

    public void startScan(){
        Toast.makeText(getActivity(), "For Flash use Volume up Key", Toast.LENGTH_SHORT).show();
        ScanOptions options = new ScanOptions();
        options.setBeepEnabled(true);
        options.setOrientationLocked(true);
        options.setCaptureActivity(Capture.class);
    }
}


