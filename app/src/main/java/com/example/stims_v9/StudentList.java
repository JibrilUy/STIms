package com.example.stims_v9;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class StudentList extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_list);

        //Initiating Firebase Database
        final FirebaseDatabase studentDatabase = FirebaseDatabase.getInstance("https://stims-v9-default-rtdb.asia-southeast1.firebasedatabase.app/");
        final DatabaseReference root = studentDatabase.getReference();

        MyAdapter2 adapter2;

        ArrayList<Model2> list2;

        RecyclerView recyclerView2 = findViewById(R.id.recycler_view_2);

        recyclerView2.setHasFixedSize(true);
        recyclerView2.setLayoutManager(new LinearLayoutManager(this));

        list2 = new ArrayList<>();

        adapter2 = new MyAdapter2(this, list2);

        DatabaseReference studentName = root.child("Users");


        Button btn_refresh = findViewById(R.id.btn_refresh);
        btn_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                studentName.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        list2.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                            Model2 nameModel = dataSnapshot.getValue(Model2.class);
                            list2.add(nameModel);
                        }
                        recyclerView2.setAdapter(adapter2);

                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });







    }
}