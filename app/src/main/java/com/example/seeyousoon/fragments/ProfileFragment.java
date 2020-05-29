package com.example.seeyousoon.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.seeyousoon.Login;
import com.example.seeyousoon.R;
import com.example.seeyousoon.SelectedPostGridview;
import com.example.seeyousoon.data.UserData;
import com.google.firebase.auth.FirebaseAuth;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {

    GridView gridView;
    Button settings;
    TextView postNo, contacts, userName, userAbout, userLocation;

    FirebaseAuth mAuth;

    UserData userData = new UserData();

    int[] posts = {R.drawable.profile,R.drawable.small_waterfall,R.drawable.forest_waterfall,
            R.drawable.brooklyn_bridge,R.drawable.eifell_tower,R.drawable.mountain_sunrise,R.drawable.indescribable};

    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        mAuth = FirebaseAuth.getInstance();

        gridView = view.findViewById(R.id.gridView);
        settings = view.findViewById(R.id.settings);
        postNo = view.findViewById(R.id.postNo);
        contacts = view.findViewById(R.id.contacts);
        userName = view.findViewById(R.id.userName);
        userAbout = view.findViewById(R.id.userAbout);
        userLocation = view.findViewById(R.id.location);

        final String MY_PREFS_NAME = "MyPrefsFile";
        SharedPreferences prefs = this.getActivity().getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        String name = prefs.getString("name", "");//"No name defined" is the default value.
        int noOfPosts = prefs.getInt("post", 0);
        int noOfContacts = prefs.getInt("contact",0);
        String about = prefs.getString("about","");
        String location = prefs.getString("location","Access not granted");
        String imageUrl = prefs.getString("profileImageUrl","default");

        userName.setText(name);
        userAbout.setText(about);
        postNo.setText(String.valueOf(noOfPosts));
        contacts.setText(String.valueOf(noOfContacts));
        userLocation.setText(location);

        userData.setUID(mAuth.getCurrentUser().getUid());
        userData.setName(name);
        userData.setImageUrl(imageUrl);

        CustomAdapter customAdapter = new CustomAdapter();
        gridView.setAdapter(customAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getContext(), SelectedPostGridview.class);
                intent.putExtra("image",posts[position]);
                startActivity(intent);
            }
        });

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                startActivity(new Intent(getContext(), Login.class));
                getActivity().finishAffinity();
            }
        });

        return view;
    }

    private class CustomAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return posts.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view1 = getLayoutInflater().inflate(R.layout.activity_posts_grid_view,null);

            ImageView image = view1.findViewById(R.id.image);

            image.setImageResource(posts[position]);

            return view1;

        }
    }
}
