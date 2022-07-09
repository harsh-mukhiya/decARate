package com.example.Cart_24.login;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.Cart_24.R;

import java.util.ArrayList;

public class Adapter_In_Adapter extends RecyclerView.Adapter<Adapter_In_Adapter.ViewHolder> {
    ArrayList<Product_orderlist> product_orderlistArrayList;
    Context context;


    public Adapter_In_Adapter(ArrayList<Product_orderlist> product_orderlistArrayList, Context context) {
        this.product_orderlistArrayList = product_orderlistArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.order_list_product, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product_orderlist product_orderlist;
        product_orderlist = product_orderlistArrayList.get(position);

        holder.ordered_product_price.setText("₹" + product_orderlist.getPrice() );
        holder.ordered_product_name.setText(product_orderlist.getProduct_name());
        holder.quantity.setText( " Quantity " + product_orderlist.getQuantity());

        ImageView imageView = holder.order_image;
        String image = product_orderlist.getImage();
        if (!image.equalsIgnoreCase("")) {
            byte[] b = Base64.decode(image, Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(b, 0, b.length);
            imageView.setImageBitmap(decodedByte);
        } else {
            imageView.setImageResource(R.mipmap.ic_launcher);
        }

    }

    @Override
    public int getItemCount() {
        return product_orderlistArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView ordered_product_name, ordered_product_price, quantity;
        ImageView order_image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            order_image = itemView.findViewById(R.id.order_image);
            ordered_product_name = itemView.findViewById(R.id.ordered_product_name);
            ordered_product_price = itemView.findViewById(R.id.ordered_product_price);
            quantity = itemView.findViewById(R.id.quantity_AIA);
        }
    }
}
