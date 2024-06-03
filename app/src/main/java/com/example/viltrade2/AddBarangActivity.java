package com.example.viltrade2;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.viltrade2.models.NewProductsModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AddBarangActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageView addImage,backimage;
    private Uri imageUri;

    private EditText tambahNama, tambahKategori, tambahDeskripsi, tambahHarga, tambahStok;
    private Button tambahButton;

    private DatabaseReference realtimeDatabaseReference;
    private StorageReference storageReference;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_barang);

        // Initialize Firebase references
        realtimeDatabaseReference = FirebaseDatabase.getInstance().getReference("products");
        storageReference = FirebaseStorage.getInstance().getReference("product_images");
        firestore = FirebaseFirestore.getInstance();

        addImage = findViewById(R.id.add_image);
        tambahNama = findViewById(R.id.tambah_nama);
        tambahKategori = findViewById(R.id.tambahKategori);
        tambahDeskripsi = findViewById(R.id.tambahDeskripsi);
        tambahHarga = findViewById(R.id.tambahHarga);
        tambahStok = findViewById(R.id.tambahStok);
        tambahButton = findViewById(R.id.tambahButton);
        backimage = findViewById(R.id.backtambah);

        backimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),DistributorActivity.class));
                finish();
            }
        });

        FrameLayout tambahGambarFrame = findViewById(R.id.tambahGambar);
        tambahGambarFrame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        tambahButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imageUri != null) {
                    uploadImageAndSaveData();
                } else {
                    Toast.makeText(AddBarangActivity.this, "Please select an image", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                addImage.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadImageAndSaveData() {
        if (imageUri != null) {
            final StorageReference fileReference = storageReference.child(System.currentTimeMillis() + ".jpg");

            fileReference.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()) {
                        fileReference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                if (task.isSuccessful()) {
                                    Uri downloadUri = task.getResult();
                                    saveData(downloadUri.toString());
                                } else {
                                    Toast.makeText(AddBarangActivity.this, "Failed to get download URL", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else {
                        Toast.makeText(AddBarangActivity.this, "Image upload failed", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void saveData(String imageUrl) {
        String nama = tambahNama.getText().toString().trim();
        String kategori = tambahKategori.getText().toString().trim();
        String deskripsi = tambahDeskripsi.getText().toString().trim();
        String harga = tambahHarga.getText().toString().trim();
        String stok = tambahStok.getText().toString().trim();


        if (nama.isEmpty() || kategori.isEmpty() || deskripsi.isEmpty() || harga.isEmpty() || stok.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        String productId = realtimeDatabaseReference.push().getKey();
        NewProductsModel newProduct = new NewProductsModel(productId, deskripsi, nama, "0", imageUrl, Integer.parseInt(harga),kategori);

        // Save to Realtime Database
        realtimeDatabaseReference.child(productId).setValue(newProduct).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (!task.isSuccessful()) {
                    Toast.makeText(AddBarangActivity.this, "Failed to add product to Realtime Database", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Save to Firestore
        Map<String, Object> productMap = new HashMap<>();
        productMap.put("productId", productId);
        productMap.put("description", deskripsi);
        productMap.put("name", nama);
        productMap.put("rating", "0");
        productMap.put("img_url", imageUrl);
        productMap.put("type",kategori);
        productMap.put("price", Double.parseDouble(harga));

        firestore.collection("NewProducts").document(productId).set(productMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(AddBarangActivity.this, "Product added successfully", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(),DistributorActivity.class));
                    finish();
                } else {
                    Toast.makeText(AddBarangActivity.this, "Failed to add product to Firestore", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
