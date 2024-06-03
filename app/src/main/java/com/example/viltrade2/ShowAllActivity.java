package com.example.viltrade2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.viltrade2.Adapter.ShowAllAdapter;
import com.example.viltrade2.Adapter.categoryAdapter;
import com.example.viltrade2.models.NewProductsModel;
import com.example.viltrade2.models.categoryModels;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ShowAllActivity extends AppCompatActivity {
    RecyclerView recyclerView, catRecyclerView; // Menambahkan RecyclerView untuk kategori
    categoryAdapter categoryAdapter; // Menggunakan nama yang sesuai untuk adapter
    ShowAllAdapter showAllAdapter;
    List<NewProductsModel> newProductsModelList; // Menggunakan model yang sesuai
    List<categoryModels> categoryModelList; // Menggunakan model kategori yang sesuai
    FirebaseFirestore firestore;

    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_all);
        String type = getIntent().getStringExtra("type");

        firestore = FirebaseFirestore.getInstance();
        recyclerView = findViewById(R.id.show_all_rec);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        imageView = findViewById(R.id.backtoolbar);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), landing_page.class);
                startActivity(i);
                finish();
            }
        });

        catRecyclerView = findViewById(R.id.rec_category); // Inisialisasi RecyclerView untuk kategori
        catRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        // Inisialisasi adapter untuk kategori
        categoryModelList = new ArrayList<>();
        categoryAdapter = new categoryAdapter(this, categoryModelList);
        catRecyclerView.setAdapter(categoryAdapter);

        newProductsModelList = new ArrayList<>();
        showAllAdapter = new ShowAllAdapter(this, newProductsModelList); // Menggunakan adapter yang sesuai
        recyclerView.setAdapter(showAllAdapter);

        loadData(type);
        loadCategories(); // Panggil metode untuk memuat data kategori
    }

    private void loadData(String type) {
        Query query;
        if (type == null || type.isEmpty()) {
            query = firestore.collection("NewProducts");
        } else {
            query = firestore.collection("NewProducts").whereEqualTo("type", type);
        }

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult() != null) {
                        for (DocumentSnapshot doc : task.getResult().getDocuments()) {
                            NewProductsModel newProductsModel = doc.toObject(NewProductsModel.class);
                            if (newProductsModel != null) {
                                newProductsModelList.add(newProductsModel);
                                Toast.makeText(ShowAllActivity.this, "Data fetched: " + newProductsModel.getName(), Toast.LENGTH_SHORT).show();
                            }
                        }
                        showAllAdapter.notifyDataSetChanged();
                    }
                } else {
                    Toast.makeText(ShowAllActivity.this, "Error getting documents: " + task.getException(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // Metode untuk memuat data kategori
    private void loadCategories() {
        firestore.collection("Category").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult() != null) {
                        for (DocumentSnapshot doc : task.getResult().getDocuments()) {
                            categoryModels categoryModel = doc.toObject(categoryModels.class); // Gunakan model yang sesuai
                            if (categoryModel != null) {
                                categoryModelList.add(categoryModel);
                            }
                        }
                        categoryAdapter.notifyDataSetChanged();
                    }
                } else {
                    Toast.makeText(ShowAllActivity.this, "Error getting categories: " + task.getException(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
