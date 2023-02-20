package com.nomanforhad.finalproject.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.nomanforhad.finalproject.R;
import com.nomanforhad.finalproject.adapters.TabAdapter;
import com.nomanforhad.finalproject.adapters.UserAdapter;
import com.nomanforhad.finalproject.fragments.AssignmentsTabFragment;
import com.nomanforhad.finalproject.fragments.ChatTabFragment;
import com.nomanforhad.finalproject.fragments.NoticeFragment;
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
import com.nomanforhad.finalproject.models.User;

import java.util.ArrayList;
import java.util.List;
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
    private String instructorId;
    private String currentRoomId;
    private String currentRoomName;
    private NoticeFragment noticeFragment;
    private Room currentRoom;

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

        firebaseAuth = FirebaseAuth.getInstance();
        instructorId = firebaseAuth.getUid();
        firebaseDatabase = FirebaseDatabase.getInstance();
        roomReference = firebaseDatabase.getReference("rooms");

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
                            assignmentsTabFragment.setArguments(bundle);
                            noticeFragment = new NoticeFragment();
                            noticeFragment.setArguments(bundle);
                            mTabAdapter.addFragment(chatTabFragment, "Students");
                            mTabAdapter.addFragment(assignmentsTabFragment, "Assignments");
                            mTabAdapter.addFragment(noticeFragment, "Notices");

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
                    assignment.setRoomId(currentRoomId);
                    assignment.setUserName(mFirebaseUser.getDisplayName());
                    assignment.setUserId(mFirebaseUser.getUid());
                    fileRef.child(mFirebaseUser.getUid()).child(key).setValue(assignment);

                    Toast.makeText(MainActivity.this, "Uploaded Successfully " + mFirebaseUser.getDisplayName(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "UploadedFailed", Toast.LENGTH_SHORT).show();
                }
            });

        } else if (requestCode == 1002 && resultCode == RESULT_OK) {

            final Uri imageUri = data.getData();
            final String timestamp = "" + System.currentTimeMillis();
            StorageReference storageReference = FirebaseStorage.getInstance().getReference();
            final String messagePushID = timestamp;

            final StorageReference filepath = storageReference.child(messagePushID + "." + "pdf");
            Toast.makeText(MainActivity.this, filepath.getName(), Toast.LENGTH_SHORT).show();
            filepath.putFile(imageUri).continueWithTask((Continuation) task -> filepath.getDownloadUrl()).addOnCompleteListener((OnCompleteListener<Uri>) task -> {
                if (task.isSuccessful()) {
                    Uri uri = task.getResult();
                    String myUrl = uri.toString();

                    noticeFragment.setFileUri(data.getData(), myUrl);

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

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference roomReference;

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

            case R.id.menu_add_student:

                ProgressDialog progressDialog = new ProgressDialog(this);
                progressDialog.setMessage("Please wait");
                progressDialog.show();

                List<User> users = new ArrayList<>();
                firebaseDatabase.getReference("users").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        users.clear();
                        progressDialog.dismiss();
                        for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                            User user = userSnapshot.getValue(User.class);
                            users.add(user);
                        }

                        View createRoomView = LayoutInflater.from(MainActivity.this).inflate(R.layout.view_create_new_room, null);

                        createRoomView.findViewById(R.id.layout_room_name).setVisibility(View.GONE);
                        createRoomView.findViewById(R.id.layout_room_deadline).setVisibility(View.GONE);

                        UserAdapter userAdapter = new UserAdapter(MainActivity.this, users);
                        RecyclerView recyclerViewStudents = createRoomView.findViewById(R.id.recyclerview_students);
                        recyclerViewStudents.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                        recyclerViewStudents.setItemViewCacheSize(users.size());
                        recyclerViewStudents.setAdapter(userAdapter);

                        AlertDialog dialog = new MaterialAlertDialogBuilder(MainActivity.this)
                                .setTitle("Add New Student")
                                .setView(createRoomView)
                                .setPositiveButton("Update", (dialogInterface, i) -> {
                                    currentRoom.insertOrUpdateStudents(userAdapter.getSelectedUsers());
                                    FirebaseDatabase.getInstance().getReference("rooms").child(currentRoomId).setValue(currentRoom)
                                            .addOnSuccessListener(unused -> {
                                                Toast.makeText(MainActivity.this, "Success !!!", Toast.LENGTH_SHORT).show();
                                            });
                                })
                                .setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.dismiss())
                                .create();

                        new Handler().post(() -> runOnUiThread(() -> dialog.show()));

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                return true;

            case R.id.menu_remove_student:

                progressDialog = new ProgressDialog(this);
                progressDialog.setMessage("Please wait");
                progressDialog.show();

                users = new ArrayList<>();
                users.clear();

                firebaseDatabase.getReference("rooms").child(currentRoomId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Room room = snapshot.getValue(Room.class);
                        for (User student : room.getStudents()) {
                            assert student != null;
                            if (!student.getUid().equals(mFirebaseUser.getUid())) {
                                users.add(student);
                            }
                        }

                        View createRoomView = LayoutInflater.from(MainActivity.this).inflate(R.layout.view_create_new_room, null);

                        createRoomView.findViewById(R.id.layout_room_name).setVisibility(View.GONE);
                        createRoomView.findViewById(R.id.layout_room_deadline).setVisibility(View.GONE);

                        UserAdapter userAdapter = new UserAdapter(MainActivity.this, users);
                        RecyclerView recyclerViewStudents = createRoomView.findViewById(R.id.recyclerview_students);
                        recyclerViewStudents.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                        recyclerViewStudents.setItemViewCacheSize(users.size());
                        recyclerViewStudents.setAdapter(userAdapter);

                        AlertDialog dialog = new MaterialAlertDialogBuilder(MainActivity.this)
                                .setTitle("Delete Student")
                                .setView(createRoomView)
                                .setPositiveButton("Update", (dialogInterface, i) -> {
                                    currentRoom.insertOrUpdateStudents(userAdapter.getSelectedUsers());
                                    FirebaseDatabase.getInstance().getReference("rooms").child(currentRoomId).setValue(currentRoom)
                                            .addOnSuccessListener(unused -> {
                                                Toast.makeText(MainActivity.this, "Success !!!", Toast.LENGTH_SHORT).show();
                                            });
                                })
                                .setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.dismiss())
                                .create();

                        new Handler().post(() -> runOnUiThread(() -> dialog.show()));

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

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
