package com.example.viltrade2;

import static com.example.viltrade2.register_page.TAG;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.viltrade2.Adapter.NewProductsAdapter;
import com.example.viltrade2.Adapter.ShowAllAdapter;
import com.example.viltrade2.Adapter.categoryAdapter;
import com.example.viltrade2.fragment.CartFragment;
import com.example.viltrade2.fragment.ProfileFragment;
import com.example.viltrade2.models.NewProductsModel;
import com.example.viltrade2.models.ShowAllModel;
import com.example.viltrade2.models.categoryModels;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class landing_page extends AppCompatActivity {

    RecyclerView catRecyclerview, newProductRecyclerview;
    TextView catShowAll, newProductShowAll;

    categoryAdapter CategoryAdapter;
    Fragment homeFragment;

    EditText search;
    categoryAdapter categoryAdapter;

    ImageView profile;

    TextView logout;
    NewProductsAdapter newProductsAdapter;
    List<NewProductsModel> newProductsModelList;

    FloatingActionButton fab;

    FirebaseFirestore db;
    List<categoryModels> categoryModelsList;

    BottomNavigationView bottomNavigationView;
    List<ShowAllModel> showAllModelList;
    ShowAllAdapter showAllAdapter;

    private static final String TAG = "LandingPage";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_landing_page);

        search = findViewById(R.id.searchbar);
        fab = findViewById(R.id.fab);
        profile = findViewById(R.id.profile);
        logout = findViewById(R.id.lokasi);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), searchActivoty.class);
                startActivity(i);
            }
        });

        bottomNavigationView = findViewById(R.id.bottview);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            Fragment fragment = null;

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.nav_home) {
                    Intent i = new Intent(getApplicationContext(), landing_page.class);
                    startActivity(i);
                }
                if (menuItem.getItemId() == R.id.nav_cart) {
                    fragment = new CartFragment();
                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.home_fragment, fragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }
                if (menuItem.getItemId() == R.id.nav_profile) {
                    ProfileFragment profileFragment = new ProfileFragment();
                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.home_fragment, profileFragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }
                return true;
            }
        });

        db = FirebaseFirestore.getInstance();

        newProductRecyclerview = findViewById(R.id.new_product_rec);
        newProductRecyclerview.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));

        // Inisialisasi dan set adapter untuk produk baru
        newProductsModelList = new ArrayList<>();
        newProductsAdapter = new NewProductsAdapter(this, newProductsModelList);
        newProductRecyclerview.setAdapter(newProductsAdapter);

        // Ambil data produk baru dari Firestore
        Log.d(TAG, "Starting to fetch new products...");
        db.collection("NewProducts").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        NewProductsModel newProductModel = document.toObject(NewProductsModel.class);
                        Log.d(TAG, "New Product: " + newProductModel.getName() + ", Type: " + newProductModel.getType());
                        newProductsModelList.add(newProductModel);
                    }
                    Log.d(TAG, "Fetched " + newProductsModelList.size() + " new products.");
                    newProductsAdapter.notifyDataSetChanged();
                } else {
                    Log.e(TAG, "Error fetching new products", task.getException());
                    Toast.makeText(getApplicationContext(), "Error: " + task.getException(), Toast.LENGTH_SHORT).show();
                }
            }
        });




        // Inisialisasi untuk kategori produk
        catRecyclerview = findViewById(R.id.rec_category);
        catRecyclerview.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        categoryModelsList = new ArrayList<>();
        CategoryAdapter = new categoryAdapter(this, categoryModelsList);
        catRecyclerview.setAdapter(CategoryAdapter);

        // Ambil data kategori dari Firestore
        db.collection("Category").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        categoryModels categoryModel = document.toObject(categoryModels.class);
                        categoryModelsList.add(categoryModel);
                    }
                    CategoryAdapter.notifyDataSetChanged();
                } else {
                    Log.e(TAG, "Fetch failed", task.getException());
                }
            }
        });

        // Implementasikan klik listener dan lainnya
        // ...

        StorageReference profileRef = getCurrentProfileStorageRef();
        if (profileRef != null) {
            profileRef.getDownloadUrl().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Uri uri = task.getResult();
                    setProfilePic(getApplicationContext(), uri, profile);
                } else {
                    Log.e(TAG, "Failed to get download URL", task.getException());
                }
            });
        } else {
            Log.e(TAG, "Profile reference is null");
        }

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), searchActivoty.class));
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), login_page.class);
                FirebaseAuth.getInstance().signOut();
                startActivity(i);
                finish();
            }
        });

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (inUser()) {
                    ProfileFragment profileFragment = new ProfileFragment();
                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.home_fragment, profileFragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                } else {
                    startActivity(new Intent(getApplicationContext(), login_page.class));
                    finish();
                }
            }
        });
    }

    boolean inUser() {
        return FirebaseAuth.getInstance().getCurrentUser() != null;
    }

    public static StorageReference getCurrentProfileStorageRef() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            return FirebaseStorage.getInstance().getReference().child("profile_pic").child(currentUser.getUid());
        } else {
            return null;
        }
    }

    public static void setProfilePic(Context context, Uri imageUri, ImageView imageView) {
        Glide.with(context).load(imageUri).apply(RequestOptions.circleCropTransform()).into(imageView);
    }
}

