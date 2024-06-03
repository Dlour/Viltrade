package com.example.viltrade2;

import static com.example.viltrade2.register_page.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


public class SplashScreen extends AppCompatActivity {

    FirebaseAuth firebaseAuth;

    FirebaseFirestore fStore;
    FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        firebaseAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(firebaseAuth.getCurrentUser()!=null) {
                    checkUserLevel(firebaseAuth.getCurrentUser().getUid());
                }else{
                    startActivity(new Intent(getApplicationContext(),landing_page.class));
                    finish();
                }
            }
        },1500);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash_screen);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    private void checkUserLevel(String uid) {
        DocumentReference df = fStore.collection("Users").document(uid);
        df.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Log.d(TAG,"onSuccses: "+documentSnapshot.getData());
                if(documentSnapshot.getString("isDistributor")!=null){
                    startActivity(new Intent(getApplicationContext(),DistributorActivity.class));
                    finish();
                } if(documentSnapshot.getString("isKonsumen")!=null){
                    startActivity(new Intent(getApplicationContext(),landing_page.class));
                    finish();
                }
            }
        });
    }

}