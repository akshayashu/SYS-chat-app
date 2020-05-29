package com.example.seeyousoon;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.KeyEvent;

import com.example.seeyousoon.fragments.AddFragment;
import com.example.seeyousoon.fragments.HomeFragment;
import com.example.seeyousoon.fragments.ProfileFragment;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);

        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
        setupTabIcons();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            viewPager.setCurrentItem(0, true);
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    private void setupTabIcons() {
        tabLayout.getTabAt(0).setIcon(R.drawable.home_icon);
        tabLayout.getTabAt(1).setIcon(R.drawable.add_icon);
        tabLayout.getTabAt(2).setIcon(R.drawable.profile_icon);
    }


    private void setupViewPager(ViewPager viewPager){
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new HomeFragment());
        adapter.addFrag(new AddFragment());
        adapter.addFrag(new ProfileFragment());
        viewPager.setAdapter(adapter);
    }
    static class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();

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

        void addFrag(Fragment fragment) {
            mFragmentList.add(fragment);
        }

    }
}
