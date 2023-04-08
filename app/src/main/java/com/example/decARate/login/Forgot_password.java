package com.example.decARate.login;

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

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

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

public class Forgot_password extends AppCompatActivity {

    EditText email, new_password, confirm_new_password;
    TextView update_password;
    SharedPreferences sp;
    ProgressDialog loading;
    Boolean passwordVisible1 = false, passwordVisible2 = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgot_password);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Cart 24");
        actionBar.setDisplayHomeAsUpEnabled(true);
        sp = getSharedPreferences("ID", MODE_PRIVATE);

        email = findViewById(R.id.forgot_email_id);
        new_password = findViewById(R.id.forgot_new_password);
        confirm_new_password = findViewById(R.id.forgot_confirm_new_password);
        update_password = findViewById(R.id.forgot_reset_password);

        update_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (new_password.getText().toString().equals(confirm_new_password.getText().toString())) {
                    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
                    String emailcheck = email.getText().toString();
                    if (emailcheck.matches(emailPattern))
                        changePassword();
                    else
                        Toast.makeText(Forgot_password.this, "Invalid email syntax", Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(Forgot_password.this, "New password not match", Toast.LENGTH_SHORT).show();
            }
        });

        confirm_new_password.setOnTouchListener(new View.OnTouchListener() {
            final int DRAWABLE_RIGHT = 2;

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == motionEvent.ACTION_UP)
                    if (motionEvent.getRawX() >= (confirm_new_password.getRight() - confirm_new_password.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        int selection = confirm_new_password.getSelectionEnd();
                        if (passwordVisible1) {
                            confirm_new_password.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_baseline_visibility_off_24, 0);
                            confirm_new_password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                            passwordVisible1 = false;

                        } else {
                            confirm_new_password.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_baseline_visibility_24, 0);
                            confirm_new_password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                            passwordVisible1 = true;
                        }
                        confirm_new_password.setSelection(selection);
                        return true;
                    }
                return false;
            }
        });
        new_password.setOnTouchListener(new View.OnTouchListener() {
            final int DRAWABLE_RIGHT = 2;

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == motionEvent.ACTION_UP)
                    if (motionEvent.getRawX() >= (new_password.getRight() - new_password.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        int selection = new_password.getSelectionEnd();
                        if (passwordVisible2) {
                            new_password.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_baseline_visibility_off_24, 0);
                            new_password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                            passwordVisible2 = false;

                        } else {
                            new_password.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_baseline_visibility_24, 0);
                            new_password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                            passwordVisible2 = true;
                        }
                        new_password.setSelection(selection);
                        return true;
                    }
                return false;
            }
        });

    }

    public void changePassword() {
        String url = "https://script.google.com/macros/s/AKfycbxYb4QirAGVNxvlYxaPVDi44Xc58mnNlBu6t_illJppwPNaQb63L7aSzZN2ITxKmlWe/exec";
        String s1 = url + "?email=" + email.getText().toString();
        String s2 = s1 + "&new_password=" + confirm_new_password.getText().toString();

        loading = ProgressDialog.show(this, "Loading", "please wait", false, true);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, s2,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        String output = "";
                        try {
                            JSONObject jobj = new JSONObject(response);
                            output = jobj.getString("output");

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        if (output.equals("Success")) {
                            loading.dismiss();
                            Intent intent = new Intent(getApplicationContext(), LogIn.class);
                            startActivity(intent);
                            Toast.makeText(Forgot_password.this, "Password Reset successfully", Toast.LENGTH_SHORT).show();
                            finishAffinity();
                        } else {
                            Toast toast = Toast.makeText(getApplicationContext(), output, Toast.LENGTH_SHORT);
                            toast.show();
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

