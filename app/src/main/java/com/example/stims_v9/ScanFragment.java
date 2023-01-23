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

    ArrayList<String> list2;
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

        list2 = new ArrayList<>();

        text_view_subject_selected = v.findViewById(R.id.text_view_subject_selected);
        text_view_violation_selected = v.findViewById(R.id.text_view_violation_selected);

        spinner_subjects = v.findViewById(R.id.spinner_subjects);
        spinner_violations = v.findViewById(R.id.spinner_violations);

        subjectsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                updateSubjectSpinner(dataSnapshot);

                ArrayAdapter<String> subjectAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, subjectList);
                spinner_subjects.setAdapter(subjectAdapter);
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

                ArrayAdapter<String> violationAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, violationList);
                spinner_violations.setAdapter(violationAdapter);
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


        btn_subjects = v.findViewById(R.id.btn_subjects);
        btn_subjects.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                replaceFragment(new SubjectFragment());
                btn_scan.setVisibility(View.GONE);
                btn_subjects.setVisibility(View.GONE);
                spinner_subjects.setVisibility(View.GONE);
                spinner_violations.setVisibility(View.GONE);
            }
        });


        // Register the launcher and result handler
        final ActivityResultLauncher<ScanOptions> barcodeLauncher = registerForActivityResult(new ScanContract(),
                result -> {
                    result.getClass();
                    AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                    builder.setTitle("Result");
                    builder.setMessage(result.getContents());
                    builder.setPositiveButton("CANCEL", (dialogInterface, i) -> dialogInterface.dismiss());
                    builder.setNegativeButton("CHECK IN/OUT", (dialogInterface, i) -> {
                        String scanResult = result.getContents();


                        DatabaseReference dateNodeRef = root.child("Logs").child(date).child(scanResult);

                        DatabaseReference searchRef = root.child("Scans").child(scanResult).child(date);


                        DatabaseReference userRes = root.child("Users").child(scanResult);
                        DatabaseReference suggestionRef = root.child("Suggestions");

                        DatabaseReference subjectSelected = root.child("SubjectSelected").child(selectedSubject);
                        DatabaseReference subjectSelectedRef = subjectSelected.child(scanResult);

                        DatabaseReference violationSelected = root.child("ViolationSelected").child(selectedViolations);
                        DatabaseReference subjectViolationRef = violationSelected.child(scanResult);


                        dateNodeRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.child("Check_In").exists()){
                                    dateNodeRef.child("Check_Out").setValue(time).addOnSuccessListener(unused -> Toast.makeText(getActivity(), "Check Out Successfully", Toast.LENGTH_SHORT).show());
                                    searchRef.child("Check_Out").setValue(time);
                                    subjectSelectedRef.child("Check_Out").setValue(time);
                                    subjectViolationRef.child("Check_Out").setValue(time);


                                }else{

                                    subjectViolationRef.child("Name").setValue(scanResult);
                                    subjectViolationRef.child("Date").setValue(date);
                                    subjectViolationRef.child("Subject").setValue(selectedSubject);
                                    subjectViolationRef.child("Violations").setValue(selectedViolations);
                                    subjectViolationRef.child("Check_In").setValue(time);

                                    subjectSelectedRef.child("Name").setValue(scanResult);
                                    subjectSelectedRef.child("Date").setValue(date);
                                    subjectSelectedRef.child("Subject").setValue(selectedSubject);
                                    subjectSelectedRef.child("Violation").setValue(selectedViolations);
                                    subjectSelectedRef.child("Check_In").setValue(time);

                                    dateNodeRef.child("Name").setValue(scanResult);
                                    dateNodeRef.child("Date").setValue(date);
                                    dateNodeRef.child("Subject").setValue(selectedSubject);
                                    dateNodeRef.child("Violation").setValue(selectedViolations);
                                    dateNodeRef.child("Check_In").setValue(time).addOnSuccessListener(unused -> Toast.makeText(getActivity(), "Check In Successfully", Toast.LENGTH_SHORT).show());

                                    searchRef.child("Name").setValue(scanResult);
                                    searchRef.child("Date").setValue(date);
                                    searchRef.child("Subject").setValue(selectedSubject);
                                    searchRef.child("Violation").setValue(selectedViolations);
                                    searchRef.child("Check_In").setValue(time);
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
        btn_scan = v.findViewById(R.id.btn_scan);
        btn_scan.setOnClickListener(view -> {
            Toast.makeText(getActivity(), "CAMERA ON", Toast.LENGTH_SHORT).show();
            barcodeLauncher.launch(new ScanOptions());
            ScanOptions options = new ScanOptions();
            options.setPrompt("For Flash use Volume up Key");
            options.setBeepEnabled(true);
            options.setOrientationLocked(true);
            options.setCaptureActivity(Capture.class);
        });
        return v;
    }



    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getChildFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
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

}


