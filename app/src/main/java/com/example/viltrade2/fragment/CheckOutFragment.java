package com.example.viltrade2.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.viltrade2.Adapter.CheckoutAdapter;
import com.example.viltrade2.R;
import com.example.viltrade2.models.CheckoutItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class CheckOutFragment extends Fragment {

    private RecyclerView recCo;
    private CheckoutAdapter checkoutAdapter;
    private List<CheckoutItem> checkoutItemList;
    private FirebaseFirestore db;
    private DatabaseReference realTimeDb;

    RecyclerView recyclerView;
    private FirebaseAuth auth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_check_out, container, false);

        // Initialize RecyclerView
        recCo = view.findViewById(R.id.rec_co);
        recCo.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize item list and adapter
        checkoutItemList = new ArrayList<>();
        checkoutAdapter = new CheckoutAdapter(getContext(), checkoutItemList);
        recCo.setAdapter(checkoutAdapter);
        recyclerView = view.findViewById(R.id.rec_co);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));

        // Initialize Firebase instances
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        realTimeDb = FirebaseDatabase.getInstance().getReference("checkouts");

        // Load data from both databases using current user ID
        loadCheckoutDataFromFirestore();
        loadCheckoutDataFromRealtimeDb();

        return view;
    }

    private void loadCheckoutDataFromFirestore() {
        String userId = auth.getCurrentUser().getUid();
        db.collection("checkouts")
                .whereEqualTo("userId", userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        checkoutItemList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            CheckoutItem item = document.toObject(CheckoutItem.class);
                            checkoutItemList.add(item);
                        }
                        checkoutAdapter.notifyDataSetChanged();
                    } else {
                        Log.e("CheckOutFragment", "Error fetching data from Firestore", task.getException());
                    }
                });
    }

    private void loadCheckoutDataFromRealtimeDb() {
        String userId = auth.getCurrentUser().getUid();
        realTimeDb.child(userId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        checkoutItemList.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            CheckoutItem item = dataSnapshot.getValue(CheckoutItem.class);
                            checkoutItemList.add(item);
                        }
                        checkoutAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("CheckOutFragment", "Error fetching data from Realtime Database", error.toException());
                    }
                });
    }
}
