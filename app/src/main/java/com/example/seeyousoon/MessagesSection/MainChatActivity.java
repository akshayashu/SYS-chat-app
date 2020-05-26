package com.example.seeyousoon.MessagesSection;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainChatActivity extends AppCompatActivity {

    CircleImageView profilePic;
    TextView userName;
    RecyclerView recyclerView;
    EditText writeMessage;
    Button sendBtn;

    FirebaseUser firebaseUser;
    DatabaseReference reference;

    MessageAdapter messageAdapter;
    List<ChatData> mChat;

    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_chat);

        profilePic = findViewById(R.id.profilePic);
        userName = findViewById(R.id.username);
        writeMessage = findViewById(R.id.writeMessage);
        sendBtn = findViewById(R.id.sendMessage);
        recyclerView = findViewById(R.id.recyclerView1);

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        intent = getIntent();
        final String userId = intent.getStringExtra("userId");
        final String username = intent.getStringExtra("userName");

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("User Data").child(userId);

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = writeMessage.getText().toString();
                if(!msg.equals("")){
                    sendMessage(firebaseUser.getUid(),userId,msg) ;
                }else{
                    Toast.makeText(MainChatActivity.this, "You can't send empty message.", Toast.LENGTH_SHORT).show();
                }
                writeMessage.setText("");
            }
        });

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserData userData = dataSnapshot.getValue(UserData.class);
                userName.setText(userData.getName());
                profilePic.setImageResource(R.drawable.profile);
                readMessage(firebaseUser.getUid(),userId,username);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void sendMessage(String sender, String receiver, String message){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("sender",sender);
        hashMap.put("receiver", receiver);
        hashMap.put("message", message);

        reference.child("Chat").push().setValue(hashMap);
    }

    private void readMessage(final String myId, final String userId,final String username){
        mChat = new ArrayList<>();

        reference = FirebaseDatabase.getInstance().getReference("Chat");

        try {
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    mChat.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        ChatData chatData = snapshot.getValue(ChatData.class);
                        if (chatData.getReceiver().equals(myId) && chatData.getSender().equals(userId) ||
                                chatData.getReceiver().equals(userId) && chatData.getSender().equals(myId)) {
                            mChat.add(chatData);
                        }
                    }
                    messageAdapter = new MessageAdapter(MainChatActivity.this, mChat,username);
                    recyclerView.setAdapter(messageAdapter);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }catch (Exception e){
            Toast.makeText(this, ""+e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
