package com.nomanforhad.finalproject;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterViewFlipper;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.nomanforhad.finalproject.adapters.StatusFlipperAdapter;
import com.nomanforhad.finalproject.models.StatusItem;
import com.nomanforhad.finalproject.utils.Utility;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ShowMyStatusActivity extends AppCompatActivity {

    private AdapterViewFlipper mAdapterViewFlipper;
    private Toolbar mToolbar;
    private CircleImageView mAvatar;
    private TextView mUsername;
    private TextView mTime;
    private ProgressBar[] mProgressBar;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference mStatusReference;

    private List<StatusItem> statusItemList = new ArrayList<>();

    private int flipperCount;
    private ObjectAnimator animation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_my_status);

        mAdapterViewFlipper = findViewById(R.id.my_status_view_flipper);
        mAvatar = findViewById(R.id.my_status_avatar);
        mUsername = findViewById(R.id.my_status_username);
        mTime = findViewById(R.id.my_status_time);
        mToolbar = findViewById(R.id.my_status_toolbar);

        FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

        mStatusReference = mFirebaseDatabase.getReference().child("assignment");

        mToolbar.bringToFront();

        getMyStatus();

    }

    private void getMyStatus() {
        mStatusReference.child(mFirebaseUser.getUid()).child("statusItem").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    StatusItem statusItem = snapshot.getValue(StatusItem.class);
                    statusItemList.add(statusItem);
                    StatusFlipperAdapter adapter = new StatusFlipperAdapter(getApplicationContext(), statusItemList);
                    mAdapterViewFlipper.setAdapter(adapter);
                    mAdapterViewFlipper.setFlipInterval(5000);
                    flipperCount = mAdapterViewFlipper.getCount();
                    mAdapterViewFlipper.startFlipping();
                }
                mProgressBar = new ProgressBar[flipperCount];
                for (int i = 0; i < flipperCount; i++) {
                    mProgressBar[i] = new ProgressBar(getApplicationContext(), null, android.R.attr.progressBarStyleHorizontal);
                    mProgressBar[i].setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
                    Utility.setMargins(mProgressBar[i]);
                    Utility.setProgressMax(mProgressBar[i]);
                    mProgressBar[i].setMax(100 * 100);
                    mProgressBar[i].getProgress();
                    ViewGroup mViewGroup = findViewById(R.id.my_status_parent_progressbar);
                    mViewGroup.addView(mProgressBar[i]);
                    mAdapterViewFlipper.addOnLayoutChangeListener((view, i12, i1, i2, i3, i4, i5, i6, i7) -> {
                        setProgressAnimate(mProgressBar[mAdapterViewFlipper.getDisplayedChild()]);
                        StatusItem statusItem = statusItemList.get(mAdapterViewFlipper.getDisplayedChild());
                        if (mAdapterViewFlipper.getDisplayedChild() == mAdapterViewFlipper.getCount() - 1) {
                            mAdapterViewFlipper.stopFlipping();
                            animation.addListener(new Animator.AnimatorListener() {
                                @Override
                                public void onAnimationStart(Animator animator) {

                                }

                                @Override
                                public void onAnimationEnd(Animator animator) {
                                    finish();
                                }

                                @Override
                                public void onAnimationCancel(Animator animator) {

                                }

                                @Override
                                public void onAnimationRepeat(Animator animator) {

                                }
                            });
                        }
                        animation.start();
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setProgressAnimate(ProgressBar pb) {
        animation = ObjectAnimator.ofInt(pb, "progress", pb.getProgress(), 100 * 100);
        animation.setDuration(5000);
        animation.setInterpolator(new LinearInterpolator());
    }

    private void setMargins(View view) {
        if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            p.setMargins(0, 0, 8, 0);
            view.requestLayout();
        }
    }

}
