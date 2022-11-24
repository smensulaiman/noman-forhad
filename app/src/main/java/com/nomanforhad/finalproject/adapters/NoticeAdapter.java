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
import com.nomanforhad.finalproject.R;
import com.nomanforhad.finalproject.activities.ChatRoomActivity;
import com.nomanforhad.finalproject.activities.PDFViewActivity;
import com.nomanforhad.finalproject.models.Notice;
import com.nomanforhad.finalproject.models.User;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class NoticeAdapter extends RecyclerView.Adapter<NoticeAdapter.MyViewHolder> {

    private List<Notice> noticeList;
    private Context mContext;

    public NoticeAdapter(Context context, List<Notice> notices) {
        this.noticeList = notices;
        this.mContext = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_notice, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {

        final Notice notice = noticeList.get(position);
        holder.txtTeacherName.setText(notice.getTeacherName());
        holder.txtDescription.setText(notice.getNotice());
        if(notice.getFileName() == null){
            holder.txtFileName.setVisibility(View.GONE);
        }else{
            holder.txtFileName.setText("Attachment : " + notice.getFileName());
            holder.txtFileName.setOnClickListener(view -> {
                Intent intent = new Intent(mContext, PDFViewActivity.class);
                intent.putExtra("pdf", notice.getFileUrl());
                mContext.startActivity(intent);
            });
        }
        Glide.with(mContext.getApplicationContext())
                .load(notice.getTeacherImage())
                .into(holder.mAvatar);
    }

    @Override
    public int getItemCount() {
        return noticeList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView txtTeacherName;
        private TextView txtFileName;
        private TextView txtDescription;
        private CircleImageView mAvatar;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            txtTeacherName = itemView.findViewById(R.id.txt_teacher_name);
            txtFileName = itemView.findViewById(R.id.txt_file_name);
            txtDescription = itemView.findViewById(R.id.txt_notice);
            mAvatar = itemView.findViewById(R.id.img_teacher_image);

        }

    }
}
