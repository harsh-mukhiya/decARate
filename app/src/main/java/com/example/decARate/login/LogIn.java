package com.example.decARate.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
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
import com.example.decARate.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class LogIn extends AppCompatActivity {

    EditText lemail, lpassword;
    TextView btnLogin;
    TextView lsignUp, forgot_your_password;
    ProgressDialog loading;
    SharedPreferences sp;
    Boolean passwordVisible = false;
    protected String TOKEN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("                         decARate");

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


        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.d( "token","Fetching FCM registration token failed");
                            return;
                        }
                        // Get new FCM registration token
                        TOKEN = task.getResult();
                        Log.d( "token",TOKEN);
                    }
                });

    }

    private void getItems() {

        loading = ProgressDialog.show(this, "Loading", "please wait", false, true);
        String url = "https://script.google.com/macros/s/AKfycbwVHmps9xBXOFAc1v2ZArYbIvjfwqGMjoMpNeCkoYfIg5vutrKUeC7rX2Njn3mTVRDU2g/exec?";
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

                                uploadTOKEN(TOKEN);

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

    private void uploadTOKEN(String token) {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        //POST TOKEN with email
        String url="https://script.google.com/macros/s/AKfycby-ecmVbJt_lnmKWmUQlkReYBMnUp0t9EuCHhB8CLlw3ME0lnjZa8BgDyd2a9hioICIIw/exec";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url
                , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println(response +"token change");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token",token);
                params.put("email",sp.getString("email",""));
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }


}