package com.example.Cart_24.login;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.Cart_24.R;

import org.json.JSONException;
import org.json.JSONObject;


public class LogIn extends AppCompatActivity {

    EditText lemail, lpassword;
    TextView btnLogin;
    TextView lsignUp, forgot_your_password;
    ProgressDialog loading;
    SharedPreferences sp;
    Boolean passwordVisible = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        lemail = findViewById(R.id.lemail);
        lpassword = findViewById(R.id.lpassword);
        btnLogin = findViewById(R.id.btnlogin);
        lsignUp = findViewById(R.id.lsignup);

        forgot_your_password = findViewById(R.id.forgot_password);


        forgot_your_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LogIn.this, Forgot_password.class));
            }
        });

        lpassword.setOnTouchListener(new View.OnTouchListener() {
            final int DRAWABLE_RIGHT = 2;

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == motionEvent.ACTION_UP)
                    if (motionEvent.getRawX() >= (lpassword.getRight())) {
                        int selection = lpassword.getSelectionEnd();
                        if (passwordVisible) {
                            lpassword.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_baseline_visibility_off_24, 0);
                            lpassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                            passwordVisible = false;

                        } else {
                            lpassword.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_baseline_visibility_24, 0);
                            lpassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                            passwordVisible = true;
                        }
                        lpassword.setSelection(selection);
                        return true;
                    }
                return false;
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getItems();
            }
        });
        lsignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LogIn.this, SignUp.class);
                startActivity(intent);

            }
        });

    }

    private void getItems() {

        loading = ProgressDialog.show(this, "Loading", "please wait", false, true);
        String url = "https://script.google.com/macros/s/AKfycbx3AHADFIWYqMYTCs0pKoXKt9CUP3l19c0HiECHr4LRcVD-XDhwOb-SSLqlO2Yxoe7V/exec?";
        String email = lemail.getText().toString();
        String password = lpassword.getText().toString();
        String s1 = url + "email=" + email;
        String s2 = s1 + "&password=" + password;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, s2,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        String email = lemail.getText().toString();
                        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
                        String output = "";


                        if (email.matches(emailPattern)) {
                            try {
                                JSONObject jobj = new JSONObject(response);
                                output = jobj.getString("output");

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            if (output.equals("Success")) {
                                loading.dismiss();
                                sp = getSharedPreferences("ID", MODE_PRIVATE);
                                SharedPreferences.Editor editor = sp.edit();
                                editor.putString("email", email);
                                editor.apply();


                                Intent intent = new Intent(getApplicationContext(), ProductList.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast toast = Toast.makeText(getApplicationContext(), output, Toast.LENGTH_SHORT);
                                toast.show();
                                loading.dismiss();
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "Invalid email syntax", Toast.LENGTH_SHORT).show();
                            loading.dismiss();
                        }
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }
        );

        int socketTimeOut = 50000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeOut, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

        stringRequest.setRetryPolicy(policy);

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(stringRequest);

    }


}