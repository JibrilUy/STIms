package com.example.stims_v9.display;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.stims_v9.Adapters.MyAdapter;
import com.example.stims_v9.Adapters.ViolationAdapter;
import com.example.stims_v9.Button.ViolationFragment;
import com.example.stims_v9.MainActivity;
import com.example.stims_v9.Model.Model;
import com.example.stims_v9.Model.ViolationModel;
import com.example.stims_v9.R;
import com.example.stims_v9.ScanFragment;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class ViolationDisplayFragment extends Fragment {

    private final FirebaseDatabase studentDatabase = FirebaseDatabase.getInstance("https://stims-v9-default-rtdb.asia-southeast1.firebasedatabase.app/");
    private final DatabaseReference root = studentDatabase.getReference();
    MaterialButton btnExitDisplayViolationDisplayFrag;
    TextView textViewUserNameViolationDisplayFrag;

    String scanResult;

    RecyclerView recyclerViewDisplayViolationFragment;

    ArrayList<ViolationModel> violationList = new ArrayList<>();


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
        recyclerViewDisplayViolationFragment = v.findViewById(R.id.recyclerViewDisplayViolationFragment);

        String scanResult = ScanResultHolder.getScanResult();
        setUserNameTextView(scanResult);

        recyclerViewDisplayViolationFragment.setHasFixedSize(true);
        recyclerViewDisplayViolationFragment.setLayoutManager(new LinearLayoutManager(getActivity()));

        btnExitDisplayViolationDisplayFrag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                replaceFragment(new ScanFragment());
            }
        });

        DatabaseReference userViolationRef = root.child("UserData").child(scanResult).child("violations");
        userViolationRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    violationList.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                        String violation = dataSnapshot.child("violations").getValue(String.class);
                        String description = dataSnapshot.child("description").getValue(String.class);

                        ViolationModel violationDetails = new ViolationModel(violation, description);
                        violationList.add(violationDetails);

                    }
                    ViolationAdapter violationAdapter = new ViolationAdapter(getContext(), violationList);
                    recyclerViewDisplayViolationFragment.setAdapter(violationAdapter);

                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("TAG", "onCancelled: " + error.getMessage()); // add this log
            }
        });



        return v;
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }

    public void setUserNameTextView(String scanResult) {

        DatabaseReference userDatabaseRef = root.child("UserData").child(scanResult);
        userDatabaseRef.child("name").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String userName = snapshot.getValue(String.class);

                    if (userName.isEmpty()) {
                        textViewUserNameViolationDisplayFrag.setText("NO NAME RECORDED");
                    }else{
                        textViewUserNameViolationDisplayFrag.setText(userName);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }


}