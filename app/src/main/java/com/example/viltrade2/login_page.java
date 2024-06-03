package com.example.viltrade2;

import static com.example.viltrade2.register_page.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class login_page extends AppCompatActivity {
    TextView daftar, viltrade, lupaPass;
    Button btn;
    private EditText Lemail, Lpass;

    private FirebaseAuth fAuth;
    private FirebaseFirestore fStore;


    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login_page);

        // Inisialisasi FirebaseAuth
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        daftar = findViewById(R.id.regist);

        Lemail = findViewById(R.id.emailfield);
        Lpass = findViewById(R.id.passwordfield);
        btn = findViewById(R.id.lgnbtn);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = Lemail.getText().toString();
                String password = Lpass.getText().toString();

                if (TextUtils.isEmpty(email)) {
                    Lemail.setError("Email Masih Kosong");
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    Lpass.setError("Kata Sandi Masih Kosong");
                    return;
                }

                // Melakukan proses login dengan Firebase Authentication
                fAuth.signInWithEmailAndPassword(email, password)
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                Log.d(TAG, "Log In Sukses");
                                checkUserLevel(authResult.getUser().getUid());
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e(TAG, "Log In Gagal", e);
                                Toast.makeText(login_page.this, "Gagal masuk. Periksa kembali email dan kata sandi Anda.", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });




        daftar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), select_role.class);
                startActivity(i);
                finish();
            }
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
                }
                if(documentSnapshot.getString("isKonsumen")!=null){
                    startActivitySec();
                    finish();
                }
            }
        });
    }

    private void startActivitySec() {
        Intent i = new Intent(login_page.this, landing_page.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }

    @Override
    protected void onStart(){
        super.onStart();
        if(FirebaseAuth.getInstance().getCurrentUser()!=null){
            DocumentReference df = FirebaseFirestore.getInstance().collection("Users").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
            df.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.getString("isDistributor")!=null){
                    startActivity(new Intent(getApplicationContext(),DistributorActivity.class));
                    finish();

                }
                if(documentSnapshot.getString("isKonsumen")!=null){
                    startActivitySec();
                    finish();
                }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    FirebaseAuth.getInstance().signOut();
                    finish();
                }
            });
            finish();
        }
    }
}
