package com.example.seeyousoon.MessagesSection;

import android.nfc.Tag;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.seeyousoon.R;
import com.example.seeyousoon.data.ChatData;
import com.example.seeyousoon.data.Chatlist;
import com.example.seeyousoon.data.UserData;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class BuddiesFragment extends Fragment {

    RecyclerView recyclerView;
    Chat_menu_adapter chat_menu_adapter;
    List<UserData> mUser;

    FirebaseUser firebaseUser;
    DatabaseReference reference;

    private List<String> usersList;

    public BuddiesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_buddies, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        usersList = new ArrayList<>();

        reference = FirebaseDatabase.getInstance().getReference("Chatlist").child(firebaseUser.getUid());
        Query query = reference.orderByChild("timestamp");
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                usersList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Log.d("datasnapshot", snapshot.getKey()+": "+snapshot.getValue());
                    Chatlist chatlist = snapshot.getValue(Chatlist.class);
                    usersList.add(chatlist.getId());
                    ChatList();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        query.addValueEventListener(valueEventListener);
        return view;
    }

    private void ChatList() {
        mUser = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference("User Data");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUser.clear();
                for(String chatlist : usersList){
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                        UserData userData = snapshot.getValue(UserData.class);
                        if(userData.getUID().equals(chatlist)){
                            mUser.add(userData);
                        }
                    }
                }
                chat_menu_adapter = new Chat_menu_adapter(getContext(),mUser,true);
                recyclerView.setAdapter(chat_menu_adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

}
