package com.example.viltrade2;

import static com.example.viltrade2.register_page.TAG;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.viltrade2.Adapter.NewProductsAdapter;
import com.example.viltrade2.models.NewProductsModel;
import com.example.viltrade2.models.categoryModels;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.example.viltrade2.Adapter.categoryAdapter;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


public class appBar extends AppCompatActivity {

    TextView catShowAll,newProductShowAll;

    FirebaseFirestore db;

    RecyclerView catRecyclerview,newProductRecyclerview;

    categoryAdapter CategoryAdapter;

    List<categoryModels> categoryModelsList;
    NewProductsAdapter newProductsAdapter;
    List<NewProductsModel> newProductsModelList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_app_bar);

        db = FirebaseFirestore.getInstance();

        newProductRecyclerview = findViewById(R.id.new_product_rec);
        catRecyclerview = findViewById(R.id.rec_category);
        catRecyclerview.setLayoutManager(new LinearLayoutManager(getApplicationContext(), RecyclerView.HORIZONTAL, false));
        categoryModelsList = new ArrayList<>();
        CategoryAdapter = new categoryAdapter(getApplicationContext(), categoryModelsList);
        catRecyclerview.setAdapter(CategoryAdapter);
        catShowAll = findViewById(R.id.category_see_all);
        newProductShowAll = findViewById(R.id.newProducts_see_all);

        catShowAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), ShowAllActivity.class);
                startActivity(i);
            }
        });
        newProductShowAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), ShowAllActivity.class);
                startActivity(i);
            }
        });
        Log.d(TAG, "Fetching categories from Firestore...");
        db.collection("Category")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Fetch successful");
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                categoryModels categoryModel = document.toObject(categoryModels.class);
                                categoryModelsList.add(categoryModel);
                                CategoryAdapter.notifyDataSetChanged();
                            }
                        } else {
                            Log.e(TAG, "Fetch failed", task.getException());
                        }
                    }
                });

        newProductRecyclerview.setLayoutManager(new LinearLayoutManager(getApplicationContext(),RecyclerView.HORIZONTAL,false));
        newProductsModelList = new ArrayList<>();
        newProductsAdapter = new NewProductsAdapter(getApplicationContext(),newProductsModelList);
        newProductRecyclerview.setAdapter(newProductsAdapter);


        db.collection("NewProducts")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Fetch successful for new products");
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                NewProductsModel newProductModel = document.toObject(NewProductsModel.class);
                                newProductsModelList.add(newProductModel);
                            }
                            // Setelah data dimuat, kemudian set adapter
                            newProductsAdapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(getApplicationContext(), "Error: " + task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}