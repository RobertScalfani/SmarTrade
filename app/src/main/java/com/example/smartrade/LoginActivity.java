package com.example.smartrade;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        Button loginBtn = findViewById(R.id.login_btn);
        loginBtn.setOnClickListener(
            v -> {
                // Check that login is successful
                // If successful, open the main activity
                // if(successful)
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                // If unsuccessful, prompt the user to try again.
            }
        );
    }

}