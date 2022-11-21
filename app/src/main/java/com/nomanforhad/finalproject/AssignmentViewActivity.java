package com.nomanforhad.finalproject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.nomanforhad.finalproject.databinding.ActivityAssignmentViewBinding;
import com.nomanforhad.finalproject.databinding.ViewEditMarksBinding;
import com.nomanforhad.finalproject.models.Assignment;
import com.nomanforhad.finalproject.utils.Utility;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class AssignmentViewActivity extends AppCompatActivity {

    private ActivityAssignmentViewBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAssignmentViewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        binding.toolbar.setNavigationOnClickListener(view -> super.onBackPressed());

        Utility.getCurrentUser(FirebaseAuth.getInstance().getUid(), user -> {
            if(Utility.isAdmin(user)){
                binding.txtBtnUpdate.setVisibility(View.VISIBLE);
            }
        });

        Assignment assignment = getIntent().getParcelableExtra("ASSIGNMENT_MODEL");
        updateUI(assignment);

        FirebaseDatabase.getInstance().getReference("files")
                .child(assignment.getUserId())
                .child(assignment.getAssignmentId())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Assignment updatedAssignment = snapshot.getValue(Assignment.class);
                        updateUI(updatedAssignment);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        binding.txtBtnUpdate.setOnClickListener(view -> {

            ViewEditMarksBinding editMarksBinding = ViewEditMarksBinding.inflate(getLayoutInflater());
            AlertDialog alertDialog = new MaterialAlertDialogBuilder(this)
                    .setIcon(R.drawable.ic_edit_black_24dp)
                    .setTitle("Edit Marks")
                    .setMessage("Please write comment in 25 words.")
                    .setView(editMarksBinding.getRoot())
                    .setPositiveButton("Update", (dialogInterface, i) -> {
                        assignment.setGrade(Integer.parseInt(editMarksBinding.layoutMarks.getEditText().getText().toString()));
                        assignment.setRemarks(editMarksBinding.layoutComment.getEditText().getText().toString());
                        FirebaseDatabase.getInstance().getReference("files").child(assignment.getUserId()).child(assignment.getAssignmentId()).setValue(assignment);
                    })
                    .setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.dismiss()).create();

            alertDialog.show();

        });

        new RetrivePDFFromURL().execute(assignment.getFileUrl());
    }

    public void updateUI(Assignment assignment) {
        binding.toolbar.setSubtitle(assignment.getFileName());
        binding.txtUsername.setText("Name : " + assignment.getUserName());
        binding.txtMark.setText("Marks : " + assignment.getGrade());
        binding.txtRemarks.setText("Comment : " + assignment.getRemarks());
    }

    class RetrivePDFFromURL extends AsyncTask<String, Void, InputStream> {
        @Override
        protected InputStream doInBackground(String... strings) {
            InputStream inputStream = null;
            try {
                URL url = new URL(strings[0]);
                HttpURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
                if (urlConnection.getResponseCode() == 200) {
                    inputStream = new BufferedInputStream(urlConnection.getInputStream());
                }

            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
            return inputStream;
        }

        @Override
        protected void onPostExecute(InputStream inputStream) {
            binding.pdfView.fromStream(inputStream).load();
        }
    }

}