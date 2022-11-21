package com.nomanforhad.finalproject.utils;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;

import com.nomanforhad.finalproject.models.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Utility {

    public interface UserInterface {
        void onUserFound(User user);
    }

    public static void setMargins(View view) {
        if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            p.setMargins(0, 0, 8, 0);
            view.requestLayout();
        }
    }

    public static void setProgressMax(ProgressBar pb) {
        pb.setMax(100 * 100);
    }

    public static void getCurrentUser(String userId, UserInterface userInterface) {

        FirebaseDatabase.getInstance().getReference("users").child(userId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        userInterface.onUserFound(snapshot.getValue(User.class));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    public static boolean isAdmin(User user) {
        if (User.UserType.valueOf(user.getUserType()) == User.UserType.TEACHER) {
            return true;
        }
        return false;
    }

}
