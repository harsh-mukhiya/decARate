package com.example.Cart_24;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.Cart_24.login.ProductList;

public class Chnage_password_Success extends AppCompatActivity {
    TextView continuebtn;

    @Override
    public void onBackPressed() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.after_password_chnage);
        getSupportActionBar().hide();
        continuebtn = findViewById(R.id.continue_btn);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Cart24");

        continuebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Chnage_password_Success.this, ProductList.class));
                finishAffinity();
            }
        });


    }
}