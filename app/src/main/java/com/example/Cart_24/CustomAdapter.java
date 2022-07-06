package com.example.Cart_24;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.Cart_24.DataBase.CURDOperations;
import com.example.Cart_24.DataBase.Product;

import java.util.ArrayList;


public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {

    private final ArrayList<DEMO> localDataSet;
    AdapterCallback adapterCallback;
    Context mcontext;
    CURDOperations curd;


    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView product_name;
        private final TextView price;
        private final TextView pQuantity;
        private final ImageView imageView;
        private final TextView btnaddtocart;

        public ViewHolder(View view) {
            super(view);
            product_name = view.findViewById(R.id.product_name);
            price = view.findViewById(R.id.price);
            pQuantity = view.findViewById(R.id.quantity);
            imageView = view.findViewById(R.id.profile_Image);
            btnaddtocart = view.findViewById(R.id.add_to_cart);
        }
    }

    public CustomAdapter(ArrayList<DEMO> dataSet, Context context, AdapterCallback adapterCallback) {
        this.adapterCallback = adapterCallback;
        localDataSet = dataSet;
        mcontext = context;
        curd = new CURDOperations(context);

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.activity_adapter, viewGroup, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        DEMO mDEMO = localDataSet.get(position);
        TextView textView = viewHolder.product_name;
        textView.setText(mDEMO.getpName());
        TextView localPrice = viewHolder.price;
        localPrice.setText("₹ " + mDEMO.getPrice());

        TextView localQuantity = viewHolder.pQuantity;
        if (Integer.parseInt(mDEMO.getQuantity()) > 0) {
            localQuantity.setText("Available : " + mDEMO.getQuantity());
            localPrice.setVisibility(View.VISIBLE);
            viewHolder.btnaddtocart.setVisibility(View.VISIBLE);
        } else {
            localQuantity.setText("Out of Stock");
            localQuantity.setTextColor(Color.DKGRAY);
            localPrice.setVisibility(View.GONE);
            viewHolder.btnaddtocart.setVisibility(View.GONE);

        }

        ImageView imageView = viewHolder.imageView;
        String image = mDEMO.getImgURL();
        if (!image.equalsIgnoreCase("")) {
            byte[] b = Base64.decode(image, Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(b, 0, b.length);
            imageView.setImageBitmap(decodedByte);
        } else {
            imageView.setImageResource(R.mipmap.ic_launcher);
        }

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (curd.getProductById(mDEMO.getProductID(), mDEMO.getEmail())) {  //,  mDEMO.getEmail()
                    viewHolder.btnaddtocart.setText("Remove From Cart");
                } else {
                    viewHolder.btnaddtocart.setText("Add to Cart");
                }
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();


        viewHolder.btnaddtocart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Product product = new Product();
                product.uid = mDEMO.getProductID();
                product.product_name = mDEMO.getpName();
                product.price = mDEMO.getPrice();
                product.imageUrl = mDEMO.getImgURL();
                product.email = mDEMO.getEmail();
                product.setmaxQuantity(Integer.parseInt(mDEMO.getQuantity()));
                product.setQuantity(1);

                if (viewHolder.btnaddtocart.getText().toString().equalsIgnoreCase("Remove From Cart")) {
                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            curd.removeFromCart(product);
                            adapterCallback.onMethodCallback();
                        }
                    };
                    Thread thread = new Thread(runnable);
                    thread.start();
                    viewHolder.btnaddtocart.setText("Add to Cart");


                } else {
                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            curd.addToCart(product);
                            adapterCallback.onMethodCallback();
                        }
                    };
                    Thread thread = new Thread(runnable);
                    thread.start();
                    viewHolder.btnaddtocart.setText("Remove From Cart");

                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return localDataSet.size();
    }
}


