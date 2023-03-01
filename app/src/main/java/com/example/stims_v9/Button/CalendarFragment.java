package com.example.stims_v9.Button;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CalendarView;
import android.widget.Spinner;

import com.example.stims_v9.Adapters.MyAdapter;
import com.example.stims_v9.Model.Model;
import com.example.stims_v9.R;
import com.example.stims_v9.ScanFragment;
import com.example.stims_v9.StatFragment;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class CalendarFragment extends Fragment {

    private final DatabaseReference root = FirebaseDatabase.getInstance("https://stims-v9-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference();
    private final DatabaseReference attendanceRef = root.child("Attendance");
    private final DatabaseReference everySubjectsRef = root.child("Subjects");
    private final DatabaseReference everySectionsRef = root.child("Sections");

    CalendarView calendarViewCalendarFrag;
    Calendar calendar = Calendar.getInstance();
    RecyclerView recyclerViewCalendarFrag;

    Spinner spinnerSectionCalendarFrag, spinnerSubjectCalendarFrag;
    MaterialButton btnExitCalendarFragment;

    String dateRef;
    ArrayList<Model> list = new ArrayList<>();
    ArrayList<String> subjectList = new ArrayList<>();
    ArrayList<String> sectionList = new ArrayList<>();
    MyAdapter adapter = new MyAdapter(getActivity(), list);
    ;
    String selectedSubject, selectedSection;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);  }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_calendar, container, false);

        calendarViewCalendarFrag = v.findViewById(R.id.calendarViewCalendarFrag);
        spinnerSectionCalendarFrag = v.findViewById(R.id.spinnerSectionCalendarFrag);
        spinnerSubjectCalendarFrag = v.findViewById(R.id.spinnerSubjectCalendarFrag);
        btnExitCalendarFragment = v.findViewById(R.id.btnExitCalendarFragment);
        recyclerViewCalendarFrag = v.findViewById(R.id.recyclerViewCalendarFrag);

        updateSubjectSpinner();
        updateSectionSpinner();

        recyclerViewCalendarFrag.setHasFixedSize(true);
        recyclerViewCalendarFrag.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerViewCalendarFrag.setAdapter(adapter);

        btnExitCalendarFragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeFragment(new StatFragment());
            }
        });


        calendarViewCalendarFrag.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int year, int month, int dayOfMonth) {

                calendar.set(year, month, dayOfMonth);
                long date = calendar.getTimeInMillis();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy, MMMM, d,EEEE");
                dateRef = sdf.format(new Date(date));

                DatabaseReference datePickerRef = attendanceRef.child(selectedSection).child(selectedSubject).child(dateRef);

                datePickerRef.addListenerForSingleValueEvent(new ValueEventListener() {
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







        return v;
    }

    private void closeFragment(Fragment fragment) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.detach(this);
        fragmentTransaction.replace(R.id.frame_layout,fragment);
        fragmentTransaction.commit();
    }
    private void updateSubjectSpinner() {
        everySubjectsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                subjectList.clear();
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    String value = childSnapshot.getValue(String.class);
                    subjectList.add(value);
                }
                adapter.notifyDataSetChanged();
                if(isAdded()) {
                    ArrayAdapter<String> subjectAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, subjectList);
                    spinnerSubjectCalendarFrag.setAdapter(subjectAdapter);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {   }
        });
        spinnerSubjectCalendarFrag.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedSubject = spinnerSubjectCalendarFrag.getSelectedItem().toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {   } });

    }
    private void updateSectionSpinner() {
        everySectionsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                sectionList.clear();
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    String value = childSnapshot.getValue(String.class);
                    sectionList.add(value);
                }
                adapter.notifyDataSetChanged();
                if(isAdded()) {
                    ArrayAdapter<String> sectionAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, sectionList);
                    spinnerSectionCalendarFrag.setAdapter(sectionAdapter);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {   }
        });
        spinnerSectionCalendarFrag.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedSection = spinnerSectionCalendarFrag.getSelectedItem().toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {   } });
    }

}