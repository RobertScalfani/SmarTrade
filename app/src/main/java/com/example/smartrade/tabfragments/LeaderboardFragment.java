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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.smartrade.R;
import com.example.smartrade.webservices.Database;
import com.example.smartrade.webservices.DatabaseListener;
import com.example.smartrade.webservices.TradeHistory;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LeaderboardFragment extends Fragment implements DatabaseListener, LocationListener {

    Button getLeaderboardBtn;
    FusedLocationProviderClient client;
    double latitude;
    double longitude;
    LocationManager locationManager;
    String provider;
    Location userLocation;
    ArrayList<String> users = new ArrayList<>();

    public LeaderboardFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_leaderboard, container, false);
        ListView leaderboard = (ListView) rootView.findViewById(R.id.leaderboardListView);
        ArrayAdapter<String> listViewAdapter = new ArrayAdapter<String>(
                getActivity(),
                android.R.layout.simple_list_item_1,
                users
        );
        // Database init
        Database.initializeDatabase(this);

        getLeaderboardBtn = (Button) rootView.findViewById(R.id.leaderboardBtn);
        getLeaderboardBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    users.clear();
                    Database.getDatabase().addUserCoordinates(longitude, latitude);
                    Database.getDatabase().generateLeaderboardRankings();
                    for(Map.Entry<String, Double> entry : Database.sortedPortfolioBalances.entrySet()){
                        Log.w("LEADERFRAG", entry.getKey());
                        users.add("User: " + entry.getKey() + " Total Value: $" + entry.getValue());
                    }
                    listViewAdapter.notifyDataSetChanged();
                    leaderboard.invalidateViews();
                } catch (Database.FirebaseAccessException e) {
                    e.printStackTrace();
                }
            }
        });

        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,0, (android.location.LocationListener) this);

        leaderboard.setAdapter(listViewAdapter);
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


    @Override
    public void onLocationChanged(@NonNull Location location) {
        if(location == null){

        } else {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
        }
    }

    @Override
    public void onLocationChanged(@NonNull List<Location> locations) {

    }

    @Override
    public void onFlushComplete(int requestCode) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {

    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {

    }
}