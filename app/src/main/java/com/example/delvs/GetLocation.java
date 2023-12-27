package com.example.delvs;

import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.delvs.databinding.ActivityGetLocationBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class GetLocation extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener {

    private Location currentLocation;
    private boolean isPermissionGranted;
    private double latitude, longitude;
    private FusedLocationProviderClient fusedLocationProviderClient;

    // Firebase database reference

    // Threads

    private UpdateLocation updateLocationThread;
    private DetectUsers detectUsersThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityGetLocationBinding getLocationBinding = ActivityGetLocationBinding.inflate(getLayoutInflater());
        setContentView(getLocationBinding.getRoot());
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        checkPermission();

        Button getLocationButton = getLocationBinding.getLocation;
        getLocationButton.setOnClickListener(view -> {
            // Check if the currentLocation is available
            getLastLocation();
        });

        // Initialize threads
        updateLocationThread = new UpdateLocation();
        detectUsersThread = new DetectUsers(this, latitude, longitude);
    }

    private void checkPermission() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            int FINE_PERMISSION_CODE = 1;
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, FINE_PERMISSION_CODE);
        } else {
            isPermissionGranted = true;
            getLastLocation();
        }
    }

    private void getLastLocation() {
        if (!isPermissionGranted) {
            checkPermission();
            return;
        }

        Task<Location> task = fusedLocationProviderClient.getLastLocation();

        task.addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    currentLocation = location;
                    Toast.makeText(GetLocation.this, "Your location Latitude: " + location.getLatitude() +
                            "\nYour location Longitude: " + location.getLongitude(), Toast.LENGTH_SHORT).show();

                    // Start the threads after obtaining the location
                    startThreads();
                } else {
                    Toast.makeText(GetLocation.this, "Could not find location", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }



    private void startThreads() {
        // Start the threads only if they are not already running
        if (!updateLocationThread.isAlive()) {
            updateLocationThread.start();
        }

        if (!detectUsersThread.isAlive()) {
            detectUsersThread.start();
        }
    }

    @Override
    public void onMapClick(@NonNull LatLng latLng) {

    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {

    }

    private class UpdateLocation extends Thread {
        public void run() {
            latitude = currentLocation.getLatitude();
            longitude = currentLocation.getLongitude();
            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
            String studentUser = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(studentUser);
            Map<String, Object> updates = new HashMap<>();
            updates.put("latitude", latitude);
            updates.put("longitude", longitude);
            databaseReference.updateChildren(updates);
            Toast.makeText(GetLocation.this, "Location details are being updated", Toast.LENGTH_SHORT).show();
        }
    }
}

