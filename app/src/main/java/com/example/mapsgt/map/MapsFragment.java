package com.example.mapsgt.map;

import android.Manifest;
import android.content.Context;
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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.mapsgt.R;
import com.example.mapsgt.enumeration.MovingStyleEnum;
import com.example.mapsgt.network.RetrofitClient;
import com.example.mapsgt.network.model.location.LocationResponse;
import com.example.mapsgt.network.model.location.RoutesItem;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

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
    final RoutesItem[] routesItem = {new RoutesItem()};
    private final float DEFAULT_ZOOM = 15f;
    private final ArrayList<LatLng> markerPoints = new ArrayList<LatLng>();
    protected LocationManager mLocationManager;
    protected LatLng mGPSLocation;
    protected GoogleMap mGoogleMap;
    protected Button mSearchButton;
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
                    AddDestinationPoint(latLng);
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

    void AddDestinationPoint(LatLng latLng) {
        if (markerPoints.size() > 1) {
            ClearMarkers();
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

    void DrawDirectionLine(LatLng origin, LatLng dest) {
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
        duration.setText("Duration: " + routesItem[0].getDuration()+" p");
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
                DrawDirectionLine(origin, dest);

            }
            @Override
            public void onFailure(@NonNull Call<LocationResponse> call, @NonNull Throwable t) {
                Log.i(TAG, String.valueOf(t));
            }
        });
    }

    public void searchLocation(View view) {
        EditText locationSearch = getView().findViewById(R.id.editText);
        String location = locationSearch.getText().toString();
        List<Address> addressList = null;

        if (location != null || !location.equals("")) {
            Geocoder geocoder = new Geocoder(this.getContext());
            try {
                addressList = geocoder.getFromLocationName(location, 1);

            } catch (IOException e) {
                e.printStackTrace();
            }
            if (addressList != null && !addressList.isEmpty()) {
                Address address = addressList.get(0);
                LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                AddDestinationPoint(latLng);
                mGoogleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
            }
            Toast.makeText(this.getContext(), "Try another name please", Toast.LENGTH_LONG).show();
        }
    }

    void ClearMarkers() {
        markerPoints.clear();
        mGoogleMap.clear();
    }

    void handleMyLocation(LatLng latLng) {
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f));
        MarkerOptions options = new MarkerOptions();
        ClearMarkers();
        markerPoints.add(latLng);

        // Replace this when using real data to get user avatar, name, status
//        MyDatabase.getInstance(this.getContext()).userDAO().getUserByUsername();

        options.position(latLng);
        options.icon(
                BitmapFromVector(this.getContext(), R.drawable.imagesuser)
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

    private BitmapDescriptor BitmapFromVector(Context context, int vectorResId) {

        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);

        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        bitmap = getResizedBitmap(bitmap, 200, 200);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        vectorDrawable.draw(canvas);

        return BitmapDescriptorFactory.fromBitmap(bitmap);
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
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
        checkPermission(Manifest.permission.ACCESS_FINE_LOCATION, ACCESS_FINE_LOCATION_CODE);

        mLocationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        Location locationGPS = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (locationGPS != null)
            mGPSLocation = new LatLng(locationGPS.getLatitude(), locationGPS.getLongitude());
    }


    void requestLocationUpdatesListener() {
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
            Toast.makeText(this.getContext(), "Permission  not granted", Toast.LENGTH_SHORT).show();
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
}