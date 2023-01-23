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
    TextView text_view_subject_selected;
    private TextViewUpdater textViewUpdater;


    ArrayList<String> list2;


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


        textViewUpdater = new TextViewUpdater();
        text_view_subject_selected = v.findViewById(R.id.text_view_subject_selected);

        btn_subjects = v.findViewById(R.id.btn_subjects);
        btn_subjects.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                replaceFragment(new SubjectFragment());
            }
        });

        text_view_subject_selected = v.findViewById(R.id.text_view_subject_selected);








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

                        DatabaseReference scanRes = root.child("Logs").child(date);
                        DatabaseReference dateNodeRef = scanRes.child(scanResult);

                        DatabaseReference searchRootRef = root.child("Scans").child(scanResult);
                        DatabaseReference searchRef = searchRootRef.child(date);

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
                                    subjectViolationRef.child("Violations").setValue(selectedViolations);
                                    subjectViolationRef.child("Check_In").setValue(time);
                                    subjectViolationRef.child("Date").setValue(date);
                                    subjectViolationRef.child("Name").setValue(scanResult);
                                    subjectViolationRef.child("Subject").setValue(selectedSubject);


                                    subjectSelectedRef.child("Subject").setValue(selectedSubject);
                                    subjectSelectedRef.child("Violation").setValue(selectedViolations);
                                    subjectSelectedRef.child("Check_In").setValue(time);
                                    subjectSelectedRef.child("Date").setValue(date);
                                    subjectSelectedRef.child("Name").setValue(scanResult);

                                    dateNodeRef.child("Subject").setValue(selectedSubject);
                                    dateNodeRef.child("Violation").setValue(selectedViolations);
                                    dateNodeRef.child("Date").setValue(date);
                                    dateNodeRef.child("Name").setValue(scanResult);
                                    dateNodeRef.child("Check_In").setValue(time).addOnSuccessListener(unused -> Toast.makeText(getActivity(), "Check In Successfully", Toast.LENGTH_SHORT).show());

                                    searchRef.child("Date").setValue(date);
                                    searchRef.child("Name").setValue(scanResult);
                                    searchRef.child("Check_In").setValue(time);
                                    searchRef.child("Subject").setValue(selectedSubject);
                                    searchRef.child("Violation").setValue(selectedViolations);


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
                                public void onCancelled (@NonNull DatabaseError error){

                                }

                        });


                    });
                    //to show it duh
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
    public void textViewManager(String string){
        TextView textView = getView().findViewById(R.id.text_view_subject_selected);
        textView.setText(string);
    }





}


