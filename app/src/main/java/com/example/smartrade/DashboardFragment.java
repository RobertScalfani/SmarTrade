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

import com.google.firebase.auth.FirebaseAuth;

public class DashboardFragment extends Fragment {

    public DashboardFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_dashboard, container, false);

        TextView portfolioValue = rootView.findViewById(R.id.textViewPortfolioValue);
        TextView positionValue = rootView.findViewById(R.id.textviewPositionValue);
        TextView cashBalance = rootView.findViewById(R.id.cashBalance);
        TextView gainLossDollar = rootView.findViewById(R.id.textViewGainlossD);
        TextView gainLossPercentage = rootView.findViewById(R.id.textViewGainlossP);

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
}