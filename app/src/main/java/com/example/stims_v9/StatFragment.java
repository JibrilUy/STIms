package com.example.stims_v9;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class StatFragment extends Fragment {
//    TODO Filter by Day where in A list of names in that day is created
//    Make a Calendar picker in which that filter by day works
//    Make a Search Bar

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_stat, container, false);
    }
}