package com.example.viltrade2;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.viltrade2.Adapter.SearchAdapter;
import com.example.viltrade2.models.NewProductsModel;
import com.example.viltrade2.models.ShowAllModel;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class searchActivoty extends AppCompatActivity {

    EditText search;
    ImageView back;
    RecyclerView recyclerView;
    FirebaseFirestore fstore;
    SearchAdapter adapter;
    List<NewProductsModel> productList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_search_activoty);

        search = findViewById(R.id.searchAct);
        back = findViewById(R.id.back);
        recyclerView = findViewById(R.id.rec_search);
        fstore = FirebaseFirestore.getInstance();
        productList = new ArrayList<>();

        adapter = new SearchAdapter(this, productList);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.setAdapter(adapter);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), landing_page.class));
                finish();
            }
        });

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String searchT = s.toString();
                if (!searchT.isEmpty()) {
                    setupSearchRecyclerView(searchT);
                } else {
                    productList.clear();
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void setupSearchRecyclerView(String searchT) {
        Query query = fstore.collection("NewProducts")
                .orderBy("name")
                .startAt(searchT)
                .endAt(searchT + "\uf8ff");

        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<NewProductsModel> newProductList = new ArrayList<>();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    NewProductsModel product = document.toObject(NewProductsModel.class);
                    newProductList.add(product);
                }
                // Perbarui daftar yang digunakan oleh adapter
                adapter.updateList(newProductList);

                if (newProductList.isEmpty() && searchT.isEmpty()) {
                    productList.clear();
                    adapter.notifyDataSetChanged();
                }
            } else {
                // Handle kesalahan saat mengambil data
                // Misalnya, tampilkan pesan kesalahan atau log
                Log.e("SearchActivity", "Error getting documents: ", task.getException());
            }
        });
    }


}
