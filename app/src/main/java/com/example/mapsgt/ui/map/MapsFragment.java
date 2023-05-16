package com.example.mapsgt.ui.map;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
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
import com.example.mapsgt.data.dao.DAOCallback;
import com.example.mapsgt.data.dao.FavouritePlaceDAO;
import com.example.mapsgt.data.dao.FriendRelationshipDAO;
import com.example.mapsgt.data.dao.HistoryPlaceDAO;
import com.example.mapsgt.data.dao.UserDAO;
import com.example.mapsgt.data.dto.UserLocation;
import com.example.mapsgt.data.entities.Friend;
import com.example.mapsgt.data.entities.HistoryPlace;
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
import com.google.maps.android.ui.IconGenerator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

enum FavouritePlaceEnum {
    Remove,
    Add
}

public class MapsFragment extends Fragment implements
        LocationListener, OnMapReadyCallback {
    private static final String TAG = "LocationFragment";
    private static final int ACCESS_FINE_LOCATION_CODE = 100;
    final RoutesItem[] routesItem = {new RoutesItem()};
    private Switch sharingSw;
    private Switch movementHistorySw;
    private boolean isTurnOnMovementHistory = false;
    private PolylineOptions movementHistoryPolylineOptions;
    private TextView distanceTv;
    private TextView durationTv;
    private TextView placeNameView;
    private TextView placeAddressView;
    private ViewGroup bottomPanel;
    private ViewGroup placeDetailScrollView;
    private Button startMovingBtn;
    private Button stopMovingBtn;
    private Button addFaPlaceBtn;
    private ImageButton showFaPlaceBtn;
    private LocationManager mLocationManager;
    private GoogleMap mGoogleMap;
    private Button mSearchButton;
    private UserLocation currentUser;
    private Marker desMarker = null;
    private List<FavouritePlace> favouritePlaces = new ArrayList<FavouritePlace>();
    private FavouritePlaceEnum favouritePlaceEnum = FavouritePlaceEnum.Add;
    private List<Friend> friendList = new ArrayList<>();
    private List<UserLocation> friendLocations = new ArrayList<>();
    private MapManagement mapManagement;
    private UserDAO userDAO;
    private FriendRelationshipDAO friendRelationshipDAO;
    private FavouritePlaceDAO favouritePlaceDAO;
    private HistoryPlaceDAO historyPlaceDAO;
    private String currentUserId;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mapManagement = new MapManagement();
        movementHistoryPolylineOptions = new PolylineOptions()
                .color(Color.LTGRAY)
                .width(8f);
        userDAO = new UserDAO();
        friendRelationshipDAO = new FriendRelationshipDAO();
        favouritePlaceDAO = new FavouritePlaceDAO();
        historyPlaceDAO = new HistoryPlaceDAO();

        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

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

        distanceTv = view.findViewById(R.id.tv_distance);
        durationTv = view.findViewById(R.id.tv_duration);
        sharingSw = view.findViewById(R.id.sw_sharing);
        movementHistorySw = view.findViewById((R.id.sw_movementHistory));
        startMovingBtn = view.findViewById(R.id.btn_start_moving);
        stopMovingBtn = view.findViewById(R.id.btn_stop_moving);
        addFaPlaceBtn = view.findViewById(R.id.btn_add_faPlace);
        showFaPlaceBtn = view.findViewById(R.id.btn_show_faPlace);
        placeNameView = view.findViewById(R.id.place_name);
        placeAddressView = view.findViewById(R.id.place_address);

        bottomPanel = view.findViewById(R.id.bottom_panel);
        placeDetailScrollView = view.findViewById(R.id.scrollView);
        turnOffPlaceDetailView();

        startMovingBtn.setOnClickListener(view1 -> {
            mapManagement.setMapMode(MapMode.MOVING);
            startMovingBtn.setVisibility(View.GONE);
            stopMovingBtn.setVisibility(View.VISIBLE);
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(currentUser.getLatitude(), currentUser.getLongitude()), 25f));
        });

        stopMovingBtn.setOnClickListener(view13 -> {
            mapManagement.setMapMode(MapMode.SHOW);
            mapManagement.setDestination(null);
            stopMovingBtn.setVisibility(View.GONE);
            startMovingBtn.setVisibility(View.GONE);
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(currentUser.getLatitude(), currentUser.getLongitude()), 15f));
            renderAllMarker();
            durationTv.setVisibility(View.GONE);
            distanceTv.setVisibility(View.GONE);
        });
        showFaPlaceBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showFavouriteLocation(v);
            }
        });
        addFaPlaceBtn.setOnClickListener(viewAddFaPlace -> {
            if (favouritePlaceEnum == FavouritePlaceEnum.Add) {
                final EditText input = new EditText(getContext());
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Địa điểm yêu thích");
                builder.setMessage("Đặt tên cho địa điểm của bạn:");
                builder.setView(input);
                builder.setPositiveButton("Thêm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String namePlace = input.getText().toString();
                        FavouritePlace favoritePlace = new FavouritePlace(currentUserId, desMarker.getPosition().latitude, desMarker.getPosition().longitude, namePlace);
                        favouritePlaceDAO.insert(favoritePlace);
                        renderFavouriteLocation();
                        renderAllMarker();
                        LatLng latLng = desMarker.getPosition();
                        turnOnPlaceDetailView(latLng);
                        getDirection(new LatLng(currentUser.getLatitude(), currentUser.getLongitude()), latLng);
                        addFaPlaceBtn.setText("Xoá địa điểm yêu ");
                        favouritePlaceEnum = FavouritePlaceEnum.Remove;
                    }
                });
                builder.setNegativeButton("Huỷ", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            } else if (favouritePlaceEnum == FavouritePlaceEnum.Remove) {
                favouritePlaceDAO.getFavoritePlaceList(currentUserId).observe(getViewLifecycleOwner(), favoritePlaces -> {
                    favoritePlaces.forEach(favoritePlace -> {
                        String favouritePlaceId = favoritePlace.getId();
                        if (favoritePlace.getLatitude() == desMarker.getPosition().latitude && favoritePlace.getLongitude() == desMarker.getPosition().longitude) {
                            favouritePlaceDAO.delete(favouritePlaceId, new DAOCallback<FavouritePlace>() {
                                @Override
                                public void onSuccess(FavouritePlace object) {
                                    renderFavouriteLocation();
                                    renderAllMarker();
                                }

                                @Override
                                public void onError(String error) {
                                    Log.e(TAG, "Error removing favourite place " + error);
                                }
                            });
                        }
                    });
                });
            }
        });

        sharingSw.setOnClickListener(view12 -> {
            if (sharingSw.isChecked()) {
                startTrackerService();
                showFriendLocation();
            } else {
                mapManagement.setGoingFriendId(null);
                stopTrackerService();
                renderAllMarker();
            }
        });
        movementHistorySw.setOnClickListener(view12 -> {
            if (movementHistorySw.isChecked()) {
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

    private void addNewHistoryPlaceToFirebase(Address placeAddress) {
        HistoryPlace historyPlace = new HistoryPlace(currentUserId, placeAddress.getLatitude(), placeAddress.getLongitude(), placeAddress.getFeatureName(), placeAddress.getAddressLine(0));
        historyPlaceDAO.insert(historyPlace);
        checkingReplaceNewHistoryPlace();
    }

    private void checkingReplaceNewHistoryPlace() {
        historyPlaceDAO.getHistoryPlaceList(currentUserId).observe(this, historyPlaceListRes -> {
            if (historyPlaceListRes.size() > 10) {
                historyPlaceDAO.delete(historyPlaceListRes.get(0).getId());
            }
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);

        checkPermission(Manifest.permission.ACCESS_FINE_LOCATION, ACCESS_FINE_LOCATION_CODE);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                // Tắt la bàn (compass)
                googleMap.getUiSettings().setCompassEnabled(false);
            }
        });
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
        desMarker = mGoogleMap.addMarker(options);

        FavouritePlaceEnum faPlaceEnum = getStatusOfFavouritePlace(latLng);
        setStatusForFavouritePlaceButton(faPlaceEnum);
        turnOnPlaceDetailView(latLng);
        getDirection(new LatLng(currentUser.getLatitude(), currentUser.getLongitude()), latLng);
    }

    private FavouritePlaceEnum getStatusOfFavouritePlace(LatLng latLng) {
        for (FavouritePlace place : favouritePlaces) {
            if (place.getLatitude() == latLng.latitude && place.getLongitude() == latLng.longitude) {
                return FavouritePlaceEnum.Remove;
            }
        }
        return FavouritePlaceEnum.Add;
    }

    private void setStatusForFavouritePlaceButton(FavouritePlaceEnum faPlaceEnum) {
        if (faPlaceEnum == FavouritePlaceEnum.Remove) {
            favouritePlaceEnum = FavouritePlaceEnum.Remove;
            addFaPlaceBtn.setText("Xóa địa điểm yêu thích");
        } else {
            favouritePlaceEnum = FavouritePlaceEnum.Add;
            addFaPlaceBtn.setText("Thêm địa điểm yêu thích");
        }
    }

    private void turnOnPlaceDetailView(LatLng latLng) {
        detailPlace(latLng);
        placeDetailScrollView.setVisibility(View.VISIBLE);
    }

    private void turnOffPlaceDetailView() {
        placeDetailScrollView.setVisibility(View.GONE);
    }

    private void detailPlace(LatLng latLng) {
        Geocoder geocoder = new Geocoder(getContext());
        try {
            List<Address> addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            if (addresses.size() > 0) {
                Address address = addresses.get(0);
                placeNameView.setText(address.getFeatureName());
                placeAddressView.setText(address.getAddressLine(0));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
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
        durationTv.setVisibility(View.VISIBLE);
        distanceTv.setVisibility(View.VISIBLE);
        durationTv.setText("Thời gian: " + formatDuration(route.getDuration()));
        distanceTv.setText("Khoảng cách: " + formatDistance(route.getDistance()));
        if (mapManagement.getMapMode().equals(MapMode.SHOW)) {
            startMovingBtn.setVisibility(View.VISIBLE);
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

    public void renderFavouriteLocation() {
        favouritePlaces.clear();
        favouritePlaceDAO.getFavoritePlaceList(currentUserId).observe(this, favouritePlacesRes -> {
            if (favouritePlacesRes != null) {
                favouritePlaces.addAll(favouritePlacesRes);
                renderFavouritePlaceMarker();
            }
        });
    }

    public void showFavouriteLocation(View view) {
        favouritePlaces.clear();
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Địa điểm yêu thích");
        ListView listView = new ListView(getContext());
        builder.setView(listView);

        favouritePlaceDAO.getFavoritePlaceList(currentUserId).observe(this, favoritePlacesRes -> {
            if (favoritePlacesRes != null) {
                favouritePlaces.addAll(favoritePlacesRes);
                FavouritePlaceAdapter adapter = new FavouritePlaceAdapter(getContext(), favouritePlaces);
                listView.setAdapter(adapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        FavouritePlace place = favouritePlaces.get(position);
                        LatLng latLng = new LatLng(place.getLatitude(), place.getLongitude());
                        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f));
                        addDestinationPoint(latLng);
                        builder.create().dismiss();
                    }
                });
            }

        });

        builder.create().show();
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
                addNewHistoryPlaceToFirebase(address);
                LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                addDestinationPoint(latLng);
                mGoogleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                turnOnPlaceDetailView(latLng);
            }
            Toast.makeText(this.getContext(), "Try another name please", Toast.LENGTH_LONG).show();
        }
    }

    private void moveToMyLocation() {
        LatLng latLng = new LatLng(currentUser.getLatitude(), currentUser.getLongitude());
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f));

        renderUserMarker(currentUser);
        renderFavouriteLocation();
    }

    private void showFriendLocation() {
        Boolean isTracking = currentUser.isSharing();
        if (isTracking) {
            friendList.forEach((friendItem) -> {
                userDAO.getByKey(friendItem.getId()).observe(getViewLifecycleOwner(), friend -> {
                    Optional<UserLocation> friendOptional = friendLocations.stream().filter(item -> item.getId().equals(friend.getId())).findFirst();
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
                });
            });
        }
    }

    private void renderAllMarker() {
        // clear marker
        mGoogleMap.clear();
        turnOffPlaceDetailView();
        // user location
        renderUserMarker(currentUser);
        // favourite place
        renderFavouritePlaceMarker();

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
                } else if (!friend.isSharing() && friend.getId().equals(mapManagement.getGoingFriendId())) {
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

    private void removeFavouritePlace(LatLng latLng) {
        for (FavouritePlace place : favouritePlaces) {
            if (place.getLongitude() == latLng.longitude && place.getLatitude() == latLng.latitude) {
                favouritePlaces.remove(place);
            }
        }
    }

    private void renderFavouritePlaceMarker() {
        for (FavouritePlace place : favouritePlaces) {
            LatLng latLng = new LatLng(place.getLatitude(), place.getLongitude());
            TextView title = new TextView(getContext());
            title.setText(place.getName());
            IconGenerator generator = new IconGenerator(getContext());
            generator.setBackground(getContext().getDrawable(R.drawable.favourite_place));
            generator.setContentView(title);
            Bitmap icon = generator.makeIcon();
            int width = 100;
            int height = 100;
            Bitmap resizedBitmap = Bitmap.createScaledBitmap(icon, width, height, false);
            MarkerOptions tp = new MarkerOptions()
                    .position(latLng)
                    .snippet("favourite-place")
                    .icon(BitmapDescriptorFactory.fromBitmap(resizedBitmap));
            mGoogleMap.addMarker(tp);
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
        if (currentUser != null) {
            if (location.getLongitude() != currentUser.getLongitude() || location.getLatitude() != currentUser.getLatitude()) {
                currentUser.setLatitude(location.getLatitude());
                currentUser.setLongitude(location.getLongitude());
                renderAllMarker();
            }
        }
        if (isTurnOnMovementHistory) {
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
                moveToMyLocation();
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
        userDAO.setIsSharing(currentUserId, true);
        Toast.makeText(getContext(), "Start sharing", Toast.LENGTH_SHORT).show();

    }

    private void stopTrackerService() {
        Intent intent = new Intent(getContext(), TrackingService.class);
        getContext().stopService(intent);

        userDAO.setIsSharing(currentUserId, false);
        Toast.makeText(getContext(), "Stop sharing", Toast.LENGTH_SHORT).show();
    }


    private void getUserInfo() {
        AtomicBoolean loadFirstTime = new AtomicBoolean(true);
        friendRelationshipDAO.getFriendList(currentUserId).observe(this, friendListRes -> {
            friendList.addAll(friendListRes);
        });

        userDAO.getByKey(currentUserId).observe(this, userRes -> {
            currentUser = new UserLocation(userRes);
            getBitmapDescriptorImg(userRes.getProfilePicture(), (BitmapDescriptor bitmapDescriptor) -> {
                currentUser.setProfileImg(bitmapDescriptor);
                if (loadFirstTime.get()) {
                    moveToMyLocation();
                    goToHistoryPlace();
                    loadFirstTime.set(false);
                }
            });
            sharingSw.setChecked(currentUser.isSharing());
        });
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
        checkPermission(Manifest.permission.ACCESS_FINE_LOCATION, ACCESS_FINE_LOCATION_CODE);

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
                        try {
                            moveToMyLocation();
                        } catch (Exception e) {
                            Log.d(TAG, "onMyLocationButtonClick: " + e);
                        }
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

                if (marker.getSnippet() != null) {
                    if ("favourite-place".equals(marker.getSnippet())) {
                        addDestinationPoint(marker.getPosition());
                    } else if (marker.getPosition().latitude != currentUser.getLatitude() || marker.getPosition().longitude != currentUser.getLongitude()) {
                        mapManagement.setGoingToFriend(true);
                        mapManagement.setGoingFriendId((String) marker.getTag());
                        renderAllMarker();
                    }
                }
                return true;
            }
        });
    }

    private void goToHistoryPlace() {
        if (getArguments() != null) {
            Bundle args = getArguments();
            if (args != null && args.containsKey("historyPlace")) {
                HistoryPlace historyPlace = args.getParcelable("historyPlace");
                LatLng latLng = new LatLng(historyPlace.getLatitude(), historyPlace.getLongitude());
                addDestinationPoint(latLng);
                mGoogleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                turnOnPlaceDetailView(latLng);
            }
        }
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

    private interface OnBitmapDescriptorLoadedListener {
        void onBitmapDescriptorLoaded(BitmapDescriptor bitmapDescriptor);
    }
}