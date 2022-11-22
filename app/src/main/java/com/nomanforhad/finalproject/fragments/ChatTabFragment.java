package com.nomanforhad.finalproject.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.nomanforhad.finalproject.R;
import com.nomanforhad.finalproject.adapters.ChatListAdapter;
import com.nomanforhad.finalproject.models.Conversation;
import com.nomanforhad.finalproject.models.User;

import org.jitsi.meet.sdk.JitsiMeetActivity;
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;
import org.jitsi.meet.sdk.JitsiMeetUserInfo;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ChatTabFragment extends Fragment {
    private static final String TAG = "ChatTabFragment";

    private RecyclerView mRecyclerView;
    private LinearLayout mStartChatLayout;
    private MaterialButton btnConference;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mConversationReference;
    private FirebaseAuth mAuth;
    private FirebaseUser mFirebaseUser;
    private ChatListAdapter mAdapter;

    private List<User> userList = new ArrayList<>();
    private List<Conversation> conversationList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_chat_tab, container, false);

        mRecyclerView = rootView.findViewById(R.id.recyvlerview_chat_tab);
        mStartChatLayout = rootView.findViewById(R.id.layout_start_chat);
        btnConference = rootView.findViewById(R.id.btn_conference);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mAuth.getCurrentUser();

        mConversationReference = mFirebaseDatabase.getReference().child("conversation").child(mFirebaseUser.getUid());

        btnConference.setOnClickListener(view -> {
            final String roomId = getArguments().getString("ROOM_ID");
            final String roomName = getArguments().getString("ROOM_NAME");
            final String userName = mFirebaseUser.getDisplayName();
            final String url = "https://meet.jit.si/"+roomId;

            Log.d(TAG, "room id : " + roomId +
                    "\nroom name : " + roomName +
                    "\nuser name : " + userName +
                    "\nroom url : " + url );

            JitsiMeetUserInfo info = new JitsiMeetUserInfo();
            info.setDisplayName(userName);

            try {
                JitsiMeetConferenceOptions options = new JitsiMeetConferenceOptions.Builder()
                        .setServerURL(new URL(url))
                        .setUserInfo(info)
                        .setRoom(roomName)
                        .setWelcomePageEnabled(false)
                        .build();
                JitsiMeetActivity.launch(getContext(), options);
            }catch (Exception e){
                e.printStackTrace();
            }
        });

        getData();

        return rootView;
    }

    public void setUserList(List<User> users) {
        this.userList = users;
    }

    private void getData() {
        Query myQuery = mConversationReference.orderByChild("timestamp");
        myQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                conversationList.clear();
                if (dataSnapshot.exists()) {
                    mStartChatLayout.setVisibility(View.GONE);
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Conversation conversation = snapshot.getValue(Conversation.class);
                        conversationList.add(conversation);
                        mAdapter = new ChatListAdapter(getActivity(), conversationList, userList);
                        mRecyclerView.setAdapter(mAdapter);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
