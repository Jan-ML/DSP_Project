package com.example.crime_tide;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class User {

    public String first_name, last_name, email, street_name, post_code, city, district;
    public String street_number, age;


    //    public ArrayList<Report> report = new ArrayList<>();
//    Map<String, Object> Reports = new HashMap<>();
    ArrayList<Report> Reports = new ArrayList<>();
    public User(){

    }
    public User(String first_name, String last_name, String email, String street_name, String post_code,
                String city, String district, String street_number, String age){
        this.first_name = first_name;
        this.last_name = last_name;
        this.email = email;
        this.street_name = street_name;
        this.post_code = post_code;
        this.city = city;
        this.district = district;
        this.street_number = street_number;
        this.age = age;
    }


    public ArrayList<Report> getReports() {
        return Reports;
    }

    public void setReports(ArrayList<Report> Reports) {
        this.Reports = Reports;
    }
}

class Report{
    int reference_number;
    String crime_type;
    String date;
    String district;

    public Report(){

    }

    public Report(int reference_number, String crime_type, String date, String district) {
        this.reference_number = reference_number;
        this.crime_type = crime_type;
        this.date = date;
        this.district = district;
    }

    public int getReference_number() {
        return reference_number;
    }

    public String getCrime_type() {
        return crime_type;
    }

    public String getDate() {
        return date;
    }

    public String getDistrict() {
        return district;
    }
}