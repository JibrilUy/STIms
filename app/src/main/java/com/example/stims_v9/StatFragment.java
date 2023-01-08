package com.example.stims_v9;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class StatFragment extends Fragment {

    //Initiating Variables

    //Initiating Firebase Database
    private final FirebaseDatabase studentDatabase = FirebaseDatabase.getInstance("https://stims-v9-default-rtdb.asia-southeast1.firebasedatabase.app/");
    private final DatabaseReference root = studentDatabase.getReference();
    private final DatabaseReference studentName = root.child("Users");


    private MyAdapter adapter;
    private MyAdapter2 adapter2;
    private ArrayList<Model> list;
    private ArrayList<Model2> list2;

    Button btn_search;
    EditText edit_text_search_bar;
    List<String> scanResultNodes = new ArrayList<>();

@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
    View v = inflater.inflate(R.layout.fragment_stat, container, false);


    RecyclerView recyclerView = v.findViewById(R.id.recycler_view_);
    RecyclerView recyclerView2 = v.findViewById(R.id.recycler_view_2);

    recyclerView.setHasFixedSize(true);
    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

    recyclerView2.setHasFixedSize(true);
    recyclerView2.setLayoutManager(new LinearLayoutManager(getActivity()));

    list = new ArrayList<>();
    list2 = new ArrayList<>();

    adapter = new MyAdapter(getActivity(), list);
    adapter2 = new MyAdapter2(getActivity(), list2);


    recyclerView.setAdapter(adapter);
    recyclerView2.setAdapter(adapter2);


    studentName.addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            list2.clear();
            for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                Model2 nameModel = dataSnapshot.getValue(Model2.class);
                list2.add(nameModel);
        }
    }
        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    });

    edit_text_search_bar = v.findViewById(R.id.edit_text_search_bar);
    btn_search = v.findViewById(R.id.btn_search);
    btn_search.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String searchResult = edit_text_search_bar.getText().toString();
            DatabaseReference dateNodeRef = FirebaseDatabase.getInstance("https://stims-v9-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference(searchResult);
            dateNodeRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    list.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                        Model model = dataSnapshot.getValue(Model.class);
                        list.add(model);
                    }
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    });
//just to push 4

        return v;
    }
}