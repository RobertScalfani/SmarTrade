package com.example.smartrade;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.smartrade.webservices.Database;
import com.example.smartrade.webservices.DatabaseListener;

public class LoginActivity extends AppCompatActivity implements DatabaseListener {

    private static final String PASSWORD = "password";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Database.initializeDatabase(this);

        Button loginBtn = findViewById(R.id.login_btn);
        loginBtn.setOnClickListener(
            v -> {
                // Check that login is successful
                // If successful, open the main activity
                EditText emailText = findViewById(R.id.email_text);
                String submittedEmail = emailText.getText().toString();
                if(!validateLogin()) {
                    Database.getDatabase().promptLogin(submittedEmail, PASSWORD, this);
                }
                if(validateLogin()){
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        );

        Button registerBtn = findViewById(R.id.register_btn);
        registerBtn.setOnClickListener(
            v -> {
                // Check that login is successful
                // If successful, open the main activity
                EditText emailText = findViewById(R.id.email_text);
                String submittedEmail = emailText.getText().toString();
                Database.getDatabase().promptRegistration(submittedEmail, PASSWORD, this);
            }
        );
    }

    private boolean validateLogin() {
        return Database.getDatabase().validateLogin();
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.i("FIREBASE", "ONSTART");
        // Check if user is signed in (non-null).
        if(validateLogin()) {
            try {
                Toast.makeText(LoginActivity.this, "Already logged in as " + Database.getDatabase().getCurrentUser().getDisplayName() ,Toast.LENGTH_SHORT).show();
            } catch (Database.FirebaseAccessException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void notifyMessage(String message) {
        Toast.makeText(LoginActivity.this, message ,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void notifyCashBalanceUpdate(double newCashBalance) {
//        throw new UnsupportedOperationException("Login activity does not support cash balances.");
    }

    @Override
    public void notifyShareCountUpdate(String ticker, double newSharesCount) {
//        throw new UnsupportedOperationException("Login activity does not support share counts.");
    }

    @Override
    public void notifyStockList(String result, double sharesOwned, int position) {

    }
}