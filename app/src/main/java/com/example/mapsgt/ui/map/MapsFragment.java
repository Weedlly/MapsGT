package com.example.mapsgt.ui.map;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.mapsgt.R;
import com.example.mapsgt.data.dto.UserLocation;
import com.example.mapsgt.data.entities.User;
import com.example.mapsgt.enumeration.MovingStyleEnum;
import com.example.mapsgt.network.RetrofitClient;
import com.example.mapsgt.network.model.location.LocationResponse;
import com.example.mapsgt.network.model.location.RoutesItem;
import com.example.mapsgt.service.TrackingService;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapsFragment extends Fragment implements
        LocationListener, OnMapReadyCallback {
    private static final String TAG = "LocationFragment";
    private static final int ACCESS_FINE_LOCATION_CODE = 100;
    final RoutesItem[] routesItem = {new RoutesItem()};
    private Switch sw_sharing;
    private Switch sw_movementHistory;
    private boolean isTurnOnMovementHistory = false;
    private PolylineOptions movementHistoryPolylineOptions;
    private TextView tv_distance;
    private TextView tv_duration;
    private Button btn_start_moving;
    private Button btn_stop_moving;
    private LocationManager mLocationManager;
    private GoogleMap mGoogleMap;
    private Button mSearchButton;
    private DatabaseReference mDatabase;
    private ValueEventListener mValueEventListener;
    private UserLocation currentUser;
    private List<String> friendIdList = new ArrayList<>();
    private List<UserLocation> friendLocations = new ArrayList<>();
    private MapManagement mapManagement;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mapManagement = new MapManagement();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        movementHistoryPolylineOptions = new PolylineOptions()
                .color(Color.LTGRAY)
                .width(8f);
        //Check whether GPS tracking is enabled
        mLocationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        if (!mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            getActivity().finish();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_maps, container, false);

        tv_distance = view.findViewById(R.id.tv_distance);
        tv_duration = view.findViewById(R.id.tv_duration);
        sw_sharing = view.findViewById(R.id.sw_sharing);
        sw_movementHistory = view.findViewById((R.id.sw_movementHistory));
        btn_start_moving = view.findViewById(R.id.btn_start_moving);
        btn_stop_moving = view.findViewById(R.id.btn_stop_moving);

        btn_start_moving.setOnClickListener(view1 -> {
            mapManagement.setMapMode(MapMode.MOVING);
            btn_start_moving.setVisibility(View.GONE);
            btn_stop_moving.setVisibility(View.VISIBLE);
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(currentUser.getLatitude(), currentUser.getLongitude()), 25f));
        });

        btn_stop_moving.setOnClickListener(view13 -> {
            mapManagement.setMapMode(MapMode.SHOW);
            mapManagement.setDestination(null);
            btn_stop_moving.setVisibility(View.GONE);
            btn_start_moving.setVisibility(View.GONE);
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(currentUser.getLatitude(), currentUser.getLongitude()), 15f));
            renderAllMarker();
            tv_duration.setVisibility(View.GONE);
            tv_distance.setVisibility(View.GONE);
        });

        sw_sharing.setOnClickListener(view12 -> {
            if (sw_sharing.isChecked()) {
                startTrackerService();
                showFriendLocation();
            } else {
                mapManagement.setGoingFriendId(null);
                stopTrackerService();
                renderAllMarker();
            }
        });
        sw_movementHistory.setOnClickListener(view12 -> {
            if (sw_movementHistory.isChecked()) {
                isTurnOnMovementHistory = true;
                mGoogleMap.addPolyline(movementHistoryPolylineOptions);
            } else {
                isTurnOnMovementHistory = false;
                Polyline mPolyline;
                mPolyline = mGoogleMap.addPolyline(movementHistoryPolylineOptions);
                mPolyline.remove();
                movementHistoryPolylineOptions = new PolylineOptions()
                        .color(Color.LTGRAY)
                        .width(8f);
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);

        checkPermission(Manifest.permission.ACCESS_FINE_LOCATION, ACCESS_FINE_LOCATION_CODE);
    }


    @Override
    public void onResume() {
        super.onResume();
        requestLocationUpdatesListener();
    }

    @Override
    public void onPause() {
        super.onPause();
        mLocationManager.removeUpdates(this);
    }

    private void addDestinationPoint(LatLng latLng) {
        mapManagement.setDestination(latLng);

        // Creating MarkerOptions
        MarkerOptions options = new MarkerOptions()
                .position(latLng)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));

        renderAllMarker();
        mGoogleMap.addMarker(options);
        getDirection(new LatLng(currentUser.getLatitude(), currentUser.getLongitude()), latLng);
    }

    private void drawDirectionLine(LatLng origin, LatLng dest) {
        RoutesItem route = routesItem[0];
        List<List<Double>> coordinates = route.getGeometry().getCoordinates();
        LatLng nextStep = origin;
        PolylineOptions polylineOptions = new PolylineOptions();
        for (List<Double> step : coordinates) {
            LatLng lng = new LatLng(step.get(1), step.get(0));
            polylineOptions.add(nextStep, lng);
            nextStep = lng;
        }
        polylineOptions.add(nextStep, dest);
        tv_duration.setVisibility(View.VISIBLE);
        tv_distance.setVisibility(View.VISIBLE);
        tv_duration.setText("Thời gian: " + formatDuration(route.getDuration()));
        tv_distance.setText("Khoảng cách: " + formatDistance(route.getDistance()));
        if (mapManagement.getMapMode().equals(MapMode.SHOW)) {
            btn_start_moving.setVisibility(View.VISIBLE);
        }
        mGoogleMap.addPolyline(polylineOptions);
    }

    private void getDirection(LatLng origin, LatLng dest) {
        if (origin == null || dest == null) {
            return;
        }
        Call<LocationResponse> call = RetrofitClient.getInstance().getMyApi().getMyDirection(String.valueOf(MovingStyleEnum.driving),
                origin.longitude,
                origin.latitude,
                dest.longitude,
                dest.latitude);
        call.enqueue(new Callback<LocationResponse>() {
            @Override
            public void onResponse(@NonNull Call<LocationResponse> call, @NonNull Response<LocationResponse> response) {
                LocationResponse locationResponses = response.body();
                routesItem[0] = locationResponses.getRoutes().get(0);
                drawDirectionLine(origin, dest);
            }

            @Override
            public void onFailure(@NonNull Call<LocationResponse> call, @NonNull Throwable t) {
                Log.i(TAG, String.valueOf(t));
            }
        });
    }

    public void searchLocation(View view) {
        EditText locationSearch = getView().findViewById(R.id.editText);
        String location = locationSearch.getText().toString().trim();
        List<Address> addressList = null;

        if (!TextUtils.isEmpty(location)) {
            Geocoder geocoder = new Geocoder(this.getContext());
            try {
                addressList = geocoder.getFromLocationName(location, 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (addressList != null && !addressList.isEmpty()) {
                Address address = addressList.get(0);
                LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                addDestinationPoint(latLng);
                mGoogleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
            }
            Toast.makeText(this.getContext(), "Try another name please", Toast.LENGTH_LONG).show();
        }
    }

    private void moveToMyLocation() {
        LatLng latLng = new LatLng(currentUser.getLatitude(), currentUser.getLongitude());
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f));

        renderUserMarker(currentUser);
    }

    private void showFriendLocation() {
        mValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Boolean isTracking = snapshot.child("users").child(currentUser.getId()).getValue(User.class).getIsSharing();

                if (isTracking) {
                    friendIdList.forEach((friendId) -> {
                        User friend = snapshot.child("users").child(friendId).getValue(User.class);
                        if (friend != null) {
                            Optional<UserLocation> friendOptional = friendLocations.stream().filter(item -> item.getId().equals(friendId)).findFirst();
                            if (friendOptional.isPresent()) {
                                UserLocation foundFriend = friendOptional.get();

                                foundFriend.setIsSharing(friend.getIsSharing());
                                foundFriend.setLatitude(friend.getLatitude());
                                foundFriend.setLongitude(friend.getLongitude());

                                renderAllMarker();
                            } else {
                                UserLocation friendLocation = new UserLocation(friend);

                                getBitmapDescriptorImg(friend.getProfilePicture(), (BitmapDescriptor bitmapDescriptor) -> {
                                    friendLocation.setProfileImg(bitmapDescriptor);
                                    friendLocations.add(friendLocation);
                                    renderAllMarker();
                                });
                            }
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Getting Location failed, log a message
                Log.w(TAG, "loadLocation:onCancelled", error.toException());
            }
        };
        mDatabase.addValueEventListener(mValueEventListener);
    }

    private void renderAllMarker() {
        // clear marker
        mGoogleMap.clear();

        // user location
        renderUserMarker(currentUser);

        LatLng latLng = new LatLng(currentUser.getLatitude(), currentUser.getLongitude());
        if (mapManagement.getMapMode().equals(MapMode.MOVING)) {
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 25f));
        }

        if (!mapManagement.isGoingToFriend() && mapManagement.getDestination() != null) {
            MarkerOptions options = new MarkerOptions()
                    .position(mapManagement.getDestination())
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));

            mGoogleMap.addMarker(options);
            getDirection(latLng, mapManagement.getDestination());
        }

        // friend location
        if (currentUser.isSharing()) {
            friendLocations.forEach((friend -> {
                if (friend != null && friend.isSharing()) {
                    renderUserMarker(friend);
                    if (mapManagement.isGoingToFriend() && friend.getId().equals(mapManagement.getGoingFriendId())) {
                        getDirection(latLng, new LatLng(friend.getLatitude(), friend.getLongitude()));
                    }
                } else if(!friend.isSharing() && friend.getId().equals(mapManagement.getGoingFriendId())) {
                    LatLng stoppedSharingFriendLocation = new LatLng(friend.getLatitude(), friend.getLongitude());
                    MarkerOptions options = new MarkerOptions()
                            .position(stoppedSharingFriendLocation)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));

                    mGoogleMap.addMarker(options);
                    getDirection(latLng, stoppedSharingFriendLocation);
                }
            }));
        }
    }

    private void renderUserMarker(UserLocation userLocation) {
        MarkerOptions markerOptions = new MarkerOptions()
                .position(new LatLng(userLocation.getLatitude(), userLocation.getLongitude()))
                .title(userLocation.getDisplayName())
                .snippet("Marker Snippet")
                .icon(userLocation.getProfileImg());

        Marker marker = mGoogleMap.addMarker(markerOptions);
        marker.setTag(userLocation.getId());
    }

    private void requestLocationUpdatesListener() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        Log.e("requestLocationUpdatesListener", "change");
    }

    @Override
    public void onLocationChanged(Location location) {
        if (currentUser != null) {
            if (location.getLongitude() != currentUser.getLongitude() || location.getLatitude() != currentUser.getLatitude()) {
                currentUser.setLatitude(location.getLatitude());
                currentUser.setLongitude(location.getLongitude());
                renderAllMarker();
            }
        }
        if(isTurnOnMovementHistory) {
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
            movementHistoryPolylineOptions.add(latLng);
            mGoogleMap.addPolyline(movementHistoryPolylineOptions);
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.i(TAG, "Provider " + provider + " has now status: " + status);
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.i(TAG, "Provider " + provider + " is enabled");
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.i(TAG, "Provider " + provider + " is disabled");
    }

    private void checkPermission(String permission, int requestCode) {
        // Checking if permission is not granted
        if (ContextCompat.checkSelfPermission(getContext(), permission) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{permission}, requestCode);
            Toast.makeText(this.getContext(), "Permission not granted", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this.getContext(), "Permission already granted", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode,
                permissions,
                grantResults);

        if (requestCode == ACCESS_FINE_LOCATION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this.getContext(), "Activity Permission Granted", Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(this.getContext(), "Activity Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void startTrackerService() {
        // Create an Intent for the service you want to start
        Intent intent = new Intent(getContext(), TrackingService.class);

        // Start the service
        getContext().startService(intent);

        //Notify the user that tracking has been enabled
        currentUser.setIsSharing(true);
        mDatabase.child("users").child(currentUser.getId()).child("isSharing").setValue(true);
        Toast.makeText(getContext(), "Start sharing", Toast.LENGTH_SHORT).show();

        // Todo: Schedule a task to stop the service after 15 minutes
    }

    private void stopTrackerService() {
        Intent intent = new Intent(getContext(), TrackingService.class);
        getContext().stopService(intent);

        if (mValueEventListener != null) {
            mDatabase.removeEventListener(mValueEventListener);
        }

        currentUser.setIsSharing(false);
        mDatabase.child("users").child(currentUser.getId()).child("isSharing").setValue(false);
        Toast.makeText(getContext(), "Stop sharing", Toast.LENGTH_SHORT).show();
    }


    private void getUserInfo() {
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
//        Demo code
        friendIdList.clear();
        if (currentUserId.equals("aUGYHuCMN9SE61lS4Q9KeK4DgB62")) {
            friendIdList.add("O21R3tAHqjXH7uKEgUMlteCH8r03");
        } else if (currentUserId.equals("O21R3tAHqjXH7uKEgUMlteCH8r03")) {
            friendIdList.add("aUGYHuCMN9SE61lS4Q9KeK4DgB62");
        }
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User response = snapshot.child("users").child(currentUserId).getValue(User.class);
                if (response != null && currentUser == null) {
                    currentUser = new UserLocation(response);
                    getBitmapDescriptorImg(response.getProfilePicture(), (BitmapDescriptor bitmapDescriptor) -> {
                        currentUser.setProfileImg(bitmapDescriptor);
                        moveToMyLocation();
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private interface OnBitmapDescriptorLoadedListener {
        void onBitmapDescriptorLoaded(BitmapDescriptor bitmapDescriptor);
    }

    private Bitmap getCircularBitmap(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int diameter = 150;

        // Calculate scaling factor to fit the image within the 120px diameter
        float scale = (float) diameter / Math.min(width, height);

        // Scale the bitmap to fit the 120px diameter
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, Math.round(width * scale), Math.round(height * scale), true);

        Bitmap output = Bitmap.createBitmap(diameter, diameter, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        Paint paint = new Paint();

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);

        // Set border color and width
        int borderColor = Color.GRAY;
        int borderWidth = 5;

        // Draw the circle
        paint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(diameter / 2f, diameter / 2f, diameter / 2f, paint);

        // Draw the border
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(borderWidth);
        paint.setColor(borderColor);
        canvas.drawCircle(diameter / 2f, diameter / 2f, diameter / 2f - borderWidth / 2f, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(scaledBitmap, (diameter - scaledBitmap.getWidth()) / 2f, (diameter - scaledBitmap.getHeight()) / 2f, paint);

        return output;
    }

    private void getBitmapDescriptorImg(String imgUrl, OnBitmapDescriptorLoadedListener listener) {
        if (isAdded()) {
            Glide.with(this)
                    .asBitmap()
                    .load(imgUrl)
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                            Bitmap circularBitmap = getCircularBitmap(resource);
                            BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(circularBitmap);
                            listener.onBitmapDescriptorLoaded(bitmapDescriptor);
                        }
                    });
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mGoogleMap = googleMap;

        // check permission
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mGoogleMap.setMyLocationEnabled(true);

        mGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);

        mGoogleMap.setOnMyLocationButtonClickListener(
                new GoogleMap.OnMyLocationButtonClickListener() {
                    @Override
                    public boolean onMyLocationButtonClick() {
                        moveToMyLocation();
                        return true;
                    }
                }
        );

        mGoogleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if (mapManagement.getMapMode().equals(MapMode.SHOW)) {
                    mapManagement.setGoingToFriend(false);
                    addDestinationPoint(latLng);
                }
            }
        });

        mSearchButton = getView().findViewById(R.id.search_bt);
        mSearchButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                searchLocation(v);
            }
        });

        getUserInfo();

        Location locationGPS = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (currentUser != null && locationGPS != null) {
            currentUser.setLatitude(locationGPS.getLatitude());
            currentUser.setLongitude(locationGPS.getLongitude());
        }

        mGoogleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if (marker.getPosition().latitude != currentUser.getLatitude() || marker.getPosition().longitude != currentUser.getLongitude()) {
                    mapManagement.setGoingToFriend(true);
                    mapManagement.setGoingFriendId((String) marker.getTag());
                    renderAllMarker();
                }

                return true;
            }
        });
    }

    private String formatDistance(double distanceInMeters) {
        if (distanceInMeters < 1000) {
            // Round the distance to the nearest 10 meters.
            int roundedDistance = (int) Math.round(distanceInMeters / 10.0) * 10;
            return roundedDistance + " m";
        } else if (distanceInMeters < 10000) {
            // Round the distance to one decimal place.
            double roundedDistance = Math.round(distanceInMeters / 100.0) / 10.0;
            return roundedDistance + " km";
        } else {
            // Round the distance to the nearest kilometer.
            int roundedDistance = (int) Math.round(distanceInMeters / 1000.0);
            return roundedDistance + " km";
        }
    }

    private String formatDuration(double seconds) {
        int hours = (int) (seconds / 3600);
        int minutes = (int) ((seconds % 3600) / 60);

        String timeString;

        if (hours > 0) {
            timeString = String.format("%d giờ %02d phút", hours, minutes);
        } else {
            timeString = String.format("%d phút", minutes);
        }

        return timeString;
    }
}

