package com.nomanforhad.finalproject.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.nomanforhad.finalproject.R;
import com.nomanforhad.finalproject.adapters.TabAdapter;
import com.nomanforhad.finalproject.fragments.AssignmentsTabFragment;
import com.nomanforhad.finalproject.fragments.ChatTabFragment;
import com.nomanforhad.finalproject.fragments.ProfileFragment;
import com.nomanforhad.finalproject.models.Assignment;
import com.nomanforhad.finalproject.models.Room;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final int RC_PHOTO_PICKER = 2;

    private TabAdapter mTabAdapter;
    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    private FloatingActionButton mFabBottom;
    private FloatingActionButton mFabTop;
    private Toolbar mToolbar;
    private AppBarLayout mAppBarLayout;

    private FloatingActionButton mFab;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference userReference;
    private DatabaseReference fileRef;
    private Room currentRoom;
    private String currentRoomId;
    private String currentRoomName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTabLayout = findViewById(R.id.tab_layout);
        mViewPager = findViewById(R.id.viewpager);
        mFabBottom = findViewById(R.id.fab_bottom);
        mFabTop = findViewById(R.id.fab_top);
        mToolbar = findViewById(R.id.toolbar);
        mAppBarLayout = findViewById(R.id.appbar_layout);

        currentRoomId = getIntent().getStringExtra("ROOM_ID");
        currentRoomName = getIntent().getStringExtra("ROOM_NAME");
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        mToolbar.setSubtitle(mFirebaseUser.getDisplayName());

        FirebaseDatabase.getInstance().getReference("rooms").child(currentRoomId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    currentRoom = snapshot.getValue(Room.class);
                    mTabAdapter = new TabAdapter(MainActivity.this, getSupportFragmentManager());

                    ChatTabFragment chatTabFragment = new ChatTabFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("ROOM_ID", currentRoomId);
                    bundle.putString("ROOM_NAME", currentRoomName);
                    chatTabFragment.setArguments(bundle);
                    AssignmentsTabFragment assignmentsTabFragment = new AssignmentsTabFragment();
                    ProfileFragment profileFragment = new ProfileFragment();

                    mTabAdapter.addFragment(chatTabFragment, "Students");
                    mTabAdapter.addFragment(assignmentsTabFragment, "Assignments");
                    mTabAdapter.addFragment(profileFragment, "Notices");

                    mViewPager.setAdapter(mTabAdapter);

                    mTabLayout.setupWithViewPager(mViewPager);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mToolbar.setNavigationOnClickListener(view -> super.onBackPressed());

        fileRef = FirebaseDatabase.getInstance().getReference().child("files");
        userReference = FirebaseDatabase.getInstance().getReference().child("users").child(mFirebaseUser.getUid());

        fabSettings();
    }

    private void fabSettings() {

        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                switch (position) {
                    case 0:
                        mFabBottom.hide();
                        mFabBottom.setImageResource(R.drawable.ic_comment_white_24dp);
                        mFabTop.hide();
                        mFabBottom.show();

                        mFabBottom.setOnClickListener(view -> {
                            Intent intent = new Intent(MainActivity.this, ContactActivity.class);
                            intent.putExtra("ROOM_ID", currentRoomId);
                            startActivity(intent);
                        });
                        break;
                    case 1:
                        mFabBottom.hide();
                        mFabBottom.setImageResource(R.drawable.ic_camera_alt_white_24dp);
                        mFabTop.show();
                        mFabBottom.show();
                        mFabTop.setOnClickListener(view -> startActivity(new Intent(MainActivity.this, StatusTextActivity.class)));
                        mFabBottom.setOnClickListener(view -> {
                            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                            intent.setType("image/jpeg");
                            intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                            startActivityForResult(Intent.createChooser(intent, "Complete action using"), RC_PHOTO_PICKER);
                        });
                        break;
                    case 2:
                        mFabTop.hide();
                        mFabBottom.hide();
                        break;
                    default:
                        break;

                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_PHOTO_PICKER && resultCode == RESULT_OK) {
            if (data != null) {
                Uri selectedImage = data.getData();
                Intent intent = new Intent(this, EditStatusActivity.class);
                intent.setData(selectedImage);
                startActivity(intent);
            } else {
                Toast.makeText(getApplicationContext(), "data is null", Toast.LENGTH_SHORT).show();
            }

        } else if (requestCode == 1001 && resultCode == RESULT_OK) {
            ImageView upload;
            final Uri imageUri = data.getData();
            final String timestamp = "" + System.currentTimeMillis();
            StorageReference storageReference = FirebaseStorage.getInstance().getReference();
            final String messagePushID = timestamp;
            Toast.makeText(MainActivity.this, imageUri.toString(), Toast.LENGTH_SHORT).show();

            final StorageReference filepath = storageReference.child(messagePushID + "." + "pdf");
            Toast.makeText(MainActivity.this, filepath.getName(), Toast.LENGTH_SHORT).show();
            filepath.putFile(imageUri).continueWithTask((Continuation) task -> filepath.getDownloadUrl()).addOnCompleteListener((OnCompleteListener<Uri>) task -> {
                if (task.isSuccessful()) {
                    Uri uri = task.getResult();
                    String myUrl = uri.toString();
                    String key = fileRef.push().getKey();

                    Assignment assignment = new Assignment();
                    assignment.setAssignmentId(key);
                    assignment.setFileName(filepath.getName());
                    assignment.setFileUrl(myUrl);
                    assignment.setUserName(mFirebaseUser.getDisplayName());
                    assignment.setUserId(mFirebaseUser.getUid());
                    fileRef.child(mFirebaseUser.getUid()).child(key).setValue(assignment);

                    Toast.makeText(MainActivity.this, "Uploaded Successfully " + mFirebaseUser.getDisplayName(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "UploadedFailed", Toast.LENGTH_SHORT).show();
                }
            });

        } else {
            Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        userReference.child("online").setValue(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        long lastSeen = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());
        userReference.child("online").setValue(false);
        userReference.child("lastSeen").setValue(lastSeen);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                mFirebaseAuth.signOut();
                Intent intent = new Intent(MainActivity.this, WelcomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        if (mViewPager.getCurrentItem() == 0) {
            super.onBackPressed();
        } else {
            mViewPager.setCurrentItem(0);
        }
    }
}
