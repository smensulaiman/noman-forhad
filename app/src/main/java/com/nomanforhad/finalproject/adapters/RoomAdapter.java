package com.nomanforhad.finalproject.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.nomanforhad.finalproject.activities.MainActivity;
import com.nomanforhad.finalproject.databinding.ItemRoomBinding;
import com.nomanforhad.finalproject.models.Room;
import com.nomanforhad.finalproject.models.User;

import java.util.List;

public class RoomAdapter extends RecyclerView.Adapter<RoomAdapter.ViewHolder> {
    private static final String TAG = "RoomAdapter";

    private Context context;
    private List<Room> rooms;

    public RoomAdapter(Context context, List<Room> rooms) {
        this.context = context;
        this.rooms = rooms;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(ItemRoomBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @SuppressLint("NewApi")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Room room = rooms.get(position);

        holder.binding.txtRoomName.setText("Name " + room.getRoomName());
        holder.binding.txtDeadline.setText("End Date : " + room.getDeadLine());
        holder.itemView.setOnClickListener(view -> {

            boolean exists = false;

            for (User student : room.getStudents()) {
                if (student.getUid().equals(FirebaseAuth.getInstance().getUid())) {
                    exists = true;
                    break;
                }
            }

            if (exists) {
                Intent intent = new Intent(context, MainActivity.class);
                intent.putExtra("ROOM_ID", room.getRoomId());
                intent.putExtra("ROOM_NAME", room.getRoomName());
                context.startActivity(intent);
            } else {

                AlertDialog alertDialog = new MaterialAlertDialogBuilder(context)
                        .setTitle("Warning !!!")
                        .setMessage("You are not authorized to enter this room")
                        .setNeutralButton("Ok", (dialogInterface, i) -> dialogInterface.dismiss()).create();
                alertDialog.show();

            }

        });
    }

    @Override
    public int getItemCount() {
        return rooms.size();
    }

    public void setRooms(List<Room> rooms) {
        this.rooms = rooms;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private ItemRoomBinding binding;

        public ViewHolder(@NonNull ItemRoomBinding itemRoomBinding) {
            super(itemRoomBinding.getRoot());
            binding = itemRoomBinding;
        }
    }
}
