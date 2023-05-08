package com.example.stims_v9.Model;

public class ViolationModel {
    private String violation;
    private String description;

    public ViolationModel() {
    }

    public ViolationModel(String violation, String description) {
        this.violation = violation;
        this.description = description;
    }

    public String getViolation() {
        return violation;
    }

    public void setViolation(String violation) {
        this.violation = violation;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}