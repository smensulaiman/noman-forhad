package com.nomanforhad.finalproject.activities;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.nomanforhad.finalproject.R;
import com.nomanforhad.finalproject.models.StatusItem;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.util.concurrent.TimeUnit;

public class EditStatusActivity extends AppCompatActivity {

    private ImageView mImageView;
    private EditText mEditText;
    private FloatingActionButton mFab;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseStorage mFirebaseStorage;
    private DatabaseReference mStatusReference;
    private StorageReference mStatusStorageReference;

    private Uri imageSource;

    private String imageResourceFromString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_status);

        mImageView = findViewById(R.id.preview_image);
        mEditText = findViewById(R.id.et_caption);
        mFab = findViewById(R.id.fab_send_status);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseStorage = FirebaseStorage.getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        mStatusReference = mFirebaseDatabase.getReference().child("assignment");
        mStatusStorageReference = mFirebaseStorage.getReference().child("assignment").child(mFirebaseUser.getUid());

        imageSource = getIntent().getData();
        imageResourceFromString = getIntent().getStringExtra("file");

        if (imageSource == null) {
            Glide.with(this)
                    .load(imageResourceFromString)
                    .into(mImageView);
        } else if (imageResourceFromString == null) {
            Glide.with(this)
                    .load(imageSource)
                    .into(mImageView);
        }

        mFab.setOnClickListener(view -> sendMediaStatus());
    }

    private void sendMediaStatus() {
        final long timestamp = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());
        final long expireTime = timestamp + TimeUnit.MILLISECONDS.convert(1, TimeUnit.MINUTES);

        mImageView.setDrawingCacheEnabled(true);
        mImageView.buildDrawingCache();
        Bitmap bitmap = mImageView.getDrawingCache();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        final String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);

        final StorageReference mediaReference = mStatusStorageReference.child(imageSource.getLastPathSegment());
        mediaReference.putFile(imageSource).addOnSuccessListener(taskSnapshot -> mediaReference.getDownloadUrl().addOnSuccessListener(uri -> {
            DatabaseReference newRef = mStatusReference.child(mFirebaseUser.getUid()).child("statusItem").push();
            mStatusReference.child(mFirebaseUser.getUid()).child("uid").setValue(mFirebaseUser.getUid());
            StatusItem statusItem = new StatusItem(newRef.getKey(), "image", uri.toString(), mEditText.getText().toString(), timestamp, expireTime, encoded, null);
            newRef.setValue(statusItem);
            finish();
        }));
    }
}
