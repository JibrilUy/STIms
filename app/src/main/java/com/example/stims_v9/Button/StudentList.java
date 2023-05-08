package com.example.stims_v9.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
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
import java.util.List;

public class StudentList extends AppCompatActivity {

    DatabaseReference root = FirebaseDatabase.getInstance("https://stims-v9-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference();

    DatabaseReference everySubjectRef = root.child("Subjects");
    DatabaseReference everySectionRef = root.child("Sections");

    MyAdapter2 adapter2;

    ArrayList<Model2> list2;

    MaterialButton btn_refresh, btn_exit;

    Spinner spinnerStudentListSubjects, spinnerStudentListActivitySection;
    RecyclerView recyclerView2;

    ArrayList <String> subjectList = new ArrayList<>();
    ArrayList <String> sectionList = new ArrayList<>();


    String selectedSection;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_list);


        recyclerView2 = findViewById(R.id.recycler_view_2);

        recyclerView2.setHasFixedSize(true);
        recyclerView2.setLayoutManager(new LinearLayoutManager(this));

        list2 = new ArrayList<>();


        spinnerStudentListActivitySection = findViewById(R.id.spinnerStudentListActivitySection);


        updateSectionSpinner();


        btn_refresh = findViewById(R.id.btn_refresh);
        btn_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DatabaseReference everyStudentRef = root.child("Students").child(selectedSection);
                everyStudentRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        list2.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                                String studentName = dataSnapshot.getValue(String.class);
                                Model2 student = new Model2(studentName);
                                list2.add(student);
                        }
                        adapter2 = new MyAdapter2(getApplicationContext(), list2);
                        recyclerView2.setAdapter(adapter2);
                        Log.d("FirebaseDebug", "List2 size: " + list2.size());

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

    public void updateSectionList(DataSnapshot dataSnapshot) {
        sectionList.clear();
        for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
            String value = childSnapshot.getValue(String.class);
            sectionList.add(value);
        }
    }
    public void updateSectionSpinner(){
        everySectionRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                updateSectionList(dataSnapshot);
                spinnerAdapter sectionAdapter = new spinnerAdapter(getApplicationContext(), sectionList);
                spinnerStudentListActivitySection.setAdapter(sectionAdapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {   }});
        spinnerStudentListActivitySection.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedSection = spinnerStudentListActivitySection.getSelectedItem().toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) { }});
    }



}