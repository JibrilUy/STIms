package com.example.stims_v9.display;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.stims_v9.MainActivity;
import com.example.stims_v9.R;
import com.example.stims_v9.ScanFragment;
import com.google.android.material.button.MaterialButton;


public class ViolationDisplayFragment extends Fragment {


    MaterialButton btnExitDisplayViolationDisplayFrag;
    TextView textViewUserNameViolationDisplayFrag;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_violation_display, container, false);

        btnExitDisplayViolationDisplayFrag = v.findViewById(R.id.btnExitDisplayViolationDisplayFrag);
        textViewUserNameViolationDisplayFrag = v.findViewById(R.id.textViewUserNameViolationDisplayFrag);



        btnExitDisplayViolationDisplayFrag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                replaceFragment(new ScanFragment());
            }
        });


    displayViolationRecycler("something");

        return v;
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }

    public void displayViolationRecycler(String scanResult){
        if(!scanResult.isEmpty()) {
            textViewUserNameViolationDisplayFrag.setText(scanResult);
        }
    }

}