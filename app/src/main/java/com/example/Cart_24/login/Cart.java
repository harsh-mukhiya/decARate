package com.example.Cart_24.login;

import static java.lang.Integer.parseInt;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.Cart_24.AdapterCallback;
import com.example.Cart_24.DataBase.CURDOperations;
import com.example.Cart_24.DataBase.CartAdapter;
import com.example.Cart_24.DataBase.Product;
import com.example.Cart_24.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

public class Cart extends AppCompatActivity implements AdapterCallback {
    List<Product> productList;
    RecyclerView recyclerView;
    CURDOperations curd;
    CartAdapter cartAdapter;
    TextView price;
    TextView checkout;
    String output;
    ImageView imageView3;
    int cartCount;
    LinearLayout linearLayout;
    ProgressDialog progressDialog;
    SharedPreferences sp;


    @Override
    public void onMethodCallback() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                cartCount = curd.findAll(sp.getString("email", "")).size();
                if (cartCount == 0) {
                    linearLayout.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.GONE);
                    imageView3.setVisibility(View.VISIBLE);
                } else {
                    linearLayout.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.VISIBLE);
                    imageView3.setVisibility(View.GONE);
                }


            }
        };
        Thread thread = new Thread(runnable);
        thread.start();


    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent mIntent = getIntent();
        int intValue = mIntent.getIntExtra("intVariableName", 0);
        setContentView(R.layout.activity_cart);
        price = findViewById(R.id.sum);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        sp = getSharedPreferences("ID", MODE_PRIVATE);

        recyclerView = findViewById(R.id.recycler);
        checkout = findViewById(R.id.checkout);
        linearLayout = findViewById(R.id.linearLayout4);
        imageView3 = findViewById(R.id.imageView3);

        if (intValue == 0) {
            linearLayout.setVisibility(View.GONE);
            recyclerView.setVisibility(View.GONE);
            imageView3.setVisibility(View.VISIBLE);
        } else {
            linearLayout.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.VISIBLE);
            imageView3.setVisibility(View.GONE);
        }

        productList = new ArrayList<>();
        curd = new CURDOperations(this);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(" Cart ");
        actionBar.setDisplayHomeAsUpEnabled(true);

        cartAdapter = new CartAdapter(productList, getApplicationContext(), curd, price, Cart.this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                productList = curd.findAll(sp.getString("email", ""));
                cartAdapter.refreshList(productList);
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();


        checkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                StringJoiner s = new StringJoiner(",");
                for (int i = 0; i < productList.size(); i++) {
                    s.add(String.valueOf(productList.get(i).getUid())+"-"+productList.get(i).getQuantity());
                }
                System.out.println(s);
                ids(s + ", ");
            }
        });


        recyclerView.setAdapter(cartAdapter);
    }

    public void ids(String ids) {
        String url = "https://script.google.com/macros/s/AKfycbwOXIeYVnJKJBAmKmkGjWjV6cTEYlxffEQZ7ncXsCVqaWcAYsIcfN3rNxj4n-_BboXy/exec?ids=";
        String s1 = url + ids;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, s1,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("response", response);
                        try {
                            JSONObject jobj = new JSONObject(response);
                            output = jobj.getString("output");
                            Log.d("ids", output);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        if (output.equals("successful")) {
                            postCheckout(ids);
                        } else {
                            cartAdapter.notAvailable(output);
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

    public void postCheckout(String ids) {
        progressDialog.show();
//        total ammount and email to post in new spread sheet

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                "https://script.google.com/macros/s/AKfycbw_LoLXuYWhPJKbOyxODeP2-vi2ER66EYrrXcCEVdgu6HsfA0XSpHUYsAhoYdPow_S-hA/exec",
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
                        Runnable runnable = new Runnable() {
                            @Override
                            public void run() {
                                for (int i = 0; i < productList.size(); i++) {
                                    curd.removeFromCart(productList.get(i));
                                }
                                productList.clear();
                            }
                        };
                        new Thread(runnable).start();
                        linearLayout.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.GONE);
                        imageView3.setVisibility(View.VISIBLE);
                        progressDialog.hide();
                        Intent intent = new Intent(getApplicationContext(), Order_complete.class);
                        intent.putExtra("orderId", output);
                        startActivity(intent);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("email", sp.getString("email", " "));
                params.put("total", price.getText().toString());
                params.put("productIDS", ids);
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