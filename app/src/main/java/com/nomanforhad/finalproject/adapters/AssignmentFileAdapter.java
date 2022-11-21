package com.nomanforhad.finalproject.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nomanforhad.finalproject.AssignmentViewActivity;
import com.nomanforhad.finalproject.R;
import com.nomanforhad.finalproject.models.Assignment;

import java.util.List;

public class AssignmentFileAdapter extends RecyclerView.Adapter<AssignmentFileAdapter.MyViewHolder> {

    private List<Assignment> assignmentList;
    private Context mContext;

    public AssignmentFileAdapter(Context context, List<Assignment> assignments) {
        this.mContext = context;
        this.assignmentList = assignments;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_file, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        Assignment currentAssignment = assignmentList.get(position);
        holder.txtFileName.setText(currentAssignment.getFileName());
        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(mContext, AssignmentViewActivity.class);
            intent.putExtra("ASSIGNMENT_MODEL", currentAssignment);
            mContext.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return assignmentList.size();
    }

    public void setAssignmentList(List<Assignment> assignmentList) {
        this.assignmentList = assignmentList;
        notifyDataSetChanged();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView txtFileName;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            txtFileName = itemView.findViewById(R.id.txt_file_name);
        }
    }
}
