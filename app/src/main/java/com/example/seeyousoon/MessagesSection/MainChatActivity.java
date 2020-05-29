package com.example.seeyousoon.MessagesSection;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
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

    ValueEventListener seenListener;

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
        seenMessage(userId);
    }

    private void seenMessage(final String userId){
        reference = FirebaseDatabase.getInstance().getReference("Chat");
        seenListener = reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    ChatData chatData = snapshot.getValue(ChatData.class);
                    if(chatData.getReceiver().equals(firebaseUser.getUid()) && chatData.getSender().equals(userId)){
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("isseen",true);
                        snapshot.getRef().updateChildren(hashMap);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void sendMessage(String sender, final String receiver, String message){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("sender",sender);
        hashMap.put("receiver", receiver);
        hashMap.put("message", message);
        hashMap.put("isseen",false);

        reference.child("Chat").push().setValue(hashMap);

        final DatabaseReference chatReference = FirebaseDatabase.getInstance().getReference("Chatlist")
                .child(firebaseUser.getUid())
                .child(receiver);

        final DatabaseReference chatReference2 = FirebaseDatabase.getInstance().getReference("Chatlist")
                .child(receiver)
                .child(firebaseUser.getUid());

        chatReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()){
                    chatReference.child("id").setValue(receiver);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        chatReference2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()){
                    chatReference2.child("id").setValue(firebaseUser.getUid());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void readMessage(final String myId, final String userId,final String username){
        mChat = new ArrayList<>();

        reference = FirebaseDatabase.getInstance().getReference("Chat");

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
    }

    private void status(String status){

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("User Data").child(firebaseUser.getUid());

        HashMap<String, Object> hashMap =  new HashMap<>();
        hashMap.put("status", status);

        reference.updateChildren(hashMap);
    }

    @Override
    protected void onResume() {
        super.onResume();
        status("Online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        reference.removeEventListener(seenListener);
        status("Offline");
    }
}
