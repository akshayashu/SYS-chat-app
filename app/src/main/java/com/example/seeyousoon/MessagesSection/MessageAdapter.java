package com.example.seeyousoon.MessagesSection;

import android.content.Context;
import android.content.Intent;
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

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder>{

    public static final int MSG_TYP_LEFT = 0;
    public static final int MSG_TYP_RIGHT = 1;
    private Context mContext;
    private List<ChatData> mChat;
    private String username;

    private FirebaseUser firebaseUser;

    public MessageAdapter (Context mContext, List<ChatData> mChat,String username){
        this.mContext = mContext;
        this.mChat = mChat;
        this.username=username;
    }

    @NonNull
    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if(viewType == MSG_TYP_RIGHT) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_right, parent, false);
            return new MessageAdapter.ViewHolder(view);
        }else{
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_left, parent, false);
            return new MessageAdapter.ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.ViewHolder holder, int position) {

        ChatData chatData = mChat.get(position);
        holder.showMessage.setText(chatData.getMessage());
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if(mChat.get(position).getSender().equals(firebaseUser.getUid())){
            holder.personName.setText("Me");
        }else{
            holder.personName.setText(username);
        }

        holder.txt_time.setText(chatData.getTime());

        if (position == mChat.size()-1){
            if (chatData.isIsseen()){
                holder.txt_seen.setText("Seen");
            }else {
                holder.txt_seen.setText("Delivered");
            }
        }else {
            holder.txt_seen.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return mChat.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        public TextView showMessage, personName, txt_seen, txt_time;

        public ViewHolder(View itemView){
            super(itemView);

            personName = itemView.findViewById(R.id.personName);
            showMessage = itemView.findViewById(R.id.textMessage);
            txt_seen = itemView.findViewById(R.id.text_seen);
            txt_time = itemView.findViewById(R.id.txtTime);
        }
    }

    @Override
    public int getItemViewType(int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if(mChat.get(position).getSender().equals(firebaseUser.getUid())){
            return MSG_TYP_RIGHT;
        }else{
            return MSG_TYP_LEFT;
        }
    }
}
