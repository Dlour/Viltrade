package com.example.viltrade2;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.viltrade2.models.MyCartModel;
import com.example.viltrade2.models.NewProductsModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class DetailActivity extends AppCompatActivity {

    ImageView detailedImg, imageView;
    TextView rating, name, description, price;
    Button tambah;

    double totalPrice;
    int quantity = 1;
    NewProductsModel newProductsModel = null;

    DatabaseReference databaseReference;
    FirebaseAuth auth;
    FirebaseUser currentUser;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_detail);

        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();

        if (currentUser == null) {
            Intent loginIntent = new Intent(DetailActivity.this, login_page.class);
            startActivity(loginIntent);
            finish();
            return;
        }

        imageView = findViewById(R.id.backtoolbar);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Menginisialisasi Firebase Realtime Database
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("AddToCart");

        // Mendapatkan objek NewProductsModel yang dikirimkan dari Intent
        final Object obj = getIntent().getSerializableExtra("detailed");
        if (obj instanceof NewProductsModel) {
            newProductsModel = (NewProductsModel) obj;
        } else {
            Toast.makeText(this, "Data produk tidak valid", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        detailedImg = findViewById(R.id.detail_image);
        name = findViewById(R.id.detail_name);
        rating = findViewById(R.id.rating);
        description = findViewById(R.id.detail_desc);
        price = findViewById(R.id.detail_price);
        tambah = findViewById(R.id.tambahbtn);

        // Menampilkan detail produk
        if (newProductsModel != null) {
            Glide.with(getApplicationContext()).load(newProductsModel.getImg_url()).into(detailedImg);
            name.setText(newProductsModel.getName());
            rating.setText(newProductsModel.getRating());
            description.setText(newProductsModel.getDescription());
            price.setText(" " + newProductsModel.getPrice());
            totalPrice = newProductsModel.getPrice() * quantity;
        }

        // Handle button click untuk menambahkan produk ke keranjang
        tambah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addToCart();
            }
        });
    }

    // Method untuk menambahkan produk ke keranjang
    private void addToCart() {
        String saveCurrentTime, saveCurrentDate;
        Calendar calForDate = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MM-yyyy");
        saveCurrentDate = currentDate.format(calForDate.getTime());
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss");
        saveCurrentTime = currentTime.format(calForDate.getTime());

        Double harga = Double.parseDouble(price.getText().toString());

        currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            databaseReference.child(currentUser.getUid())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            boolean itemExists = false;
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                MyCartModel cartItem = dataSnapshot.getValue(MyCartModel.class);
                                if (cartItem != null && cartItem.getProductId().equals(newProductsModel.getProductId())) {
                                    int newQuantity = cartItem.getTotalQuantity() + quantity;
                                    double newTotalPrice = cartItem.getTotalPrice() + (newProductsModel.getPrice() * quantity);

                                    dataSnapshot.getRef().child("TotalQuantity").setValue(newQuantity);
                                    dataSnapshot.getRef().child("price").setValue(newTotalPrice);
                                    itemExists = true;
                                    Toast.makeText(DetailActivity.this, "Updated Cart", Toast.LENGTH_SHORT).show();
                                    finish();
                                    break;
                                }
                            }

                            if (!itemExists) {
                                String newKey = databaseReference.child(currentUser.getUid()).push().getKey();
                                if (newKey != null) {
                                    HashMap<String, Object> cartMap = new HashMap<>();
                                    cartMap.put("productName", name.getText().toString());
                                    cartMap.put("price", harga);
                                    cartMap.put("currentTime", saveCurrentTime);
                                    cartMap.put("currentDate", saveCurrentDate);
                                    cartMap.put("TotalQuantity", quantity);
                                    cartMap.put("totalPrice", totalPrice);
                                    cartMap.put("img_url", newProductsModel.getImg_url());
                                    cartMap.put("productId", newProductsModel.getProductId());

                                    databaseReference.child(currentUser.getUid()).child(newKey).setValue(cartMap)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Toast.makeText(DetailActivity.this, "Added To Cart", Toast.LENGTH_SHORT).show();
                                                        finish();
                                                    } else {
                                                        Toast.makeText(DetailActivity.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(DetailActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Intent loginIntent = new Intent(DetailActivity.this, login_page.class);
            startActivity(loginIntent);
            finish();
        }
    }
}
