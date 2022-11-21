package com.nomanforhad.finalproject.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nomanforhad.finalproject.databinding.RowUserBinding;
import com.nomanforhad.finalproject.models.User;

import java.util.ArrayList;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private Context context;
    private List<User> users;
    private List<User> selectedUsers = new ArrayList<>();

    public UserAdapter(Context context, List<User> users) {
        this.context = context;
        this.users = users;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(RowUserBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final User user = users.get(position);
        holder.binding.btnCheckUser.setText(user.getUsername());

        holder.binding.btnCheckUser.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                selectedUsers.add(user);
            } else {
                selectedUsers.remove(user);
            }
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public void setUsers(List<User> users) {
        this.users = users;
        notifyDataSetChanged();
    }

    public List<User> getSelectedUsers() {
        return selectedUsers;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private RowUserBinding binding;

        public ViewHolder(@NonNull RowUserBinding itemUserBinding) {
            super(itemUserBinding.getRoot());
            binding = itemUserBinding;
        }
    }
}
