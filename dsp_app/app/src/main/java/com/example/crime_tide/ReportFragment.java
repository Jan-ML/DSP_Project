package com.example.crime_tide;

import android.app.Person;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ReportFragment extends Fragment {
    private EditText reference_number;
    private EditText crime_type;
    private EditText date;
    private EditText district;
    private FirebaseUser user;
    Button send_crime;
    DatabaseReference user_report;
    String userID;
//    Map<String, Object> Reports = new HashMap<>();
    ArrayList<Report> Reports = new ArrayList<>();
    String token;
    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_report, container, false);
        reference_number = v.findViewById(R.id.ref_number);
        crime_type =  v.findViewById(R.id.crime_type);
        date =  v.findViewById(R.id.date);
        district =  v.findViewById(R.id.crime_district);
        send_crime =  v.findViewById(R.id.send_crime);
        user = FirebaseAuth.getInstance().getCurrentUser();
        user_report = FirebaseDatabase.getInstance().getReference("Users");
        userID = user.getUid();
        user_report.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                User userProfile = snapshot.getValue(User.class);
//                User userProfile = snapshot.child("Reports").getValue(User.class);
                if (userProfile != null){
                    Reports = userProfile.getReports();
                    token = userProfile.district;
                    System.out.println("REPORT");
//                    System.out.println(Reports.get(1).date);
                }
            }
            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                Toast.makeText(getActivity(), "Something wrong happened!", Toast.LENGTH_LONG).show();
            }
        });

        send_crime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertCrimeData(Reports, token);
            }
        });
        return v;
    }

    private void insertCrimeData(ArrayList<Report> Reports, String token) {
        System.out.println(token);
        FirebaseMessaging.getInstance().subscribeToTopic(token);
        Map<String, Object> values = new HashMap<>();
        int ref = Integer.parseInt(reference_number.getText().toString().trim());
        String crime = crime_type.getText().toString().trim();
        String occurrence = date.getText().toString().trim();
        String location = district.getText().toString().trim();
        Report report = new Report(ref, crime, occurrence, location);
//        ArrayList<Report> crime_report = new ArrayList<>();
//        crime_report.add(report);
        Reports.add(report);
        User temp_user = new User();
        temp_user.setReports(Reports);
        values.put("Reports", temp_user.getReports());
        user_report.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).updateChildren(values);
//        user_report.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Reports").push().setValue(values);
        Toast.makeText(getActivity(), "Crime has been sent", Toast.LENGTH_SHORT).show();

        FcmNotificationsSender notificationsSender = new FcmNotificationsSender("/topics/"+token,
                "Alert! New Crime in "+ location, "Crime type: "+ crime, requireActivity().getApplicationContext(), getActivity());
        notificationsSender.SendNotifications();

    }

}
