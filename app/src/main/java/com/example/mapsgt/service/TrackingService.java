package com.example.mapsgt.service;

import static com.example.mapsgt.MapsGTApplication.CHANNEL_ID;

import android.Manifest;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.example.mapsgt.MainActivity;
import com.example.mapsgt.R;
import com.example.mapsgt.data.dto.UserLocationDto;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class TrackingService extends Service {
    private static final String TAG = TrackingService.class.getSimpleName();
    public static final int DEFAULT_UPDATE_INTERVAL = 10;
    private FirebaseUser user;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        user = FirebaseAuth.getInstance().getCurrentUser();
        requestLocationUpdates();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        sendNotification();

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(this);
        client.removeLocationUpdates(locationCallback);
    }

    private void sendNotification() {
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Create the persistent notification
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(getString(R.string.tracking_enabled_notif))
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.ic_my_location)
                .build();

        startForeground(1, notification);
    }

    private LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            //Get a reference to the database, so your app can perform read and write operations
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
            Location location = locationResult.getLastLocation();
            if (location != null) {
                UserLocationDto userLocationDto = new UserLocationDto();
                userLocationDto.setLongitude(location.getLongitude());
                userLocationDto.setLatitude(location.getLatitude());
                userLocationDto.setIsSharing(true);

                //Save the location data to the database
                ref.child("locations").child("users").child(user.getUid()).setValue(userLocationDto);
            }
        }
    };

    //Initiate the request to track the device's location
    private void requestLocationUpdates() {
        LocationRequest request = new LocationRequest();

        //Specify how often your app should request the device’s location
        request.setInterval(1000 * DEFAULT_UPDATE_INTERVAL);

        //Get the most accurate location data available
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(this);
        int permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);

        //If the app currently has access to the location permission...
        if (permission == PackageManager.PERMISSION_GRANTED) {
            //then request location updates
            client.requestLocationUpdates(request, locationCallback, null);
        }
    }
}
