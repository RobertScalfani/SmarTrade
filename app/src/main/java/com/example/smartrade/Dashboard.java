package com.example.smartrade;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class Dashboard extends AppCompatActivity {
    /**
     * Dashboard textviews
     */
    TextView portfolioValue = findViewById(R.id.textViewPortfolioValue);
    TextView positionValue = findViewById(R.id.textviewPositionValue);
    TextView cashBalance = findViewById(R.id.cashBalance);
    TextView gainLossDollar = findViewById(R.id.gainlossD);
    TextView gainLossPercentage = findViewById(R.id.gainlossP);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // START NAVIGATION ACTIONS

        // we are on Dashboard so we need to implement...
        // - trade -- DONE
        // - leaderboard -- Need to build out leaderboard.
        // - logout -- DONE
        Button dashTradeBtn = findViewById(R.id.dash_trade_btn);
        dashTradeBtn.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        });

        Button dashLeaderboardBtn = findViewById(R.id.dash_leaderboard_btn);
        dashLeaderboardBtn.setOnClickListener(v -> {
            Toast.makeText(this, "Leaderboard need to be developed.", Toast.LENGTH_SHORT).show();
//            Intent intent = new Intent(this, MainActivity.class);
//            startActivity(intent);
        });

        Button dashLogoutBtn = findViewById(R.id.dash_logout_btn);
        dashLogoutBtn.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
            String signOutMsg = "You are now signed out.";
            Toast.makeText(Dashboard.this, signOutMsg, Toast.LENGTH_SHORT).show();
        });

        // END NAVIGATION ACTIONS
    }


}