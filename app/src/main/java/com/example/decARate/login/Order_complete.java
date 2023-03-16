package com.example.decARate.login;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.decARate.R;

public class Order_complete extends AppCompatActivity {
    TextView continuebtn;
    TextView orderId;

    @Override
    public void onBackPressed() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);
        getSupportActionBar().hide();
        continuebtn = findViewById(R.id.continue_btn);
        orderId = findViewById(R.id.order_id);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(" Order ID ");
        Intent intent = getIntent();
        String id = intent.getStringExtra("orderId");
        orderId.setText(id);

        continuebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Order_complete.this, ProductList.class));
                finishAffinity();
            }
        });

    }
}