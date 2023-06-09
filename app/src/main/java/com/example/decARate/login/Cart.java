package com.example.decARate.login;

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
import com.example.decARate.AdapterCallback;
import com.example.decARate.DataBase.CURDOperations;
import com.example.decARate.DataBase.CartAdapter;
import com.example.decARate.DataBase.Product;
import com.example.decARate.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

public class Cart extends AppCompatActivity implements AdapterCallback {
    List<Product> productList=new ArrayList<>();
    RecyclerView recyclerView;
    CURDOperations curd;
    CartAdapter cartAdapter;
    TextView price;
    TextView checkout;
    String output;
    String type;
    ImageView imageView3;
    int cartCount;
    LinearLayout linearLayout;
    ProgressDialog progressDialog;
    SharedPreferences sp;

    public String getType() {
        return type;
    }

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
        setContentView(R.layout.activity_cart);
        price = findViewById(R.id.sum);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        sp = getSharedPreferences("ID", MODE_PRIVATE);

        recyclerView = findViewById(R.id.recycler);
        checkout = findViewById(R.id.checkout);
        linearLayout = findViewById(R.id.cardView4);
        imageView3 = findViewById(R.id.imageView3);

        productList = new ArrayList<>();
        curd = new CURDOperations(this);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(" Cart ");
        actionBar.setDisplayHomeAsUpEnabled(true);

        cartAdapter = new CartAdapter(productList, Cart.this, curd, price, Cart.this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(cartAdapter);
        type=mIntent.getStringExtra("type");
        if (type.equalsIgnoreCase("buy")){
            Product product= new Product();
            product.setQuantity(mIntent.getIntExtra("quantity",1));
            product.setProduct_name(mIntent.getStringExtra("name"));
            product.setPrice(mIntent.getStringExtra("price"));
            product.setImageUrl(mIntent.getStringExtra("image"));
            product.setmaxQuantity(mIntent.getIntExtra("maxQuantity",1));
            product.setUid(mIntent.getIntExtra("id",1));
            productList.add(product);
            cartAdapter.refreshList(productList);
        }
        else{
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    productList = curd.findAll(sp.getString("email", ""));
                    if (productList.size() == 0) {
                        linearLayout.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.GONE);
                        imageView3.setVisibility(View.VISIBLE);
                    } else {
                        linearLayout.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.VISIBLE);
                        imageView3.setVisibility(View.GONE);
                    }
                    cartAdapter.refreshList(productList);
                }
            };
            Thread thread = new Thread(runnable);
            thread.start();
        }


        checkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.show();
                StringJoiner s = new StringJoiner(",");
                for (int i = 0; i < productList.size(); i++) {
                    s.add(String.valueOf(productList.get(i).getUid())+"-"+productList.get(i).getQuantity());
                }
                System.out.println(s);
                ids(s + ", ");
            }
        });



    }

    public void ids(String ids) {
        String url = "https://script.google.com/macros/s/AKfycbwvqA7aqRC8Utbp9gNSMFFE3uFqiJ32TRlXid9HdldNpUo5YpbWtAAFreIn6-p6s1E/exec?ids=";
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
//        total ammount and email to post in new spread sheet
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                "https://script.google.com/macros/s/AKfycbxIuqr_NvXUzIiGusiDOjFIWBRSNwPoGXmuhcprLj1CiIjGuxfB0YsQQRuUXAb_GmVDsw/exec",
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
                        if(!type.equalsIgnoreCase("buy")) {
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
                        }
                        else{
                            productList.clear();
                        }
                        progressDialog.dismiss();
                        linearLayout.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.GONE);
                        imageView3.setVisibility(View.VISIBLE);
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