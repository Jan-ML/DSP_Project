package com.example.crime_tide;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import org.jetbrains.annotations.NotNull;

public class AdapterFragment extends FragmentStateAdapter {


    public AdapterFragment(@NonNull @NotNull FragmentManager fragmentManager, @NonNull @NotNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NotNull
    @Override
    public Fragment createFragment(int position) {

        switch (position){
            case 1:
                return new StatisticsFragment();
            case 2:
                return new CompareFragment();
            default:
                return new OccurredFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
