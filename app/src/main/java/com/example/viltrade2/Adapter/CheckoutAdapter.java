package com.example.viltrade2.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.viltrade2.R;
import com.example.viltrade2.models.CheckoutItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class CheckoutAdapter extends RecyclerView.Adapter<CheckoutAdapter.CheckoutViewHolder> {

    private List<CheckoutItem> checkoutItemList;
    private Context context;
    private DatabaseReference realtimeDbReference;
    private String userId;

    public CheckoutAdapter(Context context, List<CheckoutItem> checkoutItemList) {
        this.context = context;
        this.checkoutItemList = checkoutItemList;
        this.realtimeDbReference = FirebaseDatabase.getInstance().getReference("checkouts");
        this.userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        fetchDataFromRealtimeDb();
    }

    @NonNull
    @Override
    public CheckoutViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_checkout, parent, false);
        return new CheckoutViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CheckoutViewHolder holder, int position) {
        CheckoutItem item = checkoutItemList.get(position);
        Glide.with(context).load(item.getImg_url()).into(holder.imgItemCO);


        realtimeDbReference.child(userId).child(item.getProductId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Double totalPrice = snapshot.child("totalPrice").getValue(Double.class);
                    Integer totalQuantity = snapshot.child("totalQuantity").getValue(Integer.class);
                    if (totalPrice != null && totalQuantity != null) {
                        holder.hargaCO.setText("Rp " + totalPrice);
                        holder.quantCO.setText("Jumlah: " + totalQuantity);
                    } else {
                        Log.e("CheckoutAdapter", "totalPrice or totalQuantity is null for productId: " + item.getProductId());
                    }
                } else {
                    Log.e("CheckoutAdapter", "Snapshot does not exist for productId: " + item.getProductId());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("CheckoutAdapter", "Database error: " + error.getMessage());
            }
        });
    }

    @Override
    public int getItemCount() {
        return checkoutItemList.size();
    }

    public static class CheckoutViewHolder extends RecyclerView.ViewHolder {

        ImageView imgItemCO;
        TextView hargaCO;
        TextView quantCO;

        public CheckoutViewHolder(@NonNull View itemView) {
            super(itemView);
            imgItemCO = itemView.findViewById(R.id.img_itemCO);
            hargaCO = itemView.findViewById(R.id.harga_CO);
            quantCO = itemView.findViewById(R.id.quantCO);
        }
    }

    private void fetchDataFromRealtimeDb() {
        realtimeDbReference.child(userId).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, String previousChildName) {
                CheckoutItem item = snapshot.getValue(CheckoutItem.class);
                checkoutItemList.add(item);
                notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, String previousChildName) {
                CheckoutItem updatedItem = snapshot.getValue(CheckoutItem.class);
                for (int i = 0; i < checkoutItemList.size(); i++) {
                    if (checkoutItemList.get(i).getProductId().equals(updatedItem.getProductId())) {
                        checkoutItemList.set(i, updatedItem);
                        notifyItemChanged(i);
                        break;
                    }
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                CheckoutItem removedItem = snapshot.getValue(CheckoutItem.class);
                for (int i = 0; i < checkoutItemList.size(); i++) {
                    if (checkoutItemList.get(i).getProductId().equals(removedItem.getProductId())) {
                        checkoutItemList.remove(i);
                        notifyItemRemoved(i);
                        break;
                    }
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, String previousChildName) {
                // Not needed for this use case
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("CheckoutAdapter", "Database error: " + error.getMessage());
            }
        });
    }
}
