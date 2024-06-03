package com.example.viltrade2;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterKonsumen extends AppCompatActivity {

    TextView login, viltrade, role;
    EditText namapggln, namalngkp, rEmail, pass, pass2;
    Button btn;
    public static final String TAG = "TAG";

    private FirebaseAuth fAuth;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_konsumen);

        // Inisialisasi elemen UI
        role = findViewById(R.id.role_konsumen);
        btn = findViewById(R.id.registbtn2);
        namalngkp = findViewById(R.id.namalngkpbox2);
        namapggln = findViewById(R.id.namapggln2box);
        rEmail = findViewById(R.id.emailbox2);
        pass = findViewById(R.id.passwordbox2);
        pass2 = findViewById(R.id.repeatbox2);
        login = findViewById(R.id.login);
        viltrade = findViewById(R.id.viltrade2);

        fAuth = FirebaseAuth.getInstance();

        role.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), select_role.class);
                startActivity(i);
                finish();
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), login_page.class);
                startActivity(i);
            }
        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = rEmail.getText().toString();
                String password = pass.getText().toString();
                String confPassword = pass2.getText().toString();
                String fullName = namalngkp.getText().toString();
                String nickname = namapggln.getText().toString();

                if (TextUtils.isEmpty(email)) {
                    rEmail.setError("Email Kosong");
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    pass.setError("Kata Sandi kosong");
                    return;
                }
                if (TextUtils.isEmpty(confPassword) || !password.equals(confPassword)) {
                    pass2.setError("Konfirmasi kata sandi salah");
                    return;
                }
                if (TextUtils.isEmpty(fullName)) {
                    namalngkp.setError("Nama Lengkap Kosong");
                    return;
                }
                if (TextUtils.isEmpty(nickname)) {
                    namapggln.setError("Nama panggilan kosong");
                    return;
                }
                if (password.length() < 6) {
                    pass.setError("Kata sandi harus lebih dari 6 kata");
                    return;
                }

                fAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(RegisterKonsumen.this, "Berhasil dibuat", Toast.LENGTH_SHORT).show();
                            FirebaseUser fuser = fAuth.getCurrentUser();
                            if (fuser != null) {
                                saveUserData(fuser);
                            } else {
                                Log.d(TAG, "Pengguna tidak ditemukan setelah registrasi");
                            }
                        } else {
                            Toast.makeText(RegisterKonsumen.this, "Gagal membuat: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    private void saveUserData(FirebaseUser fuser) {
        DocumentReference df = db.collection("Users").document(fuser.getUid());

        Map<String, String> userInfo = new HashMap<>();
        userInfo.put("NamaLengkap", namalngkp.getText().toString());
        userInfo.put("NamaPanggilan", namapggln.getText().toString());
        userInfo.put("UserEmail", rEmail.getText().toString());
        userInfo.put("isKonsumen", "1");

        df.set(userInfo).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.d(TAG, "Data pengguna berhasil disimpan");
                startActivitySec();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "Gagal menyimpan data pengguna: " + e.getMessage());
            }
        });
    }

    private void startActivitySec() {
        Intent i = new Intent(RegisterKonsumen.this, login_page.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }
}
