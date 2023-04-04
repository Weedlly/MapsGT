package com.example.mapsgt.ui.map;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
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

import com.example.mapsgt.R;
import com.example.mapsgt.data.dto.UserLocationDto;
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
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapsFragment extends Fragment implements
        LocationListener {

    private static final String TAG = "LocationFragment";
    private static final int ACCESS_FINE_LOCATION_CODE = 100;
    private static final int PERMISSIONS_REQUEST = 100;
    final RoutesItem[] routesItem = {new RoutesItem()};
    private Switch sw_sharing;
    private final ArrayList<LatLng> markerPoints = new ArrayList<LatLng>();
    protected LocationManager mLocationManager;
    protected LatLng mGPSLocation;
    protected GoogleMap mGoogleMap;
    protected Button mSearchButton;
    private DatabaseReference mDatabase;

    ValueEventListener mValueEventListener;

    private final OnMapReadyCallback callback = new OnMapReadyCallback() {
        @Override
        public void onMapReady(GoogleMap googleMap) {
            mGoogleMap = googleMap;

            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mGoogleMap.setMyLocationEnabled(true);

            mGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);

            mGoogleMap.setOnMyLocationButtonClickListener(
                    new GoogleMap.OnMyLocationButtonClickListener() {
                        @Override
                        public boolean onMyLocationButtonClick() {
                            handleMyLocation(mGPSLocation);
                            return true;
                        }
                    }
            );

            mGoogleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {
                    addDestinationPoint(latLng);
                }
            });
            if (mGPSLocation != null) {
                handleMyLocation(mGPSLocation);
            }
            mSearchButton = getView().findViewById(R.id.search_bt);
            mSearchButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    searchLocation(v);
                }
            });
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        showFriendLocation();

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

        sw_sharing = view.findViewById(R.id.sw_sharing);

        sw_sharing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(sw_sharing.isChecked()) {
                    startSharingService();
                } else {
                    stopTrackerService();
                }
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);

        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }

        checkPermission(Manifest.permission.ACCESS_FINE_LOCATION, ACCESS_FINE_LOCATION_CODE);

        Location locationGPS = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (locationGPS != null)
            mGPSLocation = new LatLng(locationGPS.getLatitude(), locationGPS.getLongitude());
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
        if (markerPoints.size() > 1) {
            clearMarkers();
        }
        // Adding new item to the ArrayList
        markerPoints.add(latLng);

        // Creating MarkerOptions
        MarkerOptions options = new MarkerOptions();

        // Setting the position of the marker
        options.position(latLng);

        if (markerPoints.size() == 1) {
            options.icon(
                    BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)
            );
        } else if (markerPoints.size() == 2) {
            options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        }

        // Add new marker to the Google Map Android API V2
        mGoogleMap.addMarker(options);

        // Checks, whether start and end locations are captured
        if (markerPoints.size() >= 2) {
            LatLng origin = markerPoints.get(0);
            LatLng dest = markerPoints.get(1);

            getDirection(origin, dest);
        }
    }

    private void drawDirectionLine(LatLng origin, LatLng dest) {
        List<List<Double>> coordinates = routesItem[0].getGeometry().getCoordinates();
        LatLng nextStep = origin;
        PolylineOptions polylineOptions = new PolylineOptions();
        for (List<Double> step : coordinates) {
            LatLng lng = new LatLng(step.get(1), step.get(0));
            polylineOptions.add(nextStep, lng);
            nextStep = lng;
        }
        polylineOptions.add(nextStep, dest);
        TextView duration = getView().findViewById(R.id.duration_tv);
        TextView distance = getView().findViewById(R.id.distance_tv);
        duration.setText("Duration: " + routesItem[0].getDuration() + " p");
        distance.setText("Distance: " + routesItem[0].getDistance() + " m");
        mGoogleMap.addPolyline(polylineOptions);
    }

    private void getDirection(LatLng origin, LatLng dest) {
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

    private void clearMarkers() {
        markerPoints.clear();
        mGoogleMap.clear();
    }

    private void handleMyLocation(LatLng latLng) {
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f));
        MarkerOptions options = new MarkerOptions();
        clearMarkers();
        markerPoints.add(latLng);

        // Todo: replace this when using real data to get user avatar, name, status

        options.position(latLng);
        options.icon(
                bitmapFromVector(this.getContext(), R.drawable.imagesuser)
        );

        // Add new marker to the Google Map Android API V2
        mGoogleMap.addMarker(options.title("Wendy"));
    }

    public Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, false);
        bm.recycle();
        return resizedBitmap;
    }

    private BitmapDescriptor bitmapFromVector(Context context, int vectorResId) {

        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);

        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        bitmap = getResizedBitmap(bitmap, 200, 200);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        vectorDrawable.draw(canvas);

        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    private void showFriendLocation() {
        mValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // todo: show list of friend location on map
                UserLocationDto location = snapshot.child("locations").child("users").child("xczs09n7kJg9HfLvQeKRySS2N0z1").getValue(UserLocationDto.class);
                if(location == null) {
                    location = new UserLocationDto();
                    location.setLatitude(mGPSLocation.latitude);
                    location.setLongitude(mGPSLocation.longitude);
                }
                Log.d("Location", String.valueOf(location.getLatitude()) + " " + String.valueOf(location.getLongitude()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Getting Location failed, log a message
                Log.w(TAG, "loadLocation:onCancelled", error.toException());
            }
        };
        mDatabase.addValueEventListener(mValueEventListener);
    }

    private void startSharingService() {
        //Check whether this app has access to the location permission
        int permission = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION);

        //If the location permission has been granted, then start the TrackerService
        if (permission == PackageManager.PERMISSION_GRANTED) {
            startTrackerService();
        } else {
            //If the app doesn’t currently have access to the user’s location, then request access
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST);
        }
    }

    private void requestLocationUpdatesListener() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
    }

    @Override
    public void onLocationChanged(Location location) {
        mGPSLocation = new LatLng(location.getLatitude(), location.getLongitude());
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

    public void checkPermission(String permission, int requestCode) {
        // Checking if permission is not granted
        if (ContextCompat.checkSelfPermission(this.getContext(), permission) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this.getActivity(), new String[]{permission}, requestCode);
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
                System.exit(0);
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
        Toast.makeText(getContext(), "GPS tracking enabled", Toast.LENGTH_SHORT).show();

        // Todo: Schedule a task to stop the service after 15 minutes
    }

    private void stopTrackerService() {
        Intent intent = new Intent(getContext(), TrackingService.class);
        getContext().stopService(intent);
    }
}

