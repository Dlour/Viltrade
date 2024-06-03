package com.example.viltrade2.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.viltrade2.Adapter.MyCartAdapter;
import com.example.viltrade2.R;
import com.example.viltrade2.models.CheckoutItem;
import com.example.viltrade2.models.MyCartModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CartFragment extends Fragment {
    private static final String TAG = "CartFragment";

    FirebaseAuth auth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    FirebaseFirestore firestore;

    RecyclerView recyclerView;
    MyCartAdapter cartAdapter;
    List<MyCartModel> cartModelList;

    Button clearBtn;
    ImageButton tambahQt, removeQt;
    Button Checkout;
    BottomSheetBehavior<View> bottomSheetBehavior;
    TextView totharga;
    CheckBox pilihSemua;

    public CartFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_cart, container, false);

        // Initialize Firebase
        auth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("AddToCart");
        firestore = FirebaseFirestore.getInstance();

        Checkout = root.findViewById(R.id.btnCart);

        Checkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveCheckoutData();
            }
        });

        // Initialize UI elements
        tambahQt = root.findViewById(R.id.addquantity); // Ensure you have the correct ID
        removeQt = root.findViewById(R.id.removequantity); // Ensure you have the correct ID

        clearBtn = root.findViewById(R.id.clearBtn);
        clearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearCart();
            }
        });

        recyclerView = root.findViewById(R.id.rec_cart);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        cartModelList = new ArrayList<>();
        cartAdapter = new MyCartAdapter(getActivity(), cartModelList);
        recyclerView.setAdapter(cartAdapter);

        // Set the total price change listener
        cartAdapter.setOnTotalPriceChangeListener(new MyCartAdapter.OnTotalPriceChangeListener() {
            @Override
            public void onTotalPriceChanged(double totalPrice) {
                totharga.setText("Rp " + totalPrice);
            }
        });

        totharga = root.findViewById(R.id.totharga);
        totharga.setText("Rp 0");

        pilihSemua = root.findViewById(R.id.pilihSemua);
        pilihSemua.setOnCheckedChangeListener((buttonView, isChecked) -> {
            cartAdapter.selectAll(isChecked);
        });

        if (auth.getCurrentUser() != null) {
            loadCartData();
        } else {
            Log.e(TAG, "Current user is null");
        }

        return root;
    }

    private void loadCartData() {
        String userId = auth.getCurrentUser().getUid();
        if (userId == null) {
            Log.e(TAG, "User ID is null");
            return;
        }

        databaseReference.child(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            cartModelList.clear();
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                MyCartModel cartModel = dataSnapshot.getValue(MyCartModel.class);
                                if (cartModel != null) {
                                    cartModelList.add(cartModel);
                                    fetchProductDetails(cartModel);
                                }
                            }
                            cartAdapter.notifyDataSetChanged();
                        } else {
                            Log.e(TAG, "No data found in Firebase for user: " + userId);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e(TAG, "Database error: " + error.getMessage());
                    }
                });
    }

    private void fetchProductDetails(MyCartModel cartModel) {
        String productId = cartModel.getProductId();
        if (productId == null) {
            Log.e(TAG, "Product ID is null for cart item: " + cartModel.toString());
            return;
        }

        DatabaseReference productRef = firebaseDatabase.getReference("products").child(productId);
        productRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String imageUrl = snapshot.child("img_url").getValue(String.class);
                    Double price = snapshot.child("price").getValue(Double.class);

                    // Check if price is null
                    if (price == null) {
                        Log.e(TAG, "Price is null for product: " + productId);
                        price = 0.0; // or handle it accordingly
                    }

                    cartModel.setImg_url(imageUrl);
                    cartModel.setTotalPrice(price);

                    cartAdapter.notifyDataSetChanged();
                } else {
                    Log.e(TAG, "No data found in Firebase for product: " + productId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Database error: " + error.getMessage());
            }
        });
    }

    private void calculateTotalPrice() {
        double total = 0;
        for (MyCartModel cartModel : cartModelList) {
            if (cartModel.isChecked()) {
                total += cartModel.getTotalPrice() * cartModel.getTotalQuantity();
            }
        }
        totharga.setText("Rp " + total);
    }

    private void clearCart() {
        String userId = auth.getCurrentUser().getUid();
        if (userId == null) {
            Log.e(TAG, "User ID is null");
            return;
        }

        // Clear cart in RecyclerView
        cartModelList.clear();
        cartAdapter.notifyDataSetChanged();

        // Clear cart in Firebase Realtime Database
        DatabaseReference cartReference = databaseReference.child(userId);
        cartReference.removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Cart data removed successfully from Firebase");
                        totharga.setText("Rp 0");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Failed to remove cart data from Firebase", e);
                    }
                });
    }

    private void saveCheckoutData() {
        String userId = auth.getCurrentUser().getUid();
        if (userId == null) {
            Log.e(TAG, "User ID is null");
            return;
        }

        List<MyCartModel> checkedItems = new ArrayList<>();
        for (MyCartModel cartModel : cartModelList) {
            if (cartModel.isChecked()) {
                checkedItems.add(cartModel);
            }
        }

        // Save to Firestore
        for (MyCartModel item : checkedItems) {
            Map<String, Object> checkoutData = new HashMap<>();
            checkoutData.put("userId", userId);
            checkoutData.put("productId", item.getProductId());
            checkoutData.put("img_url", item.getImg_url());
            checkoutData.put("productName", item.getProductName());
            checkoutData.put("totalPrice", item.getTotalPrice());
            checkoutData.put("totalQuantity", item.getTotalQuantity());

            firestore.collection("checkouts").add(checkoutData)
                    .addOnSuccessListener(documentReference -> {
                        Log.d(TAG, "Checkout item added to Firestore: " + documentReference.getId());
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Error adding checkout item to Firestore", e);
                    });
        }

        // Save to Realtime Database
        DatabaseReference checkoutRef = firebaseDatabase.getReference("checkouts").child(userId);
        for (MyCartModel item : checkedItems) {
            DatabaseReference newCheckoutRef = checkoutRef.push();
            newCheckoutRef.setValue(item)
                    .addOnSuccessListener(aVoid -> {
                        Log.d(TAG, "Checkout item added to Realtime Database");
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Error adding checkout item to Realtime Database", e);
                    });
        }

        // Clear the cart after checkout
        clearCart();

        // Navigate to CheckOutFragment
        CheckOutFragment checkOutFragment = new CheckOutFragment();
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.home_fragment, checkOutFragment)
                .addToBackStack(null)
                .commit();
    }
}
