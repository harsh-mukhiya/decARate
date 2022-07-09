package com.example.Cart_24.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.Cart_24.AdapterCallback;
import com.example.Cart_24.CustomAdapter;
import com.example.Cart_24.DEMO;
import com.example.Cart_24.DataBase.CURDOperations;
import com.example.Cart_24.R;
import com.example.Cart_24.Change_Password;
import com.google.android.material.navigation.NavigationView;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ProductList extends AppCompatActivity implements AdapterCallback {
    TextView product_name;
    TextView price;
    TextView pQuantity;
    CURDOperations curd;


    ProgressDialog loading, loggingout;
    RecyclerView recyclerView;
    ArrayList<DEMO> arr;
    CustomAdapter c;

    MenuItem menuItem;
    int cartCount = 0;
    TextView cartText;
    View view;

    DrawerLayout drawerLayout;
    ActionBarDrawerToggle toggle;

    Dialog dialog;
    SharedPreferences sp;
    SwipeRefreshLayout swiperefresh;

    String name = "";
    String email = "";

    @Override
    protected void onResume() {
        super.onResume();
        onMethodCallback();
        update();
    }

    @Override
    public void onMethodCallback() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                cartCount = curd.findAll(sp.getString("email", "")).size();
                Log.d("CartCount", String.valueOf(cartCount));
                if (cartText != null)
                    cartText.setText(String.valueOf(cartCount));
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);

        menuItem = menu.findItem(R.id.nav_cart);

        menuItem.setActionView(R.layout.cart_icon);
        view = menuItem.getActionView();
        cartText = view.findViewById(R.id.counter);
        cartText.setText(String.valueOf(cartCount));
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onOptionsItemSelected(menuItem);
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.nav_cart) {
            Intent myIntent = new Intent(getApplicationContext(), Cart.class);
            myIntent.putExtra("type","cart");
            startActivity(myIntent);
        }
        return toggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Products");
        curd = new CURDOperations(this);
        arr = new ArrayList<>();
        recyclerView = findViewById(R.id.rv);
        product_name = findViewById(R.id.product_name);
        price = findViewById(R.id.price);
        pQuantity = findViewById(R.id.quantity);

        swiperefresh = findViewById(R.id.swiperefresh);
        swiperefresh.setRefreshing(false);
        swiperefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getItems();
            }
        });


        sp = getSharedPreferences("ID", MODE_PRIVATE);

        drawerLayout = findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        toggle.setDrawerIndicatorEnabled(true);

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.nav_profile:
                        drawerLayout.closeDrawer(GravityCompat.START);
                        Intent intent = new Intent(getApplicationContext(), Profile.class);
                        startActivity(intent);

                        break;

                    case R.id.nav_Logout:
                        dialog = new Dialog(ProductList.this);
                        dialog.setContentView(R.layout.logout);
                        dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.background_dialog));
                        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        dialog.setCancelable(true);
                        dialog.getWindow().getAttributes().windowAnimations = R.style.animaion;
                        dialog.show();

                        TextView yes = dialog.findViewById(R.id.yes);
                        TextView no = dialog.findViewById(R.id.no);

                        yes.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialog.dismiss();
                                uploadTOKEN(" ");
                            }
                        });
                        no.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialog.cancel();

                            }
                        });
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;

                    case R.id.nav_reset_password:
                        Intent intent3 = new Intent(getApplicationContext(), Change_Password.class);
                        startActivity(intent3);
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;

                    case R.id.nav_orders:
                        Intent intent2 = new Intent(getApplicationContext(), OrderedList_Nav.class);
                        startActivity(intent2);
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;
                }

                return true;
            }
        });

        update();
        getItems();
    }

    private void getItems() {
        String s1 = sp.getString("email", "");
        loading = ProgressDialog.show(this, "Loading", "please wait", false, true);
        String url = "https://script.google.com/macros/s/AKfycbwjkkpoET6edMt9gFCSlHFp_56wOOOgyiO8nLzXLRyPHjQNXVe5TWW9LoPRH4PRoiS__g/exec";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        arr = new ArrayList<>();
                        try {
                            JSONObject jobj = new JSONObject(response);
                            JSONArray jsonArray = jobj.getJSONArray("product");

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jo = jsonArray.getJSONObject(i);

                                DEMO demo = new DEMO(jo.getInt("product_id"), jo.getString("product_name"), jo.getString("price"), jo.getString("quantity"), jo.getString("image"), s1);
                                arr.add(demo);
                            }
                            c = new CustomAdapter(arr, getApplicationContext(), ProductList.this);
                            recyclerView.setAdapter(c);
                            recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                            loading.dismiss();
                            swiperefresh.setRefreshing(false);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                }
        );
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(stringRequest);

    }

    void update() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                String s1 = sp.getString("email", "");
                String url = "https://script.google.com/macros/s/AKfycbzqSXaWRVWFLmWBN9Ldk90jJPMYRr64ckAdyw_xUzqel-PXe_AfZcvWly0oiqbGsrrO2w/exec?email=";
                String s2 = url + s1;

                RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, s2, null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    name = response.getString("name");
                                    email = response.getString("email");
                                    String image = response.getString("image");

                                    View hView = navigationView.getHeaderView(0);
                                    TextView profile_name = (TextView) hView.findViewById(R.id.profile_name);
                                    profile_name.setText(name);
                                    TextView profile_email = (TextView) hView.findViewById(R.id.profile_email);
                                    profile_email.setText(email);
                                    ImageView imageView = (ImageView) hView.findViewById(R.id.profile_Image);

                                    if (!image.equalsIgnoreCase("")) {
                                        byte[] b = Base64.decode(image, Base64.DEFAULT);
                                        Bitmap decodedByte = BitmapFactory.decodeByteArray(b, 0, b.length);
                                        imageView.setImageBitmap(decodedByte);
                                    } else
                                        imageView.setImageDrawable(getDrawable(R.mipmap.ic_launcher_round));

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                });
                queue.add(jsonObjectRequest);

            }
        };
        new Thread(runnable).start();
    }


    private void uploadTOKEN(String token) {
        loggingout = ProgressDialog.show(this, "Logout", "Just a moment...");
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        //POST TOKEN with email
        String url="https://script.google.com/macros/s/AKfycbyLg-9jrBKpl89sRThRV_LoC_nU9q_ya75Z-aNiV0F4VcDtNPsI4gLUN8LkbYb7LVX8/exec";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url
                , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                SharedPreferences.Editor editor = sp.edit();
                editor.clear();
                editor.commit();
                startActivity(new Intent(getApplicationContext(), LogIn.class));
                finishAffinity();
                loggingout.dismiss();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", token);
                params.put("email", sp.getString("email", ""));
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }

}