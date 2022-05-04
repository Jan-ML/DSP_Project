package com.example.crime_tide;

import java.util.ArrayList;


public class OccurredCrimes {
    ArrayList<Integer> occurred_crimes = new ArrayList<>();
    ArrayList<String> occurred_month = new ArrayList<>();

    public ArrayList<Integer> getOccurred_crimes() {
        return occurred_crimes;
    }

    public void setOccurred_crimes(ArrayList<Integer> occurred_crimes) {
        this.occurred_crimes = occurred_crimes;
    }

    public ArrayList<String> getOccurred_month() {
        return occurred_month;
    }

    public void setOccurred_month(ArrayList<String> occurred_month) {
        this.occurred_month = occurred_month;
    }
}
