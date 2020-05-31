package com.example.seeyousoon.MessagesSection;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.seeyousoon.R;
import com.example.seeyousoon.data.ChatData;
import com.example.seeyousoon.data.UserData;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class Chat_menu_adapter extends RecyclerView.Adapter<Chat_menu_adapter.ViewHolder> {

    private Context mContext;
    private List<UserData> mUsers;
    private boolean isChat;
    private String lastMessage;

    public Chat_menu_adapter (Context mContext, List<UserData> mUsers,boolean isChat){
        this.mContext = mContext;
        this.mUsers = mUsers;
        this.isChat =  isChat;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.chat_menu_user,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final UserData userData = mUsers.get(position);
        holder.username.setText(userData.getName());
        holder.profile_image.setImageResource(R.drawable.profile);

        if (isChat){
            lastMessage(userData.getUID(), holder.lastMsg);
        } else {
            holder.lastMsg.setVisibility(View.GONE);
        }

        if (isChat){
            if (userData.getStatus().equals("Online")){
                holder.imgOn.setVisibility(View.VISIBLE);
                holder.imgOff.setVisibility(View.GONE);
            }else{
                holder.imgOff.setVisibility(View.VISIBLE);
                holder.imgOn.setVisibility(View.GONE);
            }
        }else {
            holder.imgOn.setVisibility(View.GONE);
            holder.imgOff.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext,MainChatActivity.class);
                intent.putExtra("userId",userData.getUID());
                intent.putExtra("userName",userData.getName());
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        public TextView username, lastMsg;
        public ImageView profile_image;
        private CircleImageView imgOn;
        private CircleImageView imgOff;

        public ViewHolder(View itemView){
            super(itemView);

            username = itemView.findViewById(R.id.chatUserName);
            profile_image = itemView.findViewById(R.id.chatProfileImage);
            imgOn = itemView.findViewById(R.id.img_on);
            imgOff = itemView.findViewById(R.id.img_off);
            lastMsg = itemView.findViewById(R.id.last_msg);
        }
    }

    private void lastMessage(final String userId, final TextView last_msg){
        lastMessage = "";
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Chat");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean ss=true;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    ChatData chatData = snapshot.getValue(ChatData.class);
                    ChatData chatData1 = snapshot.getValue(ChatData.class);
                    if(chatData.getReceiver().equals(firebaseUser.getUid()) && chatData.getSender().equals(userId) ||
                            chatData.getReceiver().equals(userId) && chatData.getSender().equals(firebaseUser.getUid())){
                        lastMessage = chatData.getMessage();
                        if (chatData1.getReceiver().equals(firebaseUser.getUid()) && chatData.getSender().equals(userId)) {
                            ss = chatData1.isIsseen();
                        }
                    }
                }
                if (ss){
                    last_msg.setTextColor(Color.parseColor("#ffffff"));
                } else {
                    last_msg.setTextColor(Color.parseColor("#5DD7E6"));
                }
                if (lastMessage.length() > 0){
                    last_msg.setText(lastMessage);
                }
                lastMessage = "";
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
