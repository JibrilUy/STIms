package com.example.stims_v9;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cursoradapter.widget.SimpleCursorAdapter;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CalendarView;
import android.widget.Spinner;

import androidx.appcompat.widget.SearchView;


import com.example.stims_v9.Adapters.MyAdapter;
import com.example.stims_v9.Button.StudentList;
import com.example.stims_v9.Model.Model;
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

public class StatFragment extends Fragment {




    private final FirebaseDatabase studentDatabase = FirebaseDatabase.getInstance("https://stims-v9-default-rtdb.asia-southeast1.firebasedatabase.app/");
    private final DatabaseReference root = studentDatabase.getReference();
    private final DatabaseReference studentName = root.child("Users");
    private final DatabaseReference attendanceRef = root.child("Attendance");

    private final DatabaseReference everySubjectsRef = root.child("Subjects");

    Spinner spinner_subject_stat_fragment;

    private MyAdapter adapter;
    private ArrayList<Model> list;
    ArrayList<String> subjectList;

    String selectedSubject;

    MaterialButton btnStatFragEveryStudent, btnCalendarView;
    SearchView search_view;
    CalendarView calendarView;
    Calendar calendar = Calendar.getInstance();

    RecyclerView recyclerView;
@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
    View v = inflater.inflate(R.layout.fragment_stat, container, false);

    spinner_subject_stat_fragment = v.findViewById(R.id.spinner_subject_stat_fragment);

    calendarView = v.findViewById(R.id.calendarView);
    recyclerView = v.findViewById(R.id.recycler_view_);
    search_view = v.findViewById(R.id.search_view);

    btnCalendarView = v.findViewById(R.id.btnCalendarView);
    btnStatFragEveryStudent = v.findViewById(R.id.btnStatFragEveryStudent);


    list = new ArrayList<>();
    adapter = new MyAdapter(getActivity(), list);
    subjectList = new ArrayList<>();

    calendarView.setVisibility(View.GONE);
    recyclerView.setHasFixedSize(true);
    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    recyclerView.setAdapter(adapter);

    btnCalendarView.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            hideCalendarView();
        }
    });

    btnStatFragEveryStudent.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            showEveryStudentActivity();
        }
    });

    updateSubjectList();





    calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
        @Override
        public void onSelectedDayChange(@NonNull CalendarView calendarView, int year, int month, int dayOfMonth) {

            calendar.set(year, month, dayOfMonth);
            long date = calendar.getTimeInMillis();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy, MMMM, d,EEEE");
            String dateRef = sdf.format(new Date(date));


            DatabaseReference datePickerRef = attendanceRef.child(selectedSubject).child(dateRef);

            datePickerRef.addValueEventListener(new ValueEventListener() {
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


    search_view.setOnQueryTextListener(new SearchView.OnQueryTextListener() {


        @Override
        public boolean onQueryTextSubmit(String s) {
            return false;
        }

        @Override
        public boolean onQueryTextChange(String s) {

            MatrixCursor cursor = new MatrixCursor(new String[]{"_id", "student_name"});
            DatabaseReference studentNameDatabase = FirebaseDatabase.getInstance("https://stims-v9-default-rtdb.asia-southeast1.firebasedatabase.app/")
                    .getReference("Suggestions");
            studentNameDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    int i = 0;
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        String studentName = dataSnapshot.getValue(String.class);
                        cursor.addRow(new Object[]{i, studentName});
                        Log.d("DataSnapshot", "Data added: " + studentName);
                        i++;
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            SimpleCursorAdapter adapter3 = new SimpleCursorAdapter(getActivity(), android.R.layout.simple_dropdown_item_1line, cursor,
                    new String[]{"student_name"}, new int[]{android.R.id.text1}, 0);

            search_view.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
                @Override
                public boolean onSuggestionSelect(int position) {
                    return false;
                }

                @Override
                public boolean onSuggestionClick(int position) {
                    Cursor cursor = (Cursor) search_view.getSuggestionsAdapter().getItem(position);

                    //Column must not be 0
                    @SuppressLint("Range") String suggestion = cursor.getString(cursor.getColumnIndex("student_name"));
                    search_view.setQuery(suggestion, false);
                    return false;
                }
            });
            search_view.setSuggestionsAdapter(adapter3);

            DatabaseReference searchRootRef = FirebaseDatabase.getInstance("https://stims-v9-default-rtdb.asia-southeast1.firebasedatabase.app/")
                    .getReference("Scans");
            DatabaseReference searchRef = searchRootRef.child(s).child(selectedSubject);
            searchRef.addValueEventListener(new ValueEventListener() {
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

            return false;
        }
    });
        return v;
    }

    public void hideCalendarView(){
        int visibility = calendarView.getVisibility();
        if (visibility == View.GONE || visibility == View.INVISIBLE) {
            calendarView.setVisibility(View.VISIBLE);
        } else {
            calendarView.setVisibility(View.GONE);
        }
    }

    public void hideBtnForSearchView(){
                btnCalendarView.setVisibility(View.GONE);
                btnStatFragEveryStudent.setVisibility(View.GONE);
    }

    public void showEveryStudentActivity(){
        Intent intent = new Intent(getActivity(), StudentList.class);
        startActivity(intent);
    }

    private void updateSubjectList() {
        everySubjectsRef.addValueEventListener(new ValueEventListener() {
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
                    spinner_subject_stat_fragment.setAdapter(subjectAdapter);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {   }
        });

        spinner_subject_stat_fragment.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedSubject = spinner_subject_stat_fragment.getSelectedItem().toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {   } });

    }




    }







