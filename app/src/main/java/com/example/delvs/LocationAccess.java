package com.example.delvs;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class LocationAccess extends AppCompatActivity {
    Button setLocation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_access);
        setLocation = findViewById(R.id.setLocation);
        setLocation.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(LocationAccess.this, GetLocation.class));
                        finish();
                    }
                }
        );

    }
}
