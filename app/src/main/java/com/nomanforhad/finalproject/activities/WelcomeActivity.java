package com.nomanforhad.finalproject.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.nomanforhad.finalproject.R;

public class WelcomeActivity extends AppCompatActivity {

    private FirebaseAuth mFirebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        Button mBtAgreeTos = findViewById(R.id.btn_agree_tos);

        mFirebaseAuth = FirebaseAuth.getInstance();

        mBtAgreeTos.setOnClickListener(view -> {
            Intent intent = new Intent(WelcomeActivity.this, InputPhoneNumberActivity.class);
            startActivity(intent);
            finish();
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mFirebaseAuth.getCurrentUser() != null) {
            findViewById(R.id.btn_agree_tos).setVisibility(View.GONE);
            new Handler().postDelayed(() -> onAuthSuccess(), 1000);
        }
    }

    private void onAuthSuccess() {
        startActivity(new Intent(this, RoomListActivity.class));
        finish();
    }
}
