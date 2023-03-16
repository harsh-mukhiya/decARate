package com.example.decARate;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import com.example.decARate.login.LogIn;
import com.example.decARate.login.ProductList;

public class MainActivity extends AppCompatActivity {
    SharedPreferences sp;
    final Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().hide();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.temp);
        sp = getSharedPreferences("ID", MODE_PRIVATE);

        if (!sp.getString("email", "").equalsIgnoreCase("")) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(getApplicationContext(), ProductList.class);
                    startActivity(intent);
                    finish();
                }
            }, 2000);
        } else
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(getApplicationContext(), LogIn.class);
                    startActivity(intent);
                    finish();
                }
            }, 2000);
    }


}