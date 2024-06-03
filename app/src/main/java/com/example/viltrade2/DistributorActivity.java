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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class DistributorActivity extends AppCompatActivity {

    TextView tx;
    Fragment homeFragment;
    EditText search;

    ImageView profile;

    TextView catShowAll, newProductShowAll;

    FirebaseFirestore db;

    RecyclerView catRecyclerview, newProductRecyclerview;

    categoryAdapter CategoryAdapter;

    List<categoryModels> categoryModelsList;
    NewProductsAdapter newProductsAdapter;
    List<NewProductsModel> newProductsModelList;

    List<ShowAllModel> showAllModelList;

    ShowAllAdapter showAllAdapter;
    BottomNavigationView bottomNavigationView;

    FloatingActionButton fabDisbt, addBarang, lihatBarang;

    Animation rotateOpen, rotateClose, fromBottom, toBottom;

    FloatingActionButton fab;

    private boolean clicked = false;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_distributor);

        tx = findViewById(R.id.logout);
        search = findViewById(R.id.searchbar);
        profile = findViewById(R.id.profile);
        fabDisbt = findViewById(R.id.fabdistry);
        addBarang = findViewById(R.id.addbarang);
        lihatBarang = findViewById(R.id.lihatBarang);

        addBarang.setVisibility(View.INVISIBLE);
        lihatBarang.setVisibility(View.INVISIBLE);
        addBarang.setClickable(false);
        lihatBarang.setClickable(false);

        rotateClose = AnimationUtils.loadAnimation(this, R.anim.rotate_close_anim);
        rotateOpen = AnimationUtils.loadAnimation(this, R.anim.rotate_open_anim);
        fromBottom = AnimationUtils.loadAnimation(this, R.anim.from_bottom_anim);
        toBottom = AnimationUtils.loadAnimation(this, R.anim.to_bottom_anim);

        fabDisbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onAddButtonClicked();
            }
        });

        addBarang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), AddBarangActivity.class);
                startActivity(i);
                finish();
            }
        });

        rotateClose = AnimationUtils.loadAnimation(this, R.anim.rotate_close_anim);
        rotateOpen = AnimationUtils.loadAnimation(this, R.anim.rotate_open_anim);
        fromBottom = AnimationUtils.loadAnimation(this, R.anim.from_bottom_anim);
        toBottom = AnimationUtils.loadAnimation(this, R.anim.to_bottom_anim);

        db = FirebaseFirestore.getInstance();

        newProductRecyclerview = findViewById(R.id.new_product_rec);
        catRecyclerview = findViewById(R.id.rec_category);
        catRecyclerview.setLayoutManager(new LinearLayoutManager(getApplicationContext(), RecyclerView.HORIZONTAL, false));

        // Inisialisasi daftar sebelum digunakan
        categoryModelsList = new ArrayList<>();
        CategoryAdapter = new categoryAdapter(getApplicationContext(), categoryModelsList);
        catRecyclerview.setAdapter(CategoryAdapter);

        catShowAll = findViewById(R.id.category_see_all);
        newProductShowAll = findViewById(R.id.newProducts_see_all);

        newProductRecyclerview.setLayoutManager(new LinearLayoutManager(getApplicationContext(), RecyclerView.HORIZONTAL, false));

        // Inisialisasi daftar sebelum digunakan
        newProductsModelList = new ArrayList<>();
        newProductsAdapter = new NewProductsAdapter(getApplicationContext(), newProductsModelList);
        newProductRecyclerview.setAdapter(newProductsAdapter);

        // Inisialisasi daftar sebelum digunakan
        showAllModelList = new ArrayList<>();
        showAllAdapter = new ShowAllAdapter(this, newProductsModelList);

        String type = getIntent().getStringExtra("type");
        loadData(type);

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

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProfileFragment profileFragment = new ProfileFragment();
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

                fragmentTransaction.replace(R.id.fragment_profile, profileFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), searchActivoty.class));
            }
        });

        tx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), login_page.class);
                startActivity(i);
                finish();
            }
        });

        bottomNavigationView = findViewById(R.id.bottview);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            Fragment fragment = null;

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.nav_home) {
                    Intent i = new Intent(getApplicationContext(), DistributorActivity.class);
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
    }

    private void onAddButtonClicked() {
        setVisibility(clicked);
        setAnimation(clicked);
        setClickable(clicked);
        clicked = !clicked;
    }

    private void setAnimation(Boolean clicked) {
        if (!clicked) {
            addBarang.startAnimation(fromBottom);
            lihatBarang.startAnimation(fromBottom);
            fabDisbt.startAnimation(rotateOpen);
        } else {
            addBarang.startAnimation(toBottom);
            lihatBarang.startAnimation(toBottom);
            fabDisbt.startAnimation(rotateClose);
        }
    }

    private void setClickable(Boolean clicked) {
        if (!clicked) {
            addBarang.setClickable(true);
            lihatBarang.setClickable(true);
        } else {
            addBarang.setClickable(false);
            lihatBarang.setClickable(false);
        }
    }

    private void setVisibility(Boolean clicked) {
        if (!clicked) {
            addBarang.setVisibility(View.VISIBLE);
            lihatBarang.setVisibility(View.VISIBLE);
        } else {
            addBarang.setVisibility(View.INVISIBLE);
            lihatBarang.setVisibility(View.INVISIBLE);
        }
    }

    private void loadData(String type) {
        Query query;
        if (type == null || type.isEmpty()) {
            query = db.collection("ShowAll");
        } else {
            query = db.collection("ShowAll").whereEqualTo("type", type);
        }

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult() != null) {
                        for (DocumentSnapshot doc : task.getResult().getDocuments()) {
                            ShowAllModel showAllModel = doc.toObject(ShowAllModel.class);
                            if (showAllModel != null) {
                                showAllModelList.add(showAllModel);
                                Toast.makeText(getApplicationContext(), "Data fetched: " + showAllModel.getName(), Toast.LENGTH_SHORT).show();
                            }
                        }
                        showAllAdapter.notifyDataSetChanged();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Error getting documents: " + task.getException(), Toast.LENGTH_SHORT).show();
                }
            }
        });
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

    boolean inUser() {
        return FirebaseAuth.getInstance().getCurrentUser() != null;
    }

}
