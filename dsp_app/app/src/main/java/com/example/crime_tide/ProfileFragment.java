package com.example.crime_tide;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class ProfileFragment extends Fragment {
    public static boolean[] is_mute_all = {false};
    public static boolean[] is_mute_specific = {false};
    CheckBox checkBox_all;
    CheckBox checkBox_specific;
    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_profile, container, false);

        final SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        boolean isMyValueChecked = sharedPref.getBoolean("checkbox", false);
        checkBox_all = v.findViewById(R.id.checkBox_mute_all);
        checkBox_all.setChecked(isMyValueChecked);
        checkBox_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putBoolean("checkbox", ((CheckBox) view).isChecked());
                editor.commit();
                if(checkBox_all.isChecked()){
                    getActivity().startService(new Intent(getActivity(), HomeActivity.class));
                    is_mute_all[0] = true;
                }else{
                    getActivity().stopService(new Intent(getActivity(), HomeActivity.class));
                    is_mute_all[0] = false;
                }
            }
        });
        final SharedPreferences sharedPref2 = getActivity().getPreferences(Context.MODE_PRIVATE);
        boolean isMyValueChecked2 = sharedPref2.getBoolean("checkbox1", false);
        checkBox_specific = v.findViewById(R.id.checkBox_mute_specific);
        checkBox_specific.setChecked(isMyValueChecked2);
        checkBox_specific.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = sharedPref2.edit();
                editor.putBoolean("checkbox1", ((CheckBox) view).isChecked());
                editor.commit();
                if(checkBox_specific.isChecked()){
                    getActivity().startService(new Intent(getActivity(), HomeActivity.class));
                    is_mute_specific[0] = true;
                }else{
                    getActivity().stopService(new Intent(getActivity(), HomeActivity.class));
                    is_mute_specific[0] = false;
                }
            }
        });
        System.out.println(Arrays.toString(is_mute_all));
        System.out.println(Arrays.toString(is_mute_specific));
        return v;
    }
}
