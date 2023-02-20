package com.nomanforhad.finalproject.activities;

import android.os.Bundle;
import android.view.Menu;
import android.view.accessibility.AccessibilityEvent;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nomanforhad.finalproject.R;
import com.nomanforhad.finalproject.adapters.ContactAdapter;
import com.nomanforhad.finalproject.models.Room;
import com.nomanforhad.finalproject.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ContactActivity extends AppCompatActivity {

    private FirebaseAuth mFirebaseAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private FirebaseUser mFirebaseUser;
    private String roomId;

    private RecyclerView mRecyclerView;
    private Toolbar mToolbar;
    private TextView mContactCount;

    private ContactAdapter mAdapter;

    private List<User> contactList = new ArrayList<>();

    @Override
    public boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent event) {
        return super.dispatchPopulateAccessibilityEvent(event);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        mRecyclerView = findViewById(R.id.recycler_view_contact);
        mToolbar = findViewById(R.id.toolbar_contact);
        mContactCount = findViewById(R.id.tv_contact_counts);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mToolbar.setNavigationOnClickListener(view -> finish());

        roomId = getIntent().getStringExtra("ROOM_ID");

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        mDatabaseReference = mFirebaseDatabase.getReference().child("rooms").child(roomId);

        getData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_contact, menu);
        return true;
    }

    private void getData() {
        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                contactList.clear();
                Room room = dataSnapshot.getValue(Room.class);
                for (User student : room.getStudents()) {
                    assert student != null;
                    if (!student.getUid().equals(mFirebaseUser.getUid())) {
                        contactList.add(student);
                        mContactCount.setText(contactList.size() + " contacts");
                    }
                }

                mAdapter = new ContactAdapter(ContactActivity.this, roomId, contactList);
                mRecyclerView.setAdapter(mAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
