package com.example.delvs;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DetectUsers extends Thread {

    private DatabaseReference usersReference;
    private double currentLatitude;
    private double currentLongitude;
    private Context context;

    private static final double EARTH_RADIUS_KM = 6371.0; // Earth radius in kilometers
    private static final double NEARBY_DISTANCE_THRESHOLD_KM = 3.0; // Threshold for considering users as nearby

    public DetectUsers(Context context, double currentLatitude, double currentLongitude) {
        this.context = context;
        this.currentLatitude = currentLatitude;
        this.currentLongitude = currentLongitude;
        this.usersReference = FirebaseDatabase.getInstance().getReference("Users");
    }

    public void compareLocations() {
        final List<String> nearbyUserNames = new ArrayList<>();

        usersReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Users user = snapshot.getValue(Users.class);
                    if (user != null) {
                        double userLatitude = user.getLatitude();
                        double userLongitude = user.getLongitude();

                        // Calculate distance using Haversine formula
                        double distance = calculateHaversineDistance(
                                currentLatitude, currentLongitude, userLatitude, userLongitude);

                        // Check if the user is within the defined distance threshold
                        if (distance <= NEARBY_DISTANCE_THRESHOLD_KM) {
                            // The user is nearby; accumulate the user names
                            nearbyUserNames.add(user.getUserName());
                        }
                    }
                }

                // Display the names of nearby users in a Toast
                if (!nearbyUserNames.isEmpty()) {
                    StringBuilder message = new StringBuilder("Nearby Users:\n");
                    for (String userName : nearbyUserNames) {
                        message.append(userName).append("\n");
                    }
                    showToast(message.toString());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle errors
            }
        });
    }

    private double calculateHaversineDistance(double lat1, double lon1, double lat2, double lon2) {
        // Haversine formula to calculate distance between two points on the Earth's surface
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS_KM * c; // Distance in kilometers
    }

    private void showToast(final String message) {
        // Run on UI thread to show Toast
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, message, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void run() {
        compareLocations();
        // Implement logic for detecting users
    }
}
