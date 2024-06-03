package com.example.viltrade2.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.viltrade2.DistributorActivity;
import com.example.viltrade2.R;
import com.example.viltrade2.SplashScreen;
import com.example.viltrade2.landing_page;
import com.example.viltrade2.models.usersModels;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class ProfileFragment extends Fragment {

    ImageView profile;
    EditText usernameInp;
    Button updateprof;
    ProgressBar progressBar;

    TextView logout;

    usersModels currentUserModel;
    private FirebaseFirestore fstore;
    ActivityResultLauncher<Intent> imagePick;

    FirebaseStorage storage;
    StorageReference storageReference;

    Uri selectedImageUri;


    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imagePick = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null && data.getData() != null) {
                            selectedImageUri = data.getData();
                            setProfilePic(getContext(),selectedImageUri,profile);

                        }
                    }
                }
        );

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
    }


    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        profile = view.findViewById(R.id.foto_profile);
        usernameInp = view.findViewById(R.id.username);
        updateprof = view.findViewById(R.id.updateProfile);
        progressBar = view.findViewById(R.id.progress);
        logout = view.findViewById(R.id.logoutbtn);
        fstore = FirebaseFirestore.getInstance();


        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent i = new Intent(getContext(), SplashScreen.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);

            }
        });

        profile.setOnClickListener(v -> {
            ImagePicker.with(this).compress(512).maxResultSize(512,512)
                    .createIntent(new Function1<Intent, Unit>() {
                        @Override
                        public Unit invoke(Intent intent) {
                            imagePick.launch(intent);
                            return null;
                        }
                    });
        });
        updateprof.setOnClickListener(v -> {
            setUpdateprof();




        });
        getUserData();
        return view;
    }

    void setUpdateprof(){
        String newUsername = usernameInp.getText().toString();
        if(newUsername.isEmpty()||newUsername.length()<3){
            usernameInp.setError("Harus Lebih dari 3 karakter");
            return;
        }
        currentUserModel.setNamaLengkap(newUsername);
        setInProgress(true);
        if(selectedImageUri!=null){
            getCurrentProfilePicStorageRef().putFile(selectedImageUri)
                    .addOnCompleteListener(task -> {
                        updateToFirestore();
                    });
        }
        else{
            updateToFirestore();
        }



    }
    void updateToFirestore(){
        currentUserDetails().set(currentUserModel)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        setInProgress(false);
                        if(task.isSuccessful()){
                            Toast.makeText(getContext(), "Update Berhasil", Toast.LENGTH_SHORT).show();
                            checkLevel(currentUserId());

                        }else{
                            Toast.makeText(getContext(),"Update Gagal",Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }
    void getUserData(){
        setInProgress(true);
        getCurrentProfilePicStorageRef().getDownloadUrl()
                        .addOnCompleteListener(task -> {
                           if(task.isSuccessful()){
                               Uri uri = task.getResult();
                               setProfilePic(getContext(),uri,profile);
                           }
                        });
        currentUserDetails().get().addOnCompleteListener(task -> {
            setInProgress(false);
            currentUserModel = task.getResult().toObject(usersModels.class);
            usernameInp.setText(currentUserModel.getNamaLengkap());
        });
    }
    public static DocumentReference currentUserDetails(){
        return FirebaseFirestore.getInstance().collection("Users").document(currentUserId());
    }
    public static String currentUserId(){
        return FirebaseAuth.getInstance().getUid();
    }
    void setInProgress(boolean inProgress){
        if(inProgress){
            progressBar.setVisibility(View.VISIBLE);
            updateprof.setVisibility(View.GONE);
        }else{
            progressBar.setVisibility(View.GONE);
            updateprof.setVisibility(View.VISIBLE);
        }
    }
    public static void setProfilePic (Context context, Uri imageUri, ImageView imageView){
        Glide.with(context).load(imageUri).apply(RequestOptions.circleCropTransform()).into(imageView);
    }
    public static StorageReference getCurrentProfilePicStorageRef(){
        return FirebaseStorage.getInstance().getReference().child("profile_pic")
                .child(currentUserId());
    }

    private void checkLevel(String uid){
        DocumentReference df = fstore.collection("Users").document(uid);
        df.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.getString("isDistributor")!=null){
                    startActivity(new Intent(getContext(), DistributorActivity.class));
                }
                if(documentSnapshot.getString("isKonsumen")!=null){
                    startActivity(new Intent(getContext(), landing_page.class));
                }
            }
        });
    }

}