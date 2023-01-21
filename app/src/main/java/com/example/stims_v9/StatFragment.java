package com.example.stims_v9;

import android.content.Intent;
import android.database.MatrixCursor;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cursoradapter.widget.SimpleCursorAdapter;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class StatFragment extends Fragment {

    //Initiating Variables

    //Initiating Firebase Database
    private final FirebaseDatabase studentDatabase = FirebaseDatabase.getInstance("https://stims-v9-default-rtdb.asia-southeast1.firebasedatabase.app/");
    private final DatabaseReference root = studentDatabase.getReference();
    private final DatabaseReference studentName = root.child("Users");


    private MyAdapter adapter;
    private ArrayList<Model> list;
    ArrayList<String> suggestions;

    MaterialButton btn_search_student, btn_calendar_view;
    TextInputEditText search_edit_text;
    SearchView search_view;
    List<String> searchResultList = new ArrayList<>();

@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
    View v = inflater.inflate(R.layout.fragment_stat, container, false);


    RecyclerView recyclerView = v.findViewById(R.id.recycler_view_);

    recyclerView.setHasFixedSize(true);
    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

    list = new ArrayList<>();
    adapter = new MyAdapter(getActivity(), list);

    recyclerView.setAdapter(adapter);

    CalendarView calendarView = v.findViewById(R.id.calendarView);
    calendarView.setVisibility(View.GONE);
    btn_calendar_view = v.findViewById(R.id.btn_calendar_view);
    btn_calendar_view.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int visibility = calendarView.getVisibility();
            if (visibility == View.GONE || visibility == View.INVISIBLE) {
                calendarView.setVisibility(View.VISIBLE);
                btn_calendar_view.setText("Hide Calendar");
            } else {
                calendarView.setVisibility(View.GONE);
                btn_calendar_view.setText("");
            }
        }
    });

    btn_search_student = v.findViewById(R.id.btn_student_search);
    btn_search_student.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(getActivity(), StudentList.class);
            startActivity(intent);
        }
    });

    calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
        @Override
        public void onSelectedDayChange(@NonNull CalendarView calendarView, int year, int month, int dayOfMonth) {

            Calendar calendar = Calendar.getInstance();
            calendar.set(year, month, dayOfMonth);
            long date = calendar.getTimeInMillis();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy, MMMM, d,EEEE");
            String dateRef = sdf.format(new Date(date));

            DatabaseReference root = FirebaseDatabase.getInstance("https://stims-v9-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Logs");
            DatabaseReference datePickerRef = root.child(dateRef);

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

    search_edit_text = v.findViewById(R.id.search_edit_text);
    String searchInput = search_edit_text.getText().toString();

    search_edit_text.addTextChangedListener(new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    });



    search_view = v.findViewById(R.id.search_view);
    search_view.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String s) {
            return false;
        }

        @Override
        public boolean onQueryTextChange(String s) {

            DatabaseReference searchRootRef = FirebaseDatabase.getInstance("https://stims-v9-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Scans");
            DatabaseReference searchRef = searchRootRef.child(s);

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
}