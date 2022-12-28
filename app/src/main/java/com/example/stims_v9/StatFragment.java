package com.example.stims_v9;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class StatFragment extends Fragment {
//    TODO Filter by Day where in A list of names in that day is created
//    Make a Calendar picker in which that filter by day works
//    Make a Search Bar

    //Initiating Variables
    private Button btn_list_view;


@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
    View v = inflater.inflate(R.layout.fragment_stat, container, false);

        btn_list_view = v.findViewById(R.id.btn_list_view);

        btn_list_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(),ShowActivity.class));
            }
        });




        return v;
    }
}