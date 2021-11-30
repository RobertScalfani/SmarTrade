package com.example.smartrade;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class Dashboard extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // START NAVIGATION ACTIONS
        // we are on Dashboard so we need to implement...
        // - trade -- DONE
        // - leaderboard
        // - logout -- DONE
        Button dashTradeBtn = findViewById(R.id.dash_trade_btn);
        dashTradeBtn.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
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