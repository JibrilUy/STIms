package com.example.stims_v9.Button;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.stims_v9.R;
import com.example.stims_v9.ScanFragment;
import com.example.stims_v9.Adapters.TextViewUpdater;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class SubjectFragment extends Fragment {

    private final FirebaseDatabase studentDatabase = FirebaseDatabase.getInstance("https://stims-v9-default-rtdb.asia-southeast1.firebasedatabase.app/");
    DatabaseReference root = studentDatabase.getReference();
    DatabaseReference subjectsRef = studentDatabase.getReference("Subjects");
    DatabaseReference violationsRef = studentDatabase.getReference("Violations");

    ScanFragment scanFragment;
    TextViewUpdater textViewUpdater;

    EditText edit_text_subject_add, edit_text_violations_add;
    MaterialButton btn_add_subjects, btn_add_violation, btn_delete_subjects, btn_delete_violations,btn_exit_subject;
    Spinner spinner_subjects, spinner_violations;

    ArrayList <String> subjectList = new ArrayList<>();
    ArrayList <String> violationList = new ArrayList<>();

    String selectedSubject, selectedViolation;



    @Override
    public void onCreate(Bundle savedInstanceState) { super.onCreate(savedInstanceState); }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_subject, container, false);

        textViewUpdater = new TextViewUpdater();
        scanFragment = (ScanFragment) getParentFragment();

        edit_text_subject_add = v.findViewById(R.id.edit_text_subject_add);
        edit_text_violations_add = v.findViewById(R.id.edit_text_violation_add);

        btn_exit_subject = v.findViewById(R.id.btn_exit_subject);
        btn_add_subjects = v.findViewById(R.id.btn_add_subjects);
        btn_add_violation = v.findViewById(R.id.btn_add_violations);
        btn_delete_subjects = v.findViewById(R.id.btn_delete_subjects);
        btn_delete_violations = v.findViewById(R.id.btn_delete_violations);


        btn_exit_subject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeFragment(new ScanFragment());
            }
        });
        btn_add_subjects.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addSubject(edit_text_subject_add);
            }
        });
        btn_add_violation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addViolation(edit_text_violations_add);
            }
        });
        btn_delete_subjects.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteSubject(edit_text_subject_add);
            }
        });
        btn_delete_violations.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteViolation(edit_text_violations_add);
            }
        });

        spinner_subjects = v.findViewById(R.id.spinner_subjects);
        spinner_violations = v.findViewById(R.id.spinner_violations);




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
                edit_text_subject_add.setText(selectedSubject);
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
                selectedViolation = spinner_violations.getSelectedItem().toString();
                edit_text_violations_add.setText(selectedViolation);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });


        return v;
    }

    public void removeFragment(Fragment fragment) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.detach(this);
        fragmentTransaction.replace(R.id.frame_layout,fragment);
        fragmentTransaction.commit();
    }

    public void addViolation(EditText editText) {
        String violationsToAdd = editText.getText().toString();
        if(!violationsToAdd.isEmpty()) {
            violationsRef.child(violationsToAdd).setValue(violationsToAdd).addOnSuccessListener(unused -> Toast.makeText(getActivity(), "Violation Added",
                    Toast.LENGTH_SHORT).show());
        } else {
            Toast.makeText(getActivity(), "Please Enter Violation", Toast.LENGTH_SHORT).show();
        }
    }
    public void addSubject(EditText editText) {
        String subjectsToAdd = editText.getText().toString();
        if(!subjectsToAdd.isEmpty()) {
            subjectsRef.child(subjectsToAdd).setValue(subjectsToAdd).addOnSuccessListener(unused -> Toast.makeText(getActivity(), "Subject Added",
                    Toast.LENGTH_SHORT).show());
        } else {
            Toast.makeText(getActivity(), "Please Enter Subject", Toast.LENGTH_SHORT).show();
        }
    }

    public void deleteSubject(EditText editText) {
        String subjectToDelete = editText.getText().toString();
        DatabaseReference subjectsRootRef = root.child("Subjects");
        DatabaseReference selectedSubjectsRef = subjectsRootRef.child(subjectToDelete);
        selectedSubjectsRef.removeValue().addOnSuccessListener(unused -> Toast.makeText(getActivity(), "Subject Deleted",
                Toast.LENGTH_SHORT).show());
    }
    public void deleteViolation(EditText editText) {
        String violationToDelete = editText.getText().toString();
        DatabaseReference violationsRootRef = root.child("Violations");
        DatabaseReference selectedViolationRef = violationsRootRef.child(violationToDelete);
        selectedViolationRef.removeValue().addOnSuccessListener(unused -> Toast.makeText(getActivity(), "Violation Deleted",
                Toast.LENGTH_SHORT).show());
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