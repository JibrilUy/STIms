package com.example.stims_v9.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.stims_v9.Adapters.MyAdapter2;
import com.example.stims_v9.Adapters.spinnerAdapter;
import com.example.stims_v9.Model.Model2;
import com.example.stims_v9.R;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class StudentList extends AppCompatActivity {


    MyAdapter2 adapter2;

    ArrayList<Model2> list2;

    MaterialButton btn_refresh, btn_exit;

    Spinner spinnerStudentListSubjects;
    RecyclerView recyclerView2;

    ArrayList <String> subjectList = new ArrayList<>();

    String selectedSubject;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_list);

        DatabaseReference root = FirebaseDatabase.getInstance("https://stims-v9-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference();

        DatabaseReference everySubjectRef = root.child("Subjects");



        recyclerView2 = findViewById(R.id.recycler_view_2);

        recyclerView2.setHasFixedSize(true);
        recyclerView2.setLayoutManager(new LinearLayoutManager(this));

        list2 = new ArrayList<>();

        adapter2 = new MyAdapter2(this, list2);

        spinnerStudentListSubjects = findViewById(R.id.spinnerStudentListSubjects);



        everySubjectRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                updateSubjectSpinner(dataSnapshot);
                spinnerAdapter subjectAdapter = new spinnerAdapter(getApplicationContext(), subjectList);
                    spinnerStudentListSubjects.setAdapter(subjectAdapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {   }});

        spinnerStudentListSubjects.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedSubject = spinnerStudentListSubjects.getSelectedItem().toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) { }});


        btn_refresh = findViewById(R.id.btn_refresh);
        btn_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DatabaseReference everyStudentRef = root.child("Students").child(selectedSubject);
                everyStudentRef.addValueEventListener(new ValueEventListener() {
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
                    public void onCancelled(@NonNull DatabaseError error) {  }
                });
            }
        });

        btn_exit = findViewById(R.id.btn_exit);
        btn_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    public void updateSubjectSpinner(DataSnapshot dataSnapshot) {
        subjectList.clear();
        for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
            String value = childSnapshot.getValue(String.class);
            subjectList.add(value);
        }
    }



}