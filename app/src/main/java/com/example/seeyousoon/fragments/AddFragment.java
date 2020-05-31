package com.example.seeyousoon.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.seeyousoon.MessagesSection.Chat_menu_adapter;
import com.example.seeyousoon.R;
import com.example.seeyousoon.data.UserData;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AddFragment extends Fragment {

    TextView searchUser;
    RecyclerView recyclerView;
    private Chat_menu_adapter chat_menu_adapter;
    private List<UserData> mUser;

    public AddFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add, container, false);

        searchUser = view.findViewById(R.id.searchUser);
        recyclerView = view.findViewById(R.id.recyclerView);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        searchUser.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                search(s.toString().toLowerCase());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mUser = new ArrayList<>();
        readUser();

        return view;
    }

    private void search(String s) {

        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        Query query = FirebaseDatabase.getInstance().getReference("User Data").orderByChild("lowercaseName")
                .startAt(s)
                .endAt(s+"\uf0ff");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUser.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    UserData userData = snapshot.getValue(UserData.class);
                    assert  userData != null;
                    assert  firebaseUser != null;
                    if (!userData.getUID().equals(firebaseUser.getUid())){
                        mUser.add(userData);
                    }
                }
                chat_menu_adapter = new Chat_menu_adapter(getContext(),mUser,false);
                recyclerView.setAdapter(chat_menu_adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void readUser() {

        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("User Data");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (searchUser.getText().toString().equals("")) {
                    mUser.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        UserData userData = snapshot.getValue(UserData.class);

                        assert userData != null;
                        if (!userData.getUID().equals(firebaseUser.getUid())) {
                            mUser.add(userData);
                        }
                    }

                    chat_menu_adapter = new Chat_menu_adapter(getContext(), mUser, false);
                    recyclerView.setAdapter(chat_menu_adapter);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
