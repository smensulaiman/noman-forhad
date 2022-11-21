package com.nomanforhad.finalproject.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nomanforhad.finalproject.activities.ChatRoomActivity;
import com.nomanforhad.finalproject.R;
import com.nomanforhad.finalproject.models.User;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.MyViewHolder> {

    private List<User> contactList;
    private Context mContext;
    private String roomId;

    public ContactAdapter(Context context, String roomId, List<User> contacts) {
        this.contactList = contacts;
        this.mContext = context;
        this.roomId = roomId;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contact_list, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        final User currentUser = contactList.get(position);

        DatabaseReference userReference = FirebaseDatabase.getInstance().getReference().child("users");
        userReference.child(currentUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final User user = dataSnapshot.getValue(User.class);
                holder.mContactItem.setOnClickListener(view -> {
                    Intent intent = new Intent(mContext, ChatRoomActivity.class);
                    intent.putExtra(ChatRoomActivity.EXTRAS_USER, user);
                    intent.putExtra("userUid", currentUser.getUid());
                    mContext.startActivity(intent);
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        holder.mTvUsername.setText(currentUser.getUsername());
        holder.mTvAbout.setText(currentUser.getAbout());
        holder.mAvatar.setVisibility(View.VISIBLE);
        Glide.with(holder.mAvatar.getContext())
                .load(currentUser.getPhotoUrl())
                .into(holder.mAvatar);
    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView mTvUsername;
        private TextView mTvAbout;
        private CircleImageView mAvatar;
        private RelativeLayout mContactItem;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            mTvUsername = itemView.findViewById(R.id.tv_username_contact);
            mTvAbout = itemView.findViewById(R.id.tv_about_contact);
            mAvatar = itemView.findViewById(R.id.avatar_user_contact);
            mContactItem = itemView.findViewById(R.id.item_contact_layout);
        }

    }
}
