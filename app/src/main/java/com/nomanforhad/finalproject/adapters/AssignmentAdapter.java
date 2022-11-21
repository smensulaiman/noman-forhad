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
import com.devlomi.circularstatusview.CircularStatusView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nomanforhad.finalproject.R;
import com.nomanforhad.finalproject.activities.ShowOtherStatusActivity;
import com.nomanforhad.finalproject.models.Status;
import com.nomanforhad.finalproject.models.User;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AssignmentAdapter extends RecyclerView.Adapter<AssignmentAdapter.MyViewHolder> {

    private List<Status> statusList;
    private Context mContext;

    public AssignmentAdapter(Context context, List<Status> statuses) {
        this.mContext = context;
        this.statusList = statuses;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_status, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        DatabaseReference mUserReference = FirebaseDatabase.getInstance().getReference().child("users");
        Status currentStatus = statusList.get(position);
        final String userUid = currentStatus.getUid();

        mUserReference.child(userUid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                holder.mUsername.setText(user.getUsername());
                try {
                    Glide.with(holder.mThumbnail.getContext())
                            .load(user.getPhotoUrl())
                            .into(holder.mThumbnail);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        holder.mLayout.setOnClickListener(view -> {
            Intent intent = new Intent(mContext, ShowOtherStatusActivity.class);
            intent.putExtra("uid", userUid);
            mContext.startActivity(intent);
        });

        holder.mStatusCount.setPortionsCount(currentStatus.getStatuscount());
    }

    public void setStatusList(List<Status> statusList) {
        this.statusList = statusList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return statusList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private CircleImageView mThumbnail;
        private CircularStatusView mStatusCount;
        private TextView mUsername;
        private TextView mDate;
        private RelativeLayout mLayout;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            mThumbnail = itemView.findViewById(R.id.thumbnail_status);
            mStatusCount = itemView.findViewById(R.id.circular_status_counts);
            mUsername = itemView.findViewById(R.id.username_status);
            mDate = itemView.findViewById(R.id.date_status);
            mLayout = itemView.findViewById(R.id.layout_other_status);
        }
    }
}
