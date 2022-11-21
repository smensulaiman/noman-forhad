package com.nomanforhad.finalproject;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.nomanforhad.finalproject.databinding.ActivityCreateProfileBinding;
import com.nomanforhad.finalproject.models.User;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import de.hdodenhof.circleimageview.CircleImageView;

public class CreateProfileActivity extends AppCompatActivity {

    private static final int RC_PHOTO_PICKER = 2;

    private ActivityCreateProfileBinding binding;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mUserReference;
    private FirebaseUser mFirebaseUser;
    private FirebaseStorage mFirebaseStorage;
    private StorageReference mStorageReference;

    private Button mBtNext;
    private TextInputLayout mEtUsername;
    private CircleImageView mImgAvatar;
    private ProgressBar mProgressBar;
    private Uri selectedImage;
    private Uri storageAvatar;
    private Context mContext;
    private String defaultProfileAbout = "Hello there! I am using .";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreateProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mBtNext = findViewById(R.id.bt_next_main);
        mEtUsername = findViewById(R.id.txt_username);
        mImgAvatar = findViewById(R.id.img_avatar_create);
        mProgressBar = findViewById(R.id.progressbar_create_profile);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseStorage = FirebaseStorage.getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

        mUserReference = mFirebaseDatabase.getReference().child("users").child(mFirebaseUser.getUid());
        mStorageReference = mFirebaseStorage.getReference().child("avatar").child(mFirebaseUser.getUid());

        mImgAvatar.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/jpeg");
            intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
            startActivityForResult(Intent.createChooser(intent, "Complete action using"), RC_PHOTO_PICKER);
        });

        mBtNext.setOnClickListener(view -> {
            mProgressBar.setVisibility(View.VISIBLE);
            createUserData();
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_PHOTO_PICKER && resultCode == RESULT_OK) {
            if (data != null) {
                selectedImage = data.getData();
                mImgAvatar.setVisibility(View.VISIBLE);
                Glide.with(this)
                        .load(selectedImage)
                        .into(mImgAvatar);
            } else {
                Toast.makeText(this, "data is null", Toast.LENGTH_SHORT).show();
            }

        } else {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
        }
    }

    private void createUserData() {
        mUserReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    StorageReference photoRef;
                    try {
                        photoRef = mStorageReference.child(selectedImage.getLastPathSegment());
                    } catch (Exception e) {
                        Toast.makeText(CreateProfileActivity.this, "Photo Cannot be empty", Toast.LENGTH_SHORT).show();
                        mProgressBar.setVisibility(View.GONE);
                        return;
                    }
                    photoRef.putFile(selectedImage).addOnSuccessListener(taskSnapshot -> photoRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        storageAvatar = uri;
                        String username = mEtUsername.getEditText().getText().toString();

                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                .setDisplayName(username)
                                .setPhotoUri(uri)
                                .build();

                        mFirebaseUser.updateProfile(profileUpdates).addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Intent intent = new Intent(CreateProfileActivity.this, RoomListActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        });

                        User user = new User();
                        user.setUid(mFirebaseUser.getUid());
                        user.setUsername(mEtUsername.getEditText().getText().toString());
                        user.setPassword(((TextInputLayout) findViewById(R.id.txt_password)).getEditText().getText().toString());
                        user.setAbout(defaultProfileAbout);
                        user.setLastSeen(0);

                        switch (binding.radioGroupUserType.getCheckedRadioButtonId()) {
                            case R.id.btn_teacher:
                                user.setUserType(User.UserType.TEACHER.name());
                                break;
                            default:
                                user.setUserType(User.UserType.STUDENT.name());
                                break;
                        }
                        user.setOnline(true);
                        user.setPhone(mFirebaseUser.getPhoneNumber());
                        user.setPhotoUrl(uri.toString());

                        mUserReference.setValue(user);
                        mProgressBar.setVisibility(View.GONE);

                    }));
                } else {
                    Toast.makeText(CreateProfileActivity.this,
                            "This user already exists", Toast.LENGTH_SHORT).show();
                    mProgressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(CreateProfileActivity.this, "Error : " + databaseError.getMessage(), Toast.LENGTH_LONG).show();
                mProgressBar.setVisibility(View.GONE);
            }
        });
    }
}
