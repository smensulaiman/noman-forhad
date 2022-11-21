package com.nomanforhad.finalproject.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.nomanforhad.finalproject.R;
import com.nomanforhad.finalproject.databinding.FragmentProfileBinding;
import com.nomanforhad.finalproject.databinding.ViewCreateNoticeBinding;
import com.nomanforhad.finalproject.utils.Utility;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);

        Utility.getCurrentUser(FirebaseAuth.getInstance().getUid(), user -> {
            if(Utility.isAdmin(user)){
                binding.txtBtnCreateNotice.setVisibility(View.VISIBLE);
            }
        });

        binding.txtBtnCreateNotice.setOnClickListener(view -> {

            ViewCreateNoticeBinding noticeBinding = ViewCreateNoticeBinding.inflate(inflater);
            AlertDialog alertDialog = new MaterialAlertDialogBuilder(getContext())
                    .setIcon(R.drawable.ic_edit_black_24dp)
                    .setTitle("Create Notice")
                    .setMessage("Please write your notice in 200 words.")
                    .setView(noticeBinding.getRoot())
                    .setPositiveButton("Submit", (dialogInterface, i) -> {
                        String notice = noticeBinding.layoutNotice.getEditText().getText().toString();
                        FirebaseDatabase.getInstance().getReference("notice").setValue(notice);
                    })
                    .setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.dismiss()).create();

            alertDialog.show();

        });

        FirebaseDatabase.getInstance().getReference("notice").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                binding.txtNotice.setText(snapshot.getValue(String.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return binding.getRoot();
    }
}