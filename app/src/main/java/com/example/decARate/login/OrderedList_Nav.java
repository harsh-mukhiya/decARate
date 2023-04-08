package com.example.decARate.login;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.decARate.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class OrderedList_Nav extends AppCompatActivity {
    SharedPreferences sp;
    ArrayList<OrderList> orderListArrayList;
    ArrayList<Product_orderlist> product_orderlistArrayList;
    RecyclerView recyclerView;
    OrderAdapter orderAdapter;
    ProgressDialog loading;
    SwipeRefreshLayout swiperefresh;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ordered_list_nav);
        sp = getSharedPreferences("ID", MODE_PRIVATE);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(" Ordered List ");
        actionBar.setDisplayHomeAsUpEnabled(true);

        orderListArrayList = new ArrayList<>();
        product_orderlistArrayList = new ArrayList<>();
        recyclerView = findViewById(R.id.rvOrdered);

        swiperefresh = findViewById(R.id.swiperefresh_order);
        swiperefresh.setRefreshing(false);
        swiperefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getOrderlist();
            }
        });

        getOrderlist();
    }


    public void getOrderlist() {
        loading = ProgressDialog.show(this, "Loading", "please wait", false, false);

        String url = "https://script.google.com/macros/s/AKfycbxIuqr_NvXUzIiGusiDOjFIWBRSNwPoGXmuhcprLj1CiIjGuxfB0YsQQRuUXAb_GmVDsw/exec?email=";
        String s1 = url + sp.getString("email", "");

        StringRequest stringRequest = new StringRequest(Request.Method.GET, s1,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        orderListArrayList = new ArrayList<>();
                        try {
                            JSONObject jObj = new JSONObject(response);
                            JSONArray orderList = jObj.getJSONArray("orderList");
                            for (int i = orderList.length() - 1; i >= 0; i--) {
                                JSONObject object = orderList.getJSONObject(i);
                                String date = object.getString("date");
                                String orderID = object.getString("orderId");
                                int total = object.getInt("total");
                                String productIds_value = object.getString("productIds");
                                product_orderlistArrayList = new ArrayList<>();

                                JSONArray products = object.getJSONArray("products");
                                for (int j = 0; j < products.length(); j++) {

                                    JSONObject product_INFO = products.getJSONObject(j);
                                    int product_id = product_INFO.getInt("product_id");
                                    String product_name = product_INFO.getString("product_name");
                                    int price = product_INFO.getInt("price");
                                    String image = product_INFO.getString("image");
                                    int quantity = Integer.parseInt(product_INFO.getString("quantity"));
                                    Product_orderlist product_orderlist = new Product_orderlist(product_id, product_name, price, image, quantity);
                                    product_orderlistArrayList.add(product_orderlist);
                                }
                                OrderList orderList_obj = new OrderList(date, orderID, total, productIds_value, product_orderlistArrayList);

                                orderListArrayList.add(orderList_obj);


                            }

                            orderAdapter = new OrderAdapter(orderListArrayList, getApplicationContext());
                            recyclerView.setAdapter(orderAdapter);
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
        int socketTimeOut = 50000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeOut, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

        stringRequest.setRetryPolicy(policy);

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(stringRequest);


    }

}