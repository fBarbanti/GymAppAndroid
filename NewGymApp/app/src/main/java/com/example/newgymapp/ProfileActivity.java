package com.example.newgymapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

public class ProfileActivity extends AppCompatActivity {

    private TextView email;
    private TextView username;
    private TextView usernameOld;

    private ImageView btnBack;
    private ImageView btnModifyEmail;
    private ImageView btnModifyPassword;
    private ImageView btnModifyUsername;
    private ImageView btnModifyPhoto;

    private StorageReference storageReference;
    private DatabaseReference dbRef;

    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final long ONE_MEGABYTE = 1024 * 1024;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        email = findViewById(R.id.email_text_view_profile);
        username = findViewById(R.id.username_text_view_profile);
        usernameOld = findViewById(R.id.username_edit_text_view_profile);

        btnBack = findViewById(R.id.back_to_home_f_profile);
        btnModifyEmail = findViewById(R.id.modify_email_profile_button);
        btnModifyPassword = findViewById(R.id.modify_password_profile);
        btnModifyUsername = findViewById(R.id.modify_username_profile);
        btnModifyPhoto = findViewById(R.id.profile_image);

        FirebaseAuth fireAuth = FirebaseAuth.getInstance();
        FirebaseUser fireUser = fireAuth.getCurrentUser();

        // If user is not logged in
        if(fireUser == null) {
            startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
        }
        assert fireUser != null;
        dbRef = FirebaseDatabase.getInstance(FirebaseConfig.DB_URL).getReference("Users").child(fireUser.getUid());
        storageReference = FirebaseStorage.getInstance().getReference().child("profile_images").child(fireUser.getUid());

    }

    @Override
    protected void onStart() {
        super.onStart();
        updateData();
    }

    @Override
    protected void onResume() {
        super.onResume();

        btnBack.setOnClickListener(view -> startActivity(new Intent(ProfileActivity.this, HomeActivity.class)));

        btnModifyEmail.setOnClickListener(view -> {
            Intent intent = new Intent(ProfileActivity.this, ModifyEmail.class);
            startActivity(intent);
        });

        btnModifyPassword.setOnClickListener(view -> {
            Intent intent = new Intent(ProfileActivity.this, ModifyPassword.class);
            startActivity(intent);
        });

        btnModifyUsername.setOnClickListener(view -> {
            Intent intent = new Intent(ProfileActivity.this, ModifyUsername.class);
            startActivity(intent);
        });

        btnModifyPhoto.setOnClickListener(view -> loadImageFromMemory());

    }


    private void loadImageFromMemory(){
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        try{
            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
        }
        catch (ActivityNotFoundException e) {
            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            uploadImageToFirebase(imageBitmap);
        }
    }


    private void uploadImageToFirebase(Bitmap imageBitmap) {
        if(imageBitmap != null){
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

            byte[] data = baos.toByteArray();  // Convert output stream to byteArray
            UploadTask uploadTask = storageReference.putBytes(data);
            uploadTask.addOnFailureListener(exception -> Toast.makeText(ProfileActivity.this, "Image upload failed", Toast.LENGTH_SHORT).show()).addOnSuccessListener(taskSnapshot -> {
                Toast.makeText(ProfileActivity.this, "Image upload successful", Toast.LENGTH_SHORT).show();
                btnModifyPhoto.setImageBitmap(imageBitmap);

            });
        }
    }



    /**
     * Function that updates the data to show in the layout
     */
    private void updateData() {
        // Query user info
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserProfileData userData = snapshot.getValue(UserProfileData.class);
                assert userData != null;
                email.setText(userData.getEmail());
                username.setText(userData.getUsername());
                usernameOld.setText(userData.getUsername());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        // Download user profile photo
        updatePhoto();
    }

    public void updatePhoto(){
        storageReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(bytes -> {
            Bitmap customPhoto = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            btnModifyPhoto.setImageBitmap(customPhoto);
        }).addOnFailureListener(e -> {
            Bitmap defaultPhoto = BitmapFactory.decodeResource(getResources(), R.drawable.scimmia);
            btnModifyPhoto.setImageBitmap(defaultPhoto);
        });
    }
}