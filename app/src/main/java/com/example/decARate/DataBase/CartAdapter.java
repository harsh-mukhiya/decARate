package com.example.decARate.DataBase;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.decARate.AdapterCallback;
import com.example.decARate.R;
import com.example.decARate.login.Cart;

import java.util.ArrayList;
import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder>  implements AdapterView.OnItemSelectedListener{
    private List<Product> mproductList;
    private Context context;
    CURDOperations curd;
    TextView price;
    AdapterCallback adapterCallback;
    ArrayList<String> unAvailableProductID = new ArrayList<>();

    public CartAdapter(List<Product> mproductList, Context context, CURDOperations curd, TextView textView, AdapterCallback adapterCallback) {
        this.context = context;
        this.mproductList = mproductList;
        this.curd = curd;
        this.price = textView;
        this.adapterCallback = adapterCallback;


    }

    public void refreshList(List<Product> products) {
        this.mproductList = products;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CartAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cart_adapter, parent, false);

        return new ViewHolder(view);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull CartAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.price.setText("₹ " + mproductList.get(position).getPrice());
        holder.product_name.setText(mproductList.get(position).getProduct_name());


        holder.pQuantity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        curd.update(mproductList.get(position).email, mproductList.get(position).uid,i+1);
                    }
                };
                Thread thread = new Thread(runnable);
                thread.start();
                mproductList.get(position).setQuantity(i+1);
                updatePrice();

            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) { }
        });
        ArrayAdapter ad
                = new ArrayAdapter(
                context,
                android.R.layout.simple_spinner_item,
                quantity(mproductList.get(position).getmaxQuantity()));
        ad.setDropDownViewResource(
                android.R.layout
                        .simple_spinner_dropdown_item);
        holder.pQuantity.setAdapter(ad);
        holder.pQuantity.setSelection(mproductList.get(position).getQuantity()-1);

        if (isAvailable(String.valueOf(mproductList.get(position).getUid()))) {
            holder.product_name.setTextColor(context.getResources().getColor(R.color.red));
        } else {
                holder.product_name.setTextColor(context.getResources().getColor(R.color.zblack));
        }

        String image = mproductList.get(position).getImageUrl();
        if (!image.equalsIgnoreCase("")) {
            byte[] b = Base64.decode(image, Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(b, 0, b.length);
            holder.imageView.setImageBitmap(decodedByte);
        } else {
            holder.imageView.setImageResource(R.mipmap.ic_launcher);
        }

        if(((Cart)context).getType().equalsIgnoreCase("buy")){
            holder.cremove.setVisibility(View.GONE);
        }
        else {
            holder.cremove.setVisibility(View.VISIBLE);
        }

        holder.cremove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Product product = new Product();
                product.uid = mproductList.get(position).getUid();
                product.product_name = mproductList.get(position).getProduct_name();
                product.price = mproductList.get(position).getPrice();
                product.imageUrl = mproductList.get(position).getImageUrl();
                product.email = mproductList.get(position).getEmail();
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        curd.removeFromCart(product);
                        adapterCallback.onMethodCallback();
                    }
                };
                Thread thread = new Thread(runnable);
                thread.start();
                mproductList.remove(position);
                updatePrice();
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mproductList.size();
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) { }
    @Override
    public void onNothingSelected(AdapterView<?> adapterView) { }


    static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView product_name;
        private final TextView price;
        private final Spinner pQuantity;
        private final ImageView imageView;
        private final ImageView cremove;
        private int newQuantity;
        Button checkout;

        public ViewHolder(View view) {
            super(view);
            product_name = view.findViewById(R.id.cproduct_name);
            price = view.findViewById(R.id.cprice);
            pQuantity = view.findViewById(R.id.cquantity);
            imageView = view.findViewById(R.id.cprofile_Image);
            cremove = view.findViewById(R.id.remove);
            checkout = view.findViewById(R.id.checkout);

        }
    }

    private Integer[] quantity(int max){
        Integer[] arr = new Integer[max];
        for (int i = 0; i < max; i++) {
            arr[i]=i+1;
        }
        return arr;

    }

    public void updatePrice() {
        int sum = 0;
        for (int i = 0; i < mproductList.size(); i++) {
            sum = sum + (Integer.parseInt(mproductList.get(i).getPrice())* mproductList.get(i).getQuantity());
        }
        price.setText(String.valueOf(sum));
    }

    public void notAvailable(String ids) {
        Toast.makeText(context, "Some item are out of stock", Toast.LENGTH_SHORT).show();
        String[] res = ids.split(",");
        for (int j = 0; j < res.length; j++) {
            unAvailableProductID.add(res[j]);
        }
        notifyDataSetChanged();

    }

    public boolean isAvailable(String ids) {
        for (int i = 0; i < unAvailableProductID.size(); i++) {
            if (unAvailableProductID.get(i).equalsIgnoreCase(ids)) {
                return true;
            }
        }
        return false;
    }


}
