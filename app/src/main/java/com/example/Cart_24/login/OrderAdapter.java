package com.example.Cart_24.login;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.Cart_24.R;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {


    Context context;
    ArrayList<OrderList> morderListArrayList;

    public OrderAdapter(ArrayList<OrderList> orderListArrayList, Context context) {
        this.morderListArrayList = orderListArrayList;
        this.context = context;
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView order_date, order_ID, order_Total;
        RecyclerView recyclerView_in_recycleView;
        LinearLayout layout;

        public ViewHolder(View view) {
            super(view);
            order_date = view.findViewById(R.id.order_date);
            order_ID = view.findViewById(R.id.order_list_id);
            order_Total = view.findViewById(R.id.order_total);
            layout = view.findViewById(R.id.layout_order);
            recyclerView_in_recycleView = view.findViewById(R.id.rv_in_rv);

        }
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.order_list_adapter, parent, false);
        return new ViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        OrderList orderList;
        orderList = morderListArrayList.get(position);
        holder.order_Total.setText("  Total ₹" + orderList.getTotal());
        holder.order_ID.setText(orderList.getOrderId());

        holder.order_date.setText(dateAndTime(orderList.getDate()));


        holder.layout.setOnClickListener(view -> {
            updateList(orderList.isActive, position);
        });

        if (orderList.isActive) {
            holder.recyclerView_in_recycleView.setVisibility(View.VISIBLE);

            ArrayList<Product_orderlist> listProducts = morderListArrayList.get(position).getProducts();

            Adapter_In_Adapter AIA = new Adapter_In_Adapter(listProducts, context);
            holder.recyclerView_in_recycleView.setAdapter(AIA);
            holder.recyclerView_in_recycleView.setLayoutManager(new LinearLayoutManager(context));

        } else {
            holder.recyclerView_in_recycleView.setVisibility(View.GONE);
        }
    }

    private void updateList(boolean status, int position) {
        for (int i = 0; i < morderListArrayList.size(); i++) {
            morderListArrayList.get(i).setActive(false);
        }
        morderListArrayList.get(position).setActive(!status);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return morderListArrayList.size();
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public String dateAndTime(String input) {
        Instant inst = Instant.parse(input);
        LocalDate date_only = inst.atZone(ZoneId.systemDefault()).toLocalDate();
        LocalTime time = inst.atZone(ZoneId.systemDefault()).toLocalTime();
        String newtime = String.valueOf(time);
        String[] arr = newtime.split(":");

        return date_only + "  " + arr[0] + ":" + arr[1];
    }


}
