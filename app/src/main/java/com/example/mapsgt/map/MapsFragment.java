package com.example.mapsgt.map;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;

import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.example.mapsgt.R;
//import com.google.android.gms.location.LocationListener;
import com.example.mapsgt.enumeration.MovingStyle;
import com.example.mapsgt.network.RetrofitClient;
import com.example.mapsgt.network.model.location.LocationResponse;
import com.example.mapsgt.network.model.location.RoutesItem;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;


import android.location.LocationListener;
import android.location.LocationManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapsFragment extends Fragment implements LocationListener {

    private static final String TAG = "LocationFragment";
    private final float DEFAULT_ZOOM = 15f;
    private final String BASE_URL = "https://api.mapbox.com";
    private ArrayList<LatLng> markerPoints = new ArrayList<LatLng>();
    final RoutesItem[] routesItem = {new RoutesItem()};

    protected LocationManager mLocationManager;
    protected LocationListener mLocationListener;
    protected LatLng mGPSLocation;
    protected GoogleMap mGoogleMap;

    private OnMapReadyCallback callback = new OnMapReadyCallback() {
        @Override
        public void onMapReady(GoogleMap googleMap) {
            mGoogleMap = googleMap;
//            mGoogleMap.setMapType(4);

            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mGoogleMap.setMyLocationEnabled(true);

            mGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);

            mGoogleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {

                    if (markerPoints.size() > 1) {
                        markerPoints.clear();
                        mGoogleMap.clear();
                    }

                    // Adding new item to the ArrayList
                    markerPoints.add(latLng);

                    // Creating MarkerOptions
                    MarkerOptions options = new MarkerOptions();

                    // Setting the position of the marker
                    options.position(latLng);

                    if (markerPoints.size() == 1) {
                        options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                    } else if (markerPoints.size() == 2) {
                        options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                    }

                    // Add new marker to the Google Map Android API V2
                    mGoogleMap.addMarker(options);

                    // Checks, whether start and end locations are captured
                    if (markerPoints.size() >= 2) {
                        LatLng origin = (LatLng) markerPoints.get(0);
                        LatLng dest = (LatLng) markerPoints.get(1);

                        Log.v(TAG,"Direction API Url : https://api.mapbox.com/directions/v5/mapbox/walking/" +
                                origin.longitude + "%2C" +
                                origin.latitude + "%3B" +
                                dest.longitude + "%2C" +
                                dest.latitude +
                                "?alternatives=false&geometries=geojson&overview=simplified&steps=false&access_token=pk.eyJ1Ijoid2VlZGx5IiwiYSI6ImNsN2VpMW56bjAwa2gzbnBnaHd2MjJmZGYifQ.It2pYYoWNWQ-9Ogs49OUMg"
                        );

                        getDirection(origin,dest);


                    }

                }
            });

        }
    };
    void DrawDirectionLine(LatLng origin , LatLng dest){
        Log.i(TAG,String.valueOf(routesItem[0].getGeometry()));
        List<List<Double>> coordinates = routesItem[0].getGeometry().getCoordinates();

        LatLng nextStep = origin;
        PolylineOptions polylineOptions = new PolylineOptions();
        for (List<Double> step : coordinates){

            LatLng lng = new LatLng(step.get(1),step.get(0));
            Log.i(TAG,String.valueOf(lng));
            polylineOptions.add(nextStep,lng);
            nextStep = lng;
        }

        polylineOptions.add(nextStep,dest);
        mGoogleMap.addPolyline(polylineOptions);
    }
    private void getDirection(LatLng origin , LatLng dest) {
        Call<LocationResponse> call = RetrofitClient.getInstance().getMyApi().getMyDirection(String.valueOf(MovingStyle.walking),
                origin.longitude,
                origin.latitude,
                dest.longitude,
                dest.latitude);
        Log.i(TAG,String.valueOf( call.request().url()));

        call.enqueue(new Callback<LocationResponse>() {
            @Override
            public void onResponse(@NonNull Call<LocationResponse> call, @NonNull Response<LocationResponse> response) {
                LocationResponse locationResponses = response.body();
                routesItem[0] = locationResponses.getRoutes().get(0);
                Log.i(TAG, String.valueOf(locationResponses.getRoutes().get(0)));
                DrawDirectionLine(origin,dest);

            }

            @Override
            public void onFailure(@NonNull Call<LocationResponse> call, @NonNull Throwable t) {
                Log.i(TAG,String.valueOf(t));
            }

        });
    }

    void focusLocation(LatLng latLng){
        if(mGoogleMap != null) {
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mGoogleMap.setMyLocationEnabled(true);
            mGoogleMap.addMarker(new MarkerOptions().position(latLng).title("Current location"));
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM));

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "Add listenner "  );
        requestLocationUpdatesListener();
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i(TAG, "Remove listenner "  );
        mLocationManager.removeUpdates(this);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_maps, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        checkPermission(Manifest.permission.ACTIVITY_RECOGNITION,ACTIVITY_PERMISSION_CODE);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }

        mLocationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
    }


    @SuppressLint("MissingPermission")
    void requestLocationUpdatesListener(){
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
    }


    @Override
    public void onLocationChanged(Location location) {
        mGPSLocation = new LatLng(location.getLatitude(),location.getLongitude());
//        focusLocation(mGPSLocation);
//        Log.i(TAG, String.valueOf(location.getLatitude()));
//        Log.i(TAG, String.valueOf(location.getLongitude()));
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


}