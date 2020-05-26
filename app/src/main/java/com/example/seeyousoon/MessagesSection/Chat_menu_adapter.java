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
import com.example.seeyousoon.data.UserData;

import java.util.List;

public class Chat_menu_adapter extends RecyclerView.Adapter<Chat_menu_adapter.ViewHolder> {

    private Context mContext;
    private List<UserData> mUsers;

    public Chat_menu_adapter (Context mContext, List<UserData> mUsers){
        this.mContext = mContext;
        this.mUsers = mUsers;
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

        public TextView username;
        public ImageView profile_image;

        public ViewHolder(View itemView){
            super(itemView);

            username = itemView.findViewById(R.id.chatUserName);
            profile_image = itemView.findViewById(R.id.chatProfileImage);
        }
    }


}
