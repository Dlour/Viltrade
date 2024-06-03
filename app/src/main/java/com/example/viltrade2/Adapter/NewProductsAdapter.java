package com.example.viltrade2.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.viltrade2.DetailActivity;
import com.example.viltrade2.R;
import com.example.viltrade2.models.NewProductsModel;

import java.util.List;

public class NewProductsAdapter extends RecyclerView.Adapter<NewProductsAdapter.ViewHolder> {

    private Context context;
    private List<NewProductsModel> newProductsModelList;

    public NewProductsAdapter(Context context, List<NewProductsModel> newProductsModelList) {
        this.context = context;
        this.newProductsModelList = newProductsModelList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.new_product, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        NewProductsModel product = newProductsModelList.get(position);
        holder.productName.setText(product.getName());
        holder.productPrice.setText(String.valueOf(product.getPrice()));
        Glide.with(context).load(product.getImg_url()).into(holder.productImage);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DetailActivity.class);
                intent.putExtra("detailed", product);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // Tambahkan flag ini
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return newProductsModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView productImage;
        TextView productName, productPrice;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.new_img);
            productName = itemView.findViewById(R.id.namaproduk);
            productPrice = itemView.findViewById(R.id.new_price);
        }
    }
}
