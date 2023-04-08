package com.example.decARate.login;


import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
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
import com.example.decARate.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SignUp extends AppCompatActivity {
    EditText sname, semail, sphone, s1password, s2password;
    TextView slogin;
    TextView btnsignup;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("                         decARate");

        sname = findViewById(R.id.sname);
        semail = findViewById(R.id.semail);
        s1password = findViewById(R.id.s1password);
        s2password = findViewById(R.id.s2password);
        slogin = findViewById(R.id.slogin);
        sphone = findViewById(R.id.sphone);
        btnsignup = findViewById(R.id.signUp);
        progressDialog = new ProgressDialog(SignUp.this);
        progressDialog.setMessage("Loading...");

        btnsignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addNewEntry();

            }
        });

        slogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignUp.this, LogIn.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void addNewEntry() {


        String name = sname.getText().toString();
        String email = semail.getText().toString();
        String phone = sphone.getText().toString();
        String password = s1password.getText().toString();
        String cp = s2password.getText().toString();

        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";


        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || cp.isEmpty() || phone.isEmpty()) {
            Toast toast = Toast.makeText(getApplicationContext(), "Empty Field", Toast.LENGTH_SHORT);
            toast.show();
        } else if (phone.length() != 10) {
            Toast.makeText(getApplicationContext(), "Invalid phone no", Toast.LENGTH_SHORT).show();

        } else if (!email.matches(emailPattern)) {
            Toast.makeText(getApplicationContext(), "Invalid email address", Toast.LENGTH_SHORT).show();

        } else if (!password.equals(cp)) {
            Toast toast = Toast.makeText(getApplicationContext(), "Password Mismatched", Toast.LENGTH_SHORT);
            toast.show();

        } else if (password.length() < 6) {
            Toast.makeText(getApplicationContext(), "Password length atleast 6 or more", Toast.LENGTH_SHORT).show();

        } else {
            progressDialog.show();

            StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://script.google.com/macros/s/AKfycbwVHmps9xBXOFAc1v2ZArYbIvjfwqGMjoMpNeCkoYfIg5vutrKUeC7rX2Njn3mTVRDU2g/exec", new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    String output = "";

                    try {
                        JSONObject jobj = new JSONObject(response);
                        output = jobj.getString("output");

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    progressDialog.hide();
                    if (output.equals("success")) {
                        Intent intent = new Intent(SignUp.this, LogIn.class);
                        startActivity(intent);
                        finish();

                    } else {
                        Toast toast = Toast.makeText(getApplicationContext(), output, Toast.LENGTH_SHORT);
                        toast.show();
                    }


                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            }) {

                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("name", name);
                    params.put("pno", phone);
                    params.put("email", email);
                    params.put("password", password);
                    return params;
                }
            };

            int socketTimeOut = 50000;

            RetryPolicy retryPolicy = new DefaultRetryPolicy(socketTimeOut, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            stringRequest.setRetryPolicy(retryPolicy);

            RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(stringRequest);

        }

    }


}