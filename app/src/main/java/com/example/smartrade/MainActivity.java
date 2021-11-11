package com.example.smartrade;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Temporarily just works as a way to test API

        Button getPriceBtn = findViewById(R.id.get_price_btn);
        getPriceBtn.setOnClickListener(
                v -> {
                    this.getTickerPrice();
                }
        );

    }

    private void getTickerPrice() {

        EditText enterTicker = findViewById(R.id.enter_ticker);
        String ticker = enterTicker.getText().toString();
        System.out.println("Requested Ticker: " + ticker);

        // Make ticker api request

        // Output result to textview.
        TextView displayPrice = findViewById(R.id.display_ticker);
        displayPrice.setText(ticker);
    }
}