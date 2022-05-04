package com.example.crime_tide;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class LogsFragment extends Fragment {
    private FirebaseUser user;
    private DatabaseReference reference;
    private String userID;
    private ListView listView;
    private List<String> list_of_crimes;
    private ArrayAdapter arrayAdapter;
    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_log, container, false);
        listView = v.findViewById(R.id.list_of_logs);
        list_of_crimes = new ArrayList();

        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        userID = user.getUid();
        reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String crime_type = "";
                String date = "";
                String district = "";
                int ref_number;
                User userProfile = snapshot.getValue(User.class);
                System.out.println("Reports");
                assert userProfile != null;
                if (!userProfile.Reports.isEmpty()){
                    for(int i = 0; i < userProfile.getReports().size(); i++) {
                        ref_number =  userProfile.getReports().get(i).reference_number;
                        date = userProfile.getReports().get(i).date;
                        district = userProfile.getReports().get(i).district;
                        crime_type = userProfile.getReports().get(i).crime_type +
                                " <" + String.valueOf(ref_number) + "> " + "\nDate: "+ date +
                                "\nDistrict: " + district;
                        list_of_crimes.add(crime_type);
                    }

                }
                else {
                    list_of_crimes.add("No Reported Crimes!");
                }
                arrayAdapter = new ArrayAdapter(v.getContext(),
                        android.R.layout.simple_expandable_list_item_1,list_of_crimes);
                listView.setAdapter(arrayAdapter);
                System.out.println(list_of_crimes);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), "Something wrong happened!", Toast.LENGTH_LONG).show();
            }
        });
        return v;
    }

}
