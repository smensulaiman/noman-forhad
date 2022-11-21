package com.nomanforhad.finalproject.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.nomanforhad.finalproject.R;
import com.nomanforhad.finalproject.models.Chat;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class ChatRoomAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int TYPE_INCOMING = 1;
    private final int TYPE_OUTGOING = 2;
    List<Chat> chatList;
    private Context mContext;

    public ChatRoomAdapter(Context context, List<Chat> chats) {
        this.mContext = context;
        this.chatList = chats;
    }

    @Override
    public int getItemViewType(int position) {
        Chat chat = chatList.get(position);
        if (tes(chat)) {
            return TYPE_OUTGOING;
        }
        return TYPE_INCOMING;
    }

    private boolean tes(Chat chat) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser().getUid().equalsIgnoreCase(chat.getSenderUid())) {
            return true;
        }
        return false;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == TYPE_INCOMING) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_incoming, parent, false);
            return new IncomingViewHolder(view);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_outgoing, parent, false);
            return new OutgoingViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        final Chat chat = chatList.get(position);

        if (holder.getItemViewType() == TYPE_INCOMING) {
            IncomingViewHolder holder1 = (IncomingViewHolder) holder;
            if (chat != null) {
                holder1.message.setText(chat.getMessage());
            }
        } else {
            OutgoingViewHolder holder2 = (OutgoingViewHolder) holder;
            if (chat != null) {
                holder2.message.setText(chat.getMessage());
            }
        }
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    public class IncomingViewHolder extends RecyclerView.ViewHolder {
        private TextView message;
        private ConstraintLayout constraintLayout;
        private LinearLayout layout;
        private TextView time;

        public IncomingViewHolder(@NonNull View itemView) {
            super(itemView);
            message = itemView.findViewById(R.id.tv_chat_incoming);
            constraintLayout = itemView.findViewById(R.id.layout_first_incoming);
            layout = itemView.findViewById(R.id.layout_chat_incoming);
            time = itemView.findViewById(R.id.tv_time_chat_incoming);
        }

    }

    public class OutgoingViewHolder extends RecyclerView.ViewHolder {
        private TextView message;
        private ConstraintLayout constraintLayout;
        private LinearLayout layout;
        private TextView time;

        public OutgoingViewHolder(@NonNull View itemView) {
            super(itemView);
            message = itemView.findViewById(R.id.tv_chat_outgoing);
            constraintLayout = itemView.findViewById(R.id.layout_first);
            layout = itemView.findViewById(R.id.layout_chat);
            time = itemView.findViewById(R.id.tv_time_chat_outgoing);
        }


    }

}
