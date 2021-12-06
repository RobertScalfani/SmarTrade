package com.example.smartrade;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smartrade.webservices.Database;
import com.example.smartrade.webservices.DatabaseListener;
import com.google.firebase.auth.FirebaseAuth;

public class DashboardFragment extends Fragment implements DatabaseListener {

    TextView portfolioValue;
    TextView positionValue;
    TextView cashBalance;
    TextView gainLossDollar;
    TextView gainLossPercentage;

    public DashboardFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Update the current Cash Balance.
        Database.initializeDatabase(this);
        Database.getDatabase().addToCashBalance(0.0);
    }

    @Override
    public void onResume() {
        super.onResume();
        Database.initializeDatabase(this);
        // Update the current Cash Balance.
        Database.getDatabase().addToCashBalance(0.0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_dashboard, container, false);

        this.portfolioValue = rootView.findViewById(R.id.textViewPortfolioValue);
        this.positionValue = rootView.findViewById(R.id.textviewPositionValue);
        this.cashBalance = rootView.findViewById(R.id.cashBalance);
        this.gainLossDollar = rootView.findViewById(R.id.textViewGainlossD);
        this.gainLossPercentage = rootView.findViewById(R.id.textViewGainlossP);

        Button dashLogoutBtn = rootView.findViewById(R.id.dash_logout_btn);
        dashLogoutBtn.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(this.getContext(), LoginActivity.class);
            startActivity(intent);
//            finish();
            String signOutMsg = "You are now signed out.";
            Toast.makeText(this.getContext(), signOutMsg, Toast.LENGTH_SHORT).show();
        });

        return rootView;
    }

    @Override
    public void notifyMessage(String message) {
    }

    @Override
    public void notifyCashBalanceUpdate(double newCashBalance) {
        this.cashBalance.setText(String.format("%.2f",newCashBalance));
    }

    @Override
    public void notifyShareCountUpdate(double newSharesCount) {

    }
}