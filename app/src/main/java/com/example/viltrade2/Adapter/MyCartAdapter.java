package com.example.viltrade2.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.viltrade2.R;
import com.example.viltrade2.models.MyCartModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class MyCartAdapter extends RecyclerView.Adapter<MyCartAdapter.ViewHolder> {

    private Context context;
    private List<MyCartModel> cartModelList;
    private OnTotalPriceChangeListener totalPriceChangeListener;
    private FirebaseAuth auth;
    private DatabaseReference databaseReference;

    public interface OnTotalPriceChangeListener {
        void onTotalPriceChanged(double totalPrice);
    }

    public void setOnTotalPriceChangeListener(OnTotalPriceChangeListener listener) {
        this.totalPriceChangeListener = listener;
    }

    public MyCartAdapter(Context context, List<MyCartModel> cartModelList) {
        this.context = context;
        this.cartModelList = cartModelList;
        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("AddToCart");
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_cart_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MyCartModel cartModel = cartModelList.get(position);

        if (cartModel != null) {
            holder.name.setText(cartModel.getProductName());
            holder.price.setText(String.valueOf(cartModel.getTotalPrice() * cartModel.getTotalQuantity())); // Display total price
            holder.date.setText(cartModel.getCurrentDate());
            holder.time.setText(cartModel.getCurrentTime());
            holder.quantity.setText(String.valueOf(cartModel.getTotalQuantity()));

            // Load image using Glide from Firebase Realtime Database URL
            Glide.with(context)
                    .load(cartModel.getImg_url())
                    .placeholder(R.drawable.ic_launcher_foreground) // Placeholder image while loading
                    .error(R.drawable.ic_launcher_foreground) // Error image if loading fails
                    .into(holder.itemPict);

            holder.tambahQuant.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tambahQuantity(holder.getAdapterPosition());
                }
            });

            holder.removeQuant.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    removeQuantity(holder.getAdapterPosition());
                }
            });

            holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                cartModel.setChecked(isChecked);
                updateTotalPrice();
            });

            holder.checkBox.setChecked(cartModel.isChecked());
        }
    }

    public void selectAll(boolean isChecked) {
        for (MyCartModel cartModel : cartModelList) {
            cartModel.setChecked(isChecked);
        }
        notifyDataSetChanged();
        updateTotalPrice();
    }

    @Override
    public int getItemCount() {
        return cartModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, price, date, time, quantity;
        ImageView itemPict;
        ImageButton tambahQuant, removeQuant;
        CheckBox checkBox;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.namaItem);
            price = itemView.findViewById(R.id.priceItem);
            date = itemView.findViewById(R.id.datItem);
            time = itemView.findViewById(R.id.timeItem);
            quantity = itemView.findViewById(R.id.totQuant);
            itemPict = itemView.findViewById(R.id.pictItem);
            tambahQuant = itemView.findViewById(R.id.addquantity);
            removeQuant = itemView.findViewById(R.id.removequantity);
            checkBox = itemView.findViewById(R.id.checkitem);
        }
    }

    public void tambahQuantity(int position) {
        MyCartModel cartModel = cartModelList.get(position);
        int currentQuant = cartModel.getTotalQuantity();
        cartModel.setTotalQuantity(currentQuant + 1);
        double newTotalPrice = cartModel.getTotalPrice() * cartModel.getTotalQuantity();
        cartModel.setTotalPrice(newTotalPrice);
        updateDatabase(cartModel);
        updateTotalPrice();
        notifyItemChanged(position);
    }

    public void removeQuantity(int position) {
        MyCartModel cartModel = cartModelList.get(position);
        int currentQuant = cartModel.getTotalQuantity();
        if (currentQuant > 1) {
            cartModel.setTotalQuantity(currentQuant - 1);
            double newTotalPrice = cartModel.getTotalPrice() * cartModel.getTotalQuantity();
            cartModel.setTotalPrice(newTotalPrice);
            updateDatabase(cartModel);
            updateTotalPrice();
            notifyItemChanged(position);
        } else {
            Toast.makeText(context.getApplicationContext(), "quantity tidak boleh kurang dari 1", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateDatabase(MyCartModel cartModel) {
        String userId = auth.getCurrentUser().getUid();
        if (userId == null) {
            Log.e("MyCartAdapter", "User ID is null");
            return;
        }
        databaseReference.child(userId).child(cartModel.getProductId()).setValue(cartModel)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("MyCartAdapter", "Quantity updated successfully in Firebase");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("MyCartAdapter", "Failed to update quantity in Firebase", e);
                    }
                });
    }

    private void updateTotalPrice() {
        double totalPrice = 0;
        for (MyCartModel item : cartModelList) {
            if (item.isChecked()) {
                totalPrice += item.getTotalPrice() * item.getTotalQuantity();
            }
        }
        if (totalPriceChangeListener != null) {
            totalPriceChangeListener.onTotalPriceChanged(totalPrice);
        }
    }
}
