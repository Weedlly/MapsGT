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
import com.example.mapsgt.data.dao.FriendRelationshipDAO;
import com.example.mapsgt.data.dao.UserDAO;
import com.example.mapsgt.data.dto.UserLocation;
import com.example.mapsgt.data.entities.Friend;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.ui.IconGenerator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

enum FavouritePlaceEnum{
    Remove,
    Add
}
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
    private TextView placeNameView;
    private TextView placeAddressView;
    private TextView placePhoneView;
    private TextView placeWebsiteView;
    private ViewGroup bottomPanel;
    private ViewGroup placeDetailScrollView;
    private Button btn_start_moving;
    private Button btn_stop_moving;
    private Button btn_add_faPlace;
    private Button btn_show_faPlace;
    private LocationManager mLocationManager;
    private GoogleMap mGoogleMap;
    private Button mSearchButton;
    private DatabaseReference mDatabase;
    private ValueEventListener mValueEventListener;
    private UserLocation currentUser;
    private final List<String> friendIdList = new ArrayList<>();
    private final List<UserLocation> friendLocations = new ArrayList<>();
    private MapManagement mapManagement;
    private Marker desMarker = null;
    private List<FavouritePlace> favouritePlaces = new ArrayList<FavouritePlace>();
    private String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private FavouritePlaceEnum favouritePlaceEnum = FavouritePlaceEnum.Add;
    private List<Friend> friendList = new ArrayList<>();
    private UserDAO userDAO;
    private FriendRelationshipDAO friendRelationshipDAO;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mapManagement = new MapManagement();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        movementHistoryPolylineOptions = new PolylineOptions()
                .color(Color.LTGRAY)
                .width(8f);
        userDAO = new UserDAO();
        friendRelationshipDAO = new FriendRelationshipDAO();

        //Check whether GPS tracking is enabled
        mLocationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        if (!mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            getActivity().finish();
        }
        getUserInfo();
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
        btn_add_faPlace = view.findViewById(R.id.btn_add_faPlace);
        btn_show_faPlace = view.findViewById(R.id.btn_show_faPlace);
        placeNameView = view.findViewById(R.id.place_name);
        placeAddressView = view.findViewById(R.id.place_address);
        placePhoneView = view.findViewById(R.id.place_phone);
        placeWebsiteView = view.findViewById(R.id.place_website);

        bottomPanel = view.findViewById(R.id.bottom_panel);
        placeDetailScrollView = view.findViewById(R.id.scrollView);
        TurnOffPlaceDetailView();

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
        btn_show_faPlace.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showFavouriteLocation(v);
            }
        });
        btn_add_faPlace.setOnClickListener(viewAddFaPlace -> {
            if(favouritePlaceEnum == FavouritePlaceEnum.Add) {
                final EditText input = new EditText(getContext());
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Favourite place");
                builder.setMessage("Naming your favourite place:");
                builder.setView(input);
                builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String namePlace = input.getText().toString();
                        AddNewFavouritePlaceToFirebase(namePlace);
                        renderFavouriteLocation();
                        renderAllMarker();
                        LatLng latLng = desMarker.getPosition();
                        TurnOnPlaceDetailView(latLng);
                        getDirection(new LatLng(currentUser.getLatitude(), currentUser.getLongitude()), latLng);
                        btn_add_faPlace.setText("Remove favourite place");
                        favouritePlaceEnum = FavouritePlaceEnum.Remove;
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
            else if(favouritePlaceEnum == FavouritePlaceEnum.Remove){
                Query query = FirebaseDatabase.getInstance().getReference("favourite_places")
                        .orderByChild("userId")
                        .equalTo(currentUserId);
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                LatLng latLng = desMarker.getPosition();
                                String favouritePlaceId = dataSnapshot.getKey();
                                FavouritePlace place = dataSnapshot.getValue(FavouritePlace.class);
                                if (place.getLatitude() == latLng.latitude && place.getLongitude() == latLng.longitude){
                                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                                    DatabaseReference favouritePlaceRef = database.getReference("favourite_places").child(favouritePlaceId);
                                    favouritePlaceRef.removeValue()
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Log.d(TAG, "Favourite place removed successfully");
                                                    renderFavouriteLocation();
                                                    renderAllMarker();
                                                    TurnOnPlaceDetailView(latLng);
                                                    SetStatusForFavouritePlaceButton(FavouritePlaceEnum.Add);
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.e(TAG, "Error removing favourite place", e);
                                                }
                                            });
                                }
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e(TAG, "Error removing favourite place");
                    }
                });
            }
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
    void AddNewFavouritePlaceToFirebase(String namePlace){
        FirebaseApp firebaseApp = FirebaseApp.getInstance();
        DatabaseReference databaseRef = FirebaseDatabase.getInstance(firebaseApp).getReference();
        DatabaseReference favoritePlacesRef = databaseRef.child("favourite_places");
        FavouritePlace favoritePlace = new FavouritePlace(currentUserId, desMarker.getPosition().latitude, desMarker.getPosition().longitude, namePlace);
        favoritePlacesRef.push().setValue(favoritePlace);
    }
    void AddNewHistoryPlaceToFirebase(Address placeAddress){
        FirebaseApp firebaseApp = FirebaseApp.getInstance();
        DatabaseReference databaseRef = FirebaseDatabase.getInstance(firebaseApp).getReference();
        DatabaseReference favoritePlacesRef = databaseRef.child("history_places");
        HistoryPlace historyPlace = new HistoryPlace(currentUserId, placeAddress.getLatitude(), placeAddress.getLongitude(), placeAddress.getFeatureName(),placeAddress.getAddressLine(0));
        favoritePlacesRef.push().setValue(historyPlace);
        CheckingReplaceNewHistoryPlace();
    }
    void CheckingReplaceNewHistoryPlace(){
        DatabaseReference historyPlacesRef = FirebaseDatabase.getInstance().getReference("history_places");
        Query query = historyPlacesRef.orderByChild("userId").equalTo(currentUserId);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<HistoryPlace> tmpHistoryPlaces = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    HistoryPlace historyPlace = snapshot.getValue(HistoryPlace.class);
                    tmpHistoryPlaces.add(historyPlace);
                }
                Log.d(TAG, "onDataChange: " + tmpHistoryPlaces.size());
                while (tmpHistoryPlaces.size() > 10) {
                    Log.d(TAG, "Replace: " + tmpHistoryPlaces.get(tmpHistoryPlaces.size() -1 ).getName());
                    tmpHistoryPlaces.remove(0);
                    historyPlacesRef.setValue(tmpHistoryPlaces);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle the error case if the query is canceled or fails
                Log.d(TAG, databaseError.toString());
            }
        });
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);

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

        FavouritePlaceEnum faPlaceEnum = GetStatusOfFavouritePlace(latLng);
        SetStatusForFavouritePlaceButton(faPlaceEnum);
        TurnOnPlaceDetailView(latLng);
        Log.d(TAG, "origin: " + currentUser.getLatitude() +"," +currentUser.getLongitude());
        Log.d(TAG, "des: "+ latLng);
        getDirection(new LatLng(currentUser.getLatitude(), currentUser.getLongitude()), latLng);

    }
    FavouritePlaceEnum GetStatusOfFavouritePlace(LatLng latLng){
        for (FavouritePlace place: favouritePlaces) {
            if(place.getLatitude() == latLng.latitude && place.getLongitude() == latLng.longitude){
                return FavouritePlaceEnum.Remove;
            }
        }
        return FavouritePlaceEnum.Add;
    }
    void SetStatusForFavouritePlaceButton(FavouritePlaceEnum faPlaceEnum){
        if (faPlaceEnum == FavouritePlaceEnum.Remove){
            favouritePlaceEnum = FavouritePlaceEnum.Remove;
            btn_add_faPlace.setText("Remove favourite place");
        }
        else{
            favouritePlaceEnum = FavouritePlaceEnum.Add;
            btn_add_faPlace.setText("Add favourite place");
        }
    }
    void TurnOnPlaceDetailView(LatLng latLng){
        detailPlace(latLng);
        placeDetailScrollView.setVisibility(View.VISIBLE);
    }
    void TurnOffPlaceDetailView(){
        placeDetailScrollView.setVisibility(View.GONE);
    }
    void detailPlace(LatLng latLng){
        Geocoder geocoder = new Geocoder(getContext());
        try {
            List<Address> addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            if (addresses.size() > 0) {
                Address address = addresses.get(0);
                placeNameView.setText(address.getFeatureName());
                placeAddressView.setText(address.getAddressLine(0));
                placePhoneView.setText(address.getPhone());
                placeWebsiteView.setText(address.getUrl());
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
    public void renderFavouriteLocation(){
        favouritePlaces.clear();
        FirebaseDatabase.getInstance().getReference("favourite_places").orderByChild("userId")
                .equalTo(currentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot placeSnapshot : snapshot.getChildren()) {
                            FavouritePlace place = placeSnapshot.getValue(FavouritePlace.class);
                            favouritePlaces.add(place);
                        }
                        renderFavouritePlaceMarker();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e(TAG, "Failed to get favourite places", error.toException());
                    }
                });
    }
    public void showFavouriteLocation(View view){
        favouritePlaces.clear();
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Favourite places");
        ListView listView = new ListView(getContext());
        builder.setView(listView);

        FirebaseDatabase.getInstance().getReference("favourite_places").orderByChild("userId")
                .equalTo(currentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot placeSnapshot : snapshot.getChildren()) {
                            FavouritePlace place = placeSnapshot.getValue(FavouritePlace.class);
                            favouritePlaces.add(place);
                        }
                        FavouritePlaceAdapter adapter = new FavouritePlaceAdapter(getContext(),favouritePlaces);
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

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e(TAG, "Failed to get favourite places", error.toException());
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
                Log.d(TAG, "detailPlace: " + address.getAddressLine(0));
                AddNewHistoryPlaceToFirebase(address);
                LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                addDestinationPoint(latLng);
                mGoogleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                TurnOnPlaceDetailView(latLng);
            }
            Toast.makeText(this.getContext(), "Try another name please", Toast.LENGTH_LONG).show();
        }
    }

    private void moveToMyLocation() {
        Log.d(TAG, "moveToMyLocation: ");
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
        TurnOffPlaceDetailView();
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
    private void removeFavouritePlace(LatLng latLng){
        for (FavouritePlace place:favouritePlaces) {
            if(place.getLongitude() == latLng.longitude &&  place.getLatitude() == latLng.latitude){
                favouritePlaces.remove(place);
            }
        }
    }
    private void renderFavouritePlaceMarker() {
        for (FavouritePlace place: favouritePlaces) {
            LatLng latLng = new LatLng(place.getLatitude(),place.getLongitude());
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

        if (mValueEventListener != null) {
            mDatabase.removeEventListener(mValueEventListener);
        }

        userDAO.setIsSharing(currentUserId, false);
        Toast.makeText(getContext(), "Stop sharing", Toast.LENGTH_SHORT).show();
    }


    private void getUserInfo() {
        AtomicBoolean loadFirstTime = new AtomicBoolean(true);
        friendRelationshipDAO.getFriendList(currentUserId).observe(this, friendListRes -> {
            friendList.addAll(friendListRes);
        });

        userDAO.getByKey(currentUserId).observe(this, user -> {
            currentUser = new UserLocation(user);
            getBitmapDescriptorImg(user.getProfilePicture(), (BitmapDescriptor bitmapDescriptor) -> {
                currentUser.setProfileImg(bitmapDescriptor);
                if (loadFirstTime.get()) {
                    checkPermission(Manifest.permission.ACCESS_FINE_LOCATION, ACCESS_FINE_LOCATION_CODE);
                    Location locationGPS = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if (currentUser != null && locationGPS != null) {
                        currentUser.setLatitude(locationGPS.getLatitude());
                        currentUser.setLongitude(locationGPS.getLongitude());
                    }
                    moveToMyLocation();
                    goToHistoryPlace();
                    loadFirstTime.set(false);
                }
            });
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

        mGoogleMap.setMyLocationEnabled(true);

        mGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);

        mGoogleMap.setOnMyLocationButtonClickListener(
                new GoogleMap.OnMyLocationButtonClickListener() {
                    @Override
                    public boolean onMyLocationButtonClick() {
                        try {
                            moveToMyLocation();
                        } catch (Exception e){
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

        Location locationGPS = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (currentUser != null && locationGPS != null) {
            currentUser.setLatitude(locationGPS.getLatitude());
            currentUser.setLongitude(locationGPS.getLongitude());
        }

        mGoogleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                if (marker.getSnippet() != null){
                    if ("favourite-place".equals(marker.getSnippet())) {
                        addDestinationPoint(marker.getPosition());
                    }
                    else if (marker.getPosition().latitude != currentUser.getLatitude() || marker.getPosition().longitude != currentUser.getLongitude()) {
                        mapManagement.setGoingToFriend(true);
                        mapManagement.setGoingFriendId((String) marker.getTag());
                        renderAllMarker();
                    }
                }
                return true;
            }
        });
    }
    void goToHistoryPlace(){
        if (getArguments() != null) {
            Bundle args = getArguments();
            if (args != null && args.containsKey("historyPlace")) {
                HistoryPlace historyPlace = args.getParcelable("historyPlace");
                LatLng latLng = new LatLng(historyPlace.getLatitude(), historyPlace.getLongitude());
                addDestinationPoint(latLng);
                mGoogleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                TurnOnPlaceDetailView(latLng);
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