package com.example.delvs;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.delvs.databinding.ActivityHomeBinding;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class HomeActivity extends AppCompatActivity {

    ActivityHomeBinding binding;
    String firstName, lastName, age, userName;
    FirebaseDatabase db;
    DatabaseReference reference;
    boolean RegistrationStatus = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firstName = binding.firstName.getEditText().getText().toString();
                lastName = binding.lastName.getEditText().getText().toString();
                age = binding.age.getEditText().getText().toString();
                userName = binding.userName.getEditText().getText().toString();

                if (!firstName.isEmpty() && !lastName.isEmpty() && !age.isEmpty() && !userName.isEmpty()) {
                    Users users = new Users(firstName, lastName, age, userName);
                    db = FirebaseDatabase.getInstance();
                    reference = db.getReference("Users");
                    reference.child(userName).setValue(users).addOnCompleteListener(task -> {
                        binding.firstName.getEditText().setText("");
                        binding.lastName.getEditText().setText("");
                        binding.age.getEditText().setText("");
                        binding.userName.getEditText().setText("");
                        Toast.makeText(HomeActivity.this, "Successfully Registered", Toast.LENGTH_SHORT).show();
                        // Set RegistrationStatus to true after successful registration
                        RegistrationStatus = true;
                        // Start the LocationActivity
                        startActivity(new Intent(HomeActivity.this, LocationAccess.class));
                        finish();
                    });
                } else {
                    Toast.makeText(HomeActivity.this, "Unable to register. Try again!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
