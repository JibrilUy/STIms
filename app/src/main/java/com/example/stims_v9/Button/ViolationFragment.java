package com.example.stims_v9.Button;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.stims_v9.Adapters.SharedViewModel;
import com.example.stims_v9.R;
import com.example.stims_v9.ScanFragment;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ViolationFragment extends Fragment {

    final FirebaseDatabase studentDatabase = FirebaseDatabase.getInstance("https://stims-v9-default-rtdb.asia-southeast1.firebasedatabase.app/");
    DatabaseReference root = studentDatabase.getReference();
    SharedViewModel viewModel;
    Spinner spinnerViolations;
    EditText editTextAddDescriptionViolationFrag;
    MaterialButton btnExitViolationFragment, btnAddViolationViolationFragment;

    ArrayList<String> everyViolations = new ArrayList<>();

    String userID, selectedViolation;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_violation, container, false);
        viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        userID = viewModel.getData().getValue();

        spinnerViolations = v.findViewById(R.id.spinnerViolations);
        editTextAddDescriptionViolationFrag = v.findViewById(R.id.editTextAddDescriptionViolationFrag);
        btnAddViolationViolationFragment = v.findViewById(R.id.btnAddViolationViolationFragment);
        btnExitViolationFragment = v.findViewById(R.id.btnExitViolationFragment);

        DatabaseReference everyViolationRef = root.child("Violations");

        everyViolationRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                updateViolationSpinner(dataSnapshot);
                if(isAdded()) {
                    ArrayAdapter<String> violationAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, everyViolations);
                    spinnerViolations.setAdapter(violationAdapter);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {   }});

        spinnerViolations.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedViolation = spinnerViolations.getSelectedItem().toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) { }});


        btnAddViolationViolationFragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addViolations();
                closeFragment(new ScanFragment());
            }
        });

        btnExitViolationFragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeFragment(new ScanFragment());
            }
        });

        return v;
    }

    private void closeFragment(Fragment fragment) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.detach(this);
        fragmentTransaction.replace(R.id.frame_layout,fragment);
        fragmentTransaction.commit();
    }
    public void updateViolationSpinner(DataSnapshot dataSnapshot) {
        everyViolations.clear();
        for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
            String value = childSnapshot.getValue(String.class);
            everyViolations.add(value);
        }
    }

    public void addViolations(){
        if(!TextUtils.isEmpty(userID)) {
            DatabaseReference userDatabaseRef = root.child("UserData").child(userID).child("violations").push();
            String violationDescription = editTextAddDescriptionViolationFrag.getText().toString();

            userDatabaseRef.child("violation").setValue(selectedViolation);
            userDatabaseRef.child("description").setValue(violationDescription);

            userDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    Toast.makeText(getActivity(), "VIOLATION ADDED", Toast.LENGTH_SHORT).show();
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {  }
            });
        }
    }



}