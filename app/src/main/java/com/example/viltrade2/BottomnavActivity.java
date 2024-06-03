package com.example.viltrade2;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.example.viltrade2.fragment.CartFragment;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class BottomnavActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;

    FloatingActionButton fab;

    FirebaseAuth currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_bottomnav);

        currentUser = FirebaseAuth.getInstance();
        fab = findViewById(R.id.fab);





        bottomNavigationView = findViewById(R.id.bottview);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                String userLevel = currentUser.getCurrentUser().getUid();

                if (userLevel != null) {
                    DocumentReference df = FirebaseFirestore.getInstance().collection("User").document(userLevel);
                    df.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        Fragment fragment = null;
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if(documentSnapshot.getString("isDistributor")!=null){
                                if (menuItem.getItemId() == R.id.nav_home) {
                                    Intent i = new Intent(getApplicationContext(), DistributorActivity.class);
                                    startActivity(i);
                                    finish();

                                }else if(menuItem.getItemId()==R.id.nav_cart){
                                    fragment = new CartFragment();
                                    finish();


                                }
                            }if(documentSnapshot.getString("isKonsumen")!=null){
                                if(menuItem.getItemId()==R.id.nav_home) {
                                    Intent i = new Intent(getApplicationContext(), landing_page.class);
                                    startActivity(i);
                                    finish();
                                }else if(menuItem.getItemId()==R.id.nav_cart){
                                    fragment = new CartFragment();
                                    finish();
                                }

                            }
                        }
                    });


                }
                return true;
            }
        });

    }
}