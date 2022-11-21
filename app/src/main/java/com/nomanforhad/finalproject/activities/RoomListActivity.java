package com.nomanforhad.finalproject.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nomanforhad.finalproject.R;
import com.nomanforhad.finalproject.adapters.RoomAdapter;
import com.nomanforhad.finalproject.adapters.UserAdapter;
import com.nomanforhad.finalproject.databinding.ActivityRoomListBinding;
import com.nomanforhad.finalproject.models.Room;
import com.nomanforhad.finalproject.models.User;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class RoomListActivity extends AppCompatActivity {
    private static final String TAG = "RoomListActivity";

    private ActivityRoomListBinding binding;

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private String instructorId;
    private String instructorName;

    private RoomAdapter roomAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRoomListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setDisplayShowHomeEnabled(true);

        firebaseAuth = FirebaseAuth.getInstance();
        instructorId = firebaseAuth.getUid();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("rooms");

        roomAdapter = new RoomAdapter(this, new ArrayList());

        binding.recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        binding.recyclerView.setAdapter(roomAdapter);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                List<Room> rooms = new ArrayList<>();

                for (DataSnapshot child : snapshot.getChildren()) {
                    Room room = child.getValue(Room.class);
                    rooms.add(room);
                }

                roomAdapter.setRooms(rooms);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        firebaseDatabase.getReference("users").child(firebaseAuth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() == null) {
                    Toast.makeText(RoomListActivity.this, "Invalid user", Toast.LENGTH_SHORT).show();
                    firebaseAuth.signOut();
                    startActivity(new Intent(RoomListActivity.this, WelcomeActivity.class));
                    finish();
                } else {
                    User user = snapshot.getValue(User.class);
                    instructorName = user.getUsername();
                    switch (User.UserType.valueOf(user.getUserType())) {
                        case STUDENT:
                            Toast.makeText(RoomListActivity.this, "Hello " + user.getUsername(), Toast.LENGTH_SHORT).show();
                            break;
                        case TEACHER:
                            binding.btnCreateNewRoom.setVisibility(View.VISIBLE);
                            break;
                        default:
                            break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                firebaseAuth.signOut();
                finish();
            }
        });

        binding.btnSignOut.setOnClickListener(view -> {
            firebaseAuth.signOut();
            startActivity(new Intent(RoomListActivity.this, WelcomeActivity.class));
            finish();
        });

        binding.btnCreateNewRoom.setOnClickListener(view -> {

            List<User> users = new ArrayList<>();

            firebaseDatabase.getReference("users").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                        User user = userSnapshot.getValue(User.class);
                        users.add(user);
                    }

                    View createRoomView = LayoutInflater.from(RoomListActivity.this).inflate(R.layout.view_create_new_room, null);

                    TextInputLayout roomNameLayout = createRoomView.findViewById(R.id.layout_room_name);
                    TextInputLayout dateLayout = createRoomView.findViewById(R.id.layout_room_deadline);

                    dateLayout.getEditText().setOnClickListener(view12 -> {
                        DatePickerDialog datePickerDialog;
                        final Calendar c = Calendar.getInstance();
                        int mYear = c.get(Calendar.YEAR);
                        int mMonth = c.get(Calendar.MONTH);
                        int mDay = c.get(Calendar.DAY_OF_MONTH);
                        datePickerDialog = new DatePickerDialog(RoomListActivity.this,
                                (view1, year, monthOfYear, dayOfMonth) -> dateLayout.getEditText().setText(dayOfMonth + "/"
                                        + (monthOfYear + 1) + "/" + year), mYear, mMonth, mDay);
                        datePickerDialog.show();
                    });

                    UserAdapter userAdapter = new UserAdapter(RoomListActivity.this, users);
                    RecyclerView recyclerViewStudents = createRoomView.findViewById(R.id.recyclerview_students);
                    recyclerViewStudents.setLayoutManager(new LinearLayoutManager(RoomListActivity.this));
                    recyclerViewStudents.setItemViewCacheSize(users.size());
                    recyclerViewStudents.setAdapter(userAdapter);

                    AlertDialog dialog = new MaterialAlertDialogBuilder(RoomListActivity.this)
                            .setTitle("Create Room")
                            .setView(createRoomView)
                            .setPositiveButton("Create room", (dialogInterface, i) -> {

                                String key = databaseReference.push().getKey();

                                Room room = new Room();
                                room.setRoomId(key);
                                room.setRoomName(roomNameLayout.getEditText().getText().toString());
                                room.setInstructorId(instructorId);
                                room.setInstructorName(instructorName);
                                room.setDeadLine(dateLayout.getEditText().getText().toString());
                                room.setStudents(userAdapter.getSelectedUsers());

                                databaseReference.child(key).setValue(room);
                                dialogInterface.dismiss();

                            })
                            .setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.dismiss())
                            .create();

                    new Handler().post(() -> runOnUiThread(() -> dialog.show()));

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        });

    }
}