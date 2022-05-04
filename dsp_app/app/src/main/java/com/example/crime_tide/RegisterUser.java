package com.example.crime_tide;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import org.jetbrains.annotations.NotNull;

import java.util.regex.Pattern;

public class RegisterUser extends AppCompatActivity implements View.OnClickListener{
    private ImageButton logo;
    private TextView register_user;
    private EditText edit_first_name, edit_last_name, edit_age, edit_email, edit_password,
    edit_street_number, edit_street_name, edit_post_code, edit_city, edit_district;
    private ProgressBar progressBar;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);

        mAuth = FirebaseAuth.getInstance();

        logo = (ImageButton) findViewById(R.id.imageButton);
        logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentLoadNewActivity = new Intent(RegisterUser.this, MainActivity.class);
                startActivity(intentLoadNewActivity);
            }
        });

        register_user = (Button) findViewById(R.id.register_user);
        register_user.setOnClickListener(this);

        edit_first_name = (EditText) findViewById(R.id.first_name);
        edit_last_name = (EditText) findViewById(R.id.last_name);
        edit_password = (EditText) findViewById(R.id.password);
        edit_age = (EditText) findViewById(R.id.age);
        edit_email = (EditText) findViewById(R.id.email);
        edit_street_number = (EditText) findViewById(R.id.street_number);
        edit_street_name = (EditText) findViewById(R.id.street_name);
        edit_post_code = (EditText) findViewById(R.id.post_code);
        edit_city = (EditText) findViewById(R.id.city);
        edit_district = (EditText) findViewById(R.id.district);

        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.register_user:
                registerUser();
                break;

        }
    }

    private void registerUser() {
        String email = edit_email.getText().toString().trim();
        String password = edit_password.getText().toString().trim();
        String first_name = edit_first_name.getText().toString().trim();
        String last_name = edit_last_name.getText().toString().trim();
        String age = edit_age.getText().toString().trim();
        String street_number = edit_street_number.getText().toString().trim();
        String street_name = edit_street_name.getText().toString().trim();
        String post_code = edit_post_code.getText().toString().trim();
        String city = edit_city.getText().toString().trim();
        String district = edit_district.getText().toString().trim();

        if(first_name.isEmpty()){
            edit_first_name.setError("First name is required");
            edit_first_name.requestFocus();
            return;
        }

        if(last_name.isEmpty()){
            edit_last_name.setError("Last name is required");
            edit_last_name.requestFocus();
            return;
        }

        if(age.isEmpty()){
            edit_age.setError("Age is required");
            edit_age.requestFocus();
            return;
        }

        if(email.isEmpty()){
            edit_email.setError("Email is required");
            edit_email.requestFocus();
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            edit_email.setError("Please provide valid email");
            edit_email.requestFocus();
            return;
        }
        if(password.isEmpty()){
            edit_password.setError("Password is required");
            edit_password.requestFocus();
            return;
        }
        if(password.length() < 6){
            edit_password.setError("Minimum password length should be 6 characters!");
            edit_password.requestFocus();
            return;
        }
        if(street_number.isEmpty()){
            edit_street_number.setError("Street number is required");
            edit_street_number.requestFocus();
            return;
        }
        if(street_name.isEmpty()){
            edit_street_name.setError("Street name is required");
            edit_street_name.requestFocus();
            return;
        }
        if(post_code.isEmpty()){
            edit_post_code.setError("Post code is required");
            edit_post_code.requestFocus();
            return;
        }
        if(city.isEmpty()){
            edit_city.setError("City is required");
            edit_city.requestFocus();
            return;
        }
        if(district.isEmpty()){
            edit_district.setError("District is required");
            edit_district.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull @org.jetbrains.annotations.NotNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            User user = new User(first_name, last_name, email, street_name, post_code, city, district,
                                    street_number, age);
                            FirebaseDatabase.getInstance().getReference("Users")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull @NotNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(RegisterUser.this, "User has been registered successfully!", Toast.LENGTH_LONG).show();
                                        progressBar.setVisibility(View.GONE);
                                    }else {
                                        Toast.makeText(RegisterUser.this, "Failed to register! Try again!", Toast.LENGTH_LONG).show();
                                        progressBar.setVisibility(View.GONE);

                                    }
                                }
                            });
                        }else{
                            Toast.makeText(RegisterUser.this, "Failed to register!", Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
    }
}