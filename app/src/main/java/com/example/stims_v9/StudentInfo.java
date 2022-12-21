package com.example.stims_v9;

public class StudentInfo {

    private String student_name;

    public StudentInfo() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public StudentInfo(String student_name){
        this.student_name = student_name;
    }

}


