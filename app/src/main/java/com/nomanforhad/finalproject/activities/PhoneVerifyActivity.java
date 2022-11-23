package com.nomanforhad.finalproject.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nomanforhad.finalproject.R;

import java.util.concurrent.TimeUnit;

public class PhoneVerifyActivity extends AppCompatActivity {

    private String phoneNumber;
    private String verificationId;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;

    private EditText mEtCode;
    private Button mBtNext;
    private TextView mTvWaiting;
    private Toolbar mToolbar;
    private TextView mToolbarTitle;
    private TextView mTvCountdownSMS;

    private ProgressDialog mProgressDialog;

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallback = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            if(mProgressDialog.isShowing()){
                mProgressDialog.dismiss();
            }
            mBtNext.setVisibility(View.VISIBLE);
            verificationId = s;
        }

        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            String code = phoneAuthCredential.getSmsCode();
            if (code != null) {
                mEtCode.setText(code);
                showProgressDialog(code);
            }
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(PhoneVerifyActivity.this, "Something went wrong: " + e.getMessage(), Toast.LENGTH_LONG).show();
            if(mProgressDialog.isShowing()){
                mProgressDialog.dismiss();
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_verify);

        phoneNumber = getIntent().getStringExtra("phonenumber");

        mEtCode = findViewById(R.id.et_verification_code);
        mBtNext = findViewById(R.id.bt_next_main);
        mTvWaiting = findViewById(R.id.tv_wating_text);
        mToolbarTitle = findViewById(R.id.toolbar_title_input_number);
        mTvCountdownSMS = findViewById(R.id.tv_countdown_sms);

        String test = getString(R.string.waiting_sms, phoneNumber);
        mTvWaiting.setText(test);
        mToolbarTitle.setText("Verify " + phoneNumber);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.show();
        mBtNext.setVisibility(View.GONE);

        new Handler(Looper.getMainLooper()).post(() -> sendVerificationCode(phoneNumber));

        mBtNext.setOnClickListener(view -> {
            String code = mEtCode.getText().toString();
            showProgressDialog(code);
        });
    }

    private void verifyCode(String code) {
        try {
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
            signInWithCredential(credential);
        } catch (Exception e) {
            e.printStackTrace();
            if(mProgressDialog.isShowing()){
                mProgressDialog.dismiss();
            }
            Toast.makeText(this, "Verification code wrong", Toast.LENGTH_SHORT).show();
        }
    }

    private void signInWithCredential(PhoneAuthCredential credential) {
        mFirebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if(mProgressDialog.isShowing()){
                            mProgressDialog.dismiss();
                        }
                        mFirebaseDatabase.getReference("users").child(mFirebaseAuth.getUid())
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    Intent intent = new Intent(PhoneVerifyActivity.this, RoomListActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Intent intent = new Intent(PhoneVerifyActivity.this, CreateProfileActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    } else {
                        if(mProgressDialog.isShowing()){
                            mProgressDialog.dismiss();
                        }
                        Toast.makeText(PhoneVerifyActivity.this, "Cannot verify : " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void sendVerificationCode(String number) {
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(mFirebaseAuth)
                .setPhoneNumber(number)       // Phone number to verify
                .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                .setActivity(this)                 // Activity (for callback binding)
                .setCallbacks(mCallback)          // OnVerificationStateChangedCallbacks
                .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void showProgressDialog(final String code) {
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMessage("Verifying");
        mProgressDialog.show();
        verifyCode(code);
    }
}
