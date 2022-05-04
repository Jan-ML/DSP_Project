package com.example.crime_tide;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;


import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class TabFragment extends Fragment {
    TabLayout tabLayout;
    AdapterFragment adapterFragment;
    ViewPager2 viewPager2;

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_tab, container, false);

        tabLayout = v.findViewById(R.id.tablayout);
        viewPager2 = v.findViewById(R.id.viewpager2);


        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        adapterFragment = new AdapterFragment(fragmentManager, getLifecycle());
        viewPager2.setAdapter(adapterFragment);
        tabLayout.addTab(tabLayout.newTab().setText("Occurred Crimes"));
        tabLayout.addTab(tabLayout.newTab().setText("Predicted Crimes"));
        tabLayout.addTab(tabLayout.newTab().setText("Compare Crimes"));

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
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                tabLayout.selectTab(tabLayout.getTabAt(position));
            }
        });


//        AdapterFragment adapterFragment = new AdapterFragment(getParentFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
//        adapterFragment.addFragment(new OccurredFragment(), "Occurred Crimes");
//        adapterFragment.addFragment(new StatisticsFragment(), "Predicted Crimes");
//        adapterFragment.addFragment(new CompareFragment(), "Compare Crimes");
//        viewPager.setAdapter(adapterFragment);
        return v;
    }
}
