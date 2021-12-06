package com.example.smartrade;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import com.example.smartrade.tabfragments.FragmentAdapter;
import com.google.android.material.tabs.TabLayout;

/**
 * The main activity which manages the tabs and the currently viewed page.
 */
public class MainActivity extends AppCompatActivity {

    TabLayout tabLayout;
    ViewPager2 viewPager2;
    FragmentAdapter fragmentAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tabLayout = findViewById(R.id.tab_layout);
        viewPager2 = findViewById(R.id.view_pager2);

        // Set the adaptor for the view pager.
        FragmentManager manager = getSupportFragmentManager();
        fragmentAdapter = new FragmentAdapter(manager, getLifecycle());
        viewPager2.setAdapter(fragmentAdapter);

        // Set the names for the tabs.
        tabLayout.addTab(tabLayout.newTab().setText("DASHBOARD"));
        tabLayout.addTab(tabLayout.newTab().setText("TRADE"));
        tabLayout.addTab(tabLayout.newTab().setText("LEADERBOARD"));

        // Set the tab listener.
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager2.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        // Register tab callback.
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                tabLayout.selectTab(tabLayout.getTabAt(position));
            }
        });

    }
}
