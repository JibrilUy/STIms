package com.example.stims_v9;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class SubjectFragment extends Fragment {

    private final FirebaseDatabase studentDatabase = FirebaseDatabase.getInstance("https://stims-v9-default-rtdb.asia-southeast1.firebasedatabase.app/");
    private final DatabaseReference root = studentDatabase.getReference();
    DatabaseReference subjectsRef = studentDatabase.getReference("Subjects");
    DatabaseReference violationsRef = studentDatabase.getReference("Violations");

    EditText edit_text_subject_add, edit_text_violations_add;
    MaterialButton btn_add_subjects, btn_add_violation;
    Spinner spinner_subjects, spinner_violations;

    String selectedSubject, selectedViolations;

    ScanFragment scanFragment;
    TextViewUpdater textViewUpdater;


    ArrayList<String> subjectList;
    ArrayList<String> violationList;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_subject, container, false);

        subjectList = new ArrayList<>();
        violationList = new ArrayList<>();

        textViewUpdater = new TextViewUpdater();
        scanFragment = (ScanFragment) getParentFragment();

        edit_text_subject_add = v.findViewById(R.id.edit_text_subject_add);

        btn_add_subjects = v.findViewById(R.id.btn_add_subjects);
        btn_add_subjects.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String subjectToAdd = edit_text_subject_add.getText().toString();
                if(!subjectToAdd.isEmpty()) {
                    subjectsRef.push().setValue(subjectToAdd).addOnSuccessListener(unused -> Toast.makeText(getActivity(), "Subject Added",
                            Toast.LENGTH_SHORT).show());
                }else{
                    Toast.makeText(getActivity(), "Please Enter Subject", Toast.LENGTH_SHORT).show();
                }
            }
        });

        edit_text_violations_add = v.findViewById(R.id.edit_text_violation_add);

        btn_add_violation = v.findViewById(R.id.btn_add_violations);
        btn_add_violation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String violationsToAdd = edit_text_violations_add.getText().toString();
                if(!violationsToAdd.isEmpty()) {
                    violationsRef.push().setValue(violationsToAdd).addOnSuccessListener(unused -> Toast.makeText(getActivity(), "Violation Added",
                            Toast.LENGTH_SHORT).show());
                }else{
                    Toast.makeText(getActivity(), "Please Enter Violations", Toast.LENGTH_SHORT).show();
                }
            }
        });


        spinner_subjects = v.findViewById(R.id.spinner_subjects);
        subjectsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                subjectList.clear();
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    String value = childSnapshot.getValue(String.class);
                    subjectList.add(value);
                }
                ArrayAdapter<String> subjectAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, subjectList);
                spinner_subjects.setAdapter(subjectAdapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        spinner_subjects.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                String selectedSubject = spinner_subjects.getSelectedItem().toString();
                textViewUpdater.updateTextView(selectedSubject, scanFragment.text_view_subject_selected);
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });


        spinner_violations = v.findViewById(R.id.spinner_violations);
        violationsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                violationList.clear();
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    String value = childSnapshot.getValue(String.class);
                    violationList.add(value);
                }
                ArrayAdapter<String> subjectAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, violationList);
                spinner_violations.setAdapter(subjectAdapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        spinner_violations.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedViolations = spinner_violations.getSelectedItem().toString();
//                text_view_subject_selected.setText(selectedViolations);
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        return v;
    }
}