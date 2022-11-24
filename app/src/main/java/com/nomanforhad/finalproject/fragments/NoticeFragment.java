package com.nomanforhad.finalproject.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.nomanforhad.finalproject.R;
import com.nomanforhad.finalproject.adapters.NoticeAdapter;
import com.nomanforhad.finalproject.databinding.FragmentProfileBinding;
import com.nomanforhad.finalproject.databinding.ViewCreateNoticeBinding;
import com.nomanforhad.finalproject.models.Notice;
import com.nomanforhad.finalproject.models.User;
import com.nomanforhad.finalproject.utils.Utility;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class NoticeFragment extends Fragment {

    private FragmentProfileBinding binding;
    private ViewCreateNoticeBinding noticeBinding;
    private User user;
    private NoticeAdapter noticeAdapter;
    private List<Notice> noticeList;
    private String fileUrl = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        noticeList = new ArrayList<>();
        noticeAdapter = new NoticeAdapter(getContext(), noticeList);
        binding.recyclerView.setAdapter(noticeAdapter);
        
        Utility.getCurrentUser(FirebaseAuth.getInstance().getUid(), user -> {
            this.user = user;
            if(Utility.isAdmin(user)){
                binding.fabChat.setVisibility(View.VISIBLE);
            }
        });

        binding.fabChat.setOnClickListener(view -> {

            noticeBinding = ViewCreateNoticeBinding.inflate(inflater);
            noticeBinding.btnAttachFile.setOnClickListener(view1 -> {
                choosePdf();
            });

            AlertDialog alertDialog = new MaterialAlertDialogBuilder(getContext())
                    .setIcon(R.drawable.ic_edit_black_24dp)
                    .setTitle("Create Notice")
                    .setMessage("Please write your notice in 200 words.")
                    .setView(noticeBinding.getRoot())
                    .setPositiveButton("Submit", (dialogInterface, i) -> {

                        String strNotice = noticeBinding.layoutNotice.getEditText().getText().toString();

                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("notice");
                        String key = ref.push().getKey();
                        Notice notice = new Notice();
                        notice.setNoticeId(key);
                        notice.setTeacherName(user.getUsername());
                        notice.setTeacherImage(user.getPhotoUrl());
                        notice.setNotice(strNotice);
                        notice.setFileUrl(fileUrl);
                        notice.setRoomId(null);
                        ref.child(key).setValue(notice);

                        dialogInterface.dismiss();
                    })
                    .setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.dismiss()).create();

            alertDialog.show();

        });

        FirebaseDatabase.getInstance().getReference("notice").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                noticeList.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    noticeList.add(dataSnapshot.getValue(Notice.class));
                }
                noticeAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return binding.getRoot();
    }

    public void setFileUri(Uri fileUri, String myUrl){
        noticeBinding.txtFileName.setText(getFileName(fileUri));
        this.fileUrl = myUrl;
    }

    @SuppressLint("Range")
    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContext().getContentResolver().query(uri, null,
                    null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    private static final int PDF_CHOOSER_CODE = 1002;

    private void choosePdf() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/pdf");
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        getActivity().startActivityForResult(intent, PDF_CHOOSER_CODE);
    }
}