package com.example.smartrade.tabfragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationRequest;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.smartrade.R;
import com.example.smartrade.webservices.Database;
import com.example.smartrade.webservices.DatabaseListener;
import com.example.smartrade.webservices.TradeHistory;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.List;

public class LeaderboardFragment extends Fragment implements DatabaseListener {

    Button getLeaderboardBtn;
    FusedLocationProviderClient client;
    double latitude;
    double longitude;
    LocationManager locationManager;
    String provider;
    Location userLocation;

    public LeaderboardFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_leaderboard, container, false);
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        Criteria locationCriteria = new Criteria();
        locationCriteria.setAccuracy(Criteria.ACCURACY_FINE);

        //Initialize Location Listener
        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                userLocation = location;
                if(userLocation != null) {
                    longitude = userLocation.getLongitude();
                    latitude = userLocation.getLatitude();
                    Log.i("LAT", String.valueOf(latitude));
                    Log.i("Loc", "Location not null!");
                }
                else {
                    Log.e("LOC", "NULL");
                }
            }
            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {}
            @Override
            public void onProviderEnabled(String provider) {}
            @Override
            public void onProviderDisabled(String provider) {}
        };

        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0,0,locationListener);

        // Database init
        Database.initializeDatabase(this);

        getLeaderboardBtn = (Button) rootView.findViewById(R.id.leaderboardBtn);

        getLeaderboardBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("LAT", String.valueOf(latitude));
                Log.i("Loc", "Location not null!");
            }
        });


        return rootView;
    }



    @Override
    public void notifyMessage(String message) {

    }

    @Override
    public void notifyCashBalanceUpdate(double newCashBalance) {

    }

    @Override
    public void notifyShareCountUpdate(String ticker, double newSharesCount) {

    }

    @Override
    public void notifyStockList(String result, double sharesOwned, int position) {

    }

    @Override
    public void notifyTradeHistory(String ticker, TradeHistory tradeHistory, int position) {

    }



}