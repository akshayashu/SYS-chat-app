package com.example.seeyousoon.MessagesSection;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.agrawalsuneet.dotsloader.loaders.BounceLoader;
import com.agrawalsuneet.dotsloader.loaders.ZeeLoader;
import com.example.seeyousoon.R;
import com.example.seeyousoon.data.ChatData;
import com.google.android.material.tabs.TabLayout;
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

public class MessageMenuActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private TabLayout tabLayout;
    private TextView load;
    private BounceLoader bounceLoad;

    DatabaseReference reference;
    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_menu);

        bounceLoad = findViewById(R.id.bounce);
        load = findViewById(R.id.loading);

        BounceLoader bounceLoader = new BounceLoader(this,
                40,
                ContextCompat.getColor(this, R.color.colorPrimary),
                true,
                ContextCompat.getColor(this, R.color.colorAccent));

        bounceLoader.setAnimDuration(1000);
        bounceLoad.addView(bounceLoader);

        viewPager = findViewById(R.id.viewPager1);
        tabLayout = findViewById(R.id.tabLayout1);

        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void setupViewPager(final ViewPager viewPager){

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chat");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                int unread = 0;
                ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    ChatData chatData = snapshot.getValue(ChatData.class);
                    if (chatData.getReceiver().equals(firebaseUser.getUid()) && !chatData.isIsseen()){
                        unread++;
                    }
                }

                if (unread == 0){
                    adapter.addFrag(new BuddiesFragment(),"Buddies");
                } else {
                    adapter.addFrag(new BuddiesFragment(),"Buddies("+unread+")");
                }
                adapter.addFrag(new ContactsFragment(),"Contacts");
                bounceLoad.removeAllViews();
                bounceLoad.setVisibility(View.GONE);
                load.setVisibility(View.GONE);
                viewPager.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    static class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitle = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitle.add(title);
        }

        void update(int pos, String title){
            mFragmentTitle.set(pos,title);
        }

        @Override
        public void notifyDataSetChanged() {
            super.notifyDataSetChanged();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitle.get(position);
        }
    }

    private void status(String status){

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("User Data").child(firebaseUser.getUid());

        HashMap<String, Object> hashMap =  new HashMap<>();
        hashMap.put("status", status);

        reference.updateChildren(hashMap);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        status("Online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        status("Offline");
    }
}
