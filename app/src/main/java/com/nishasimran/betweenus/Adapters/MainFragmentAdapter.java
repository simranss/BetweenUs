package com.nishasimran.betweenus.Adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.nishasimran.betweenus.Fragments.MainFragment;

import java.util.ArrayList;

public class MainFragmentAdapter extends FragmentStateAdapter {

    private final ArrayList<Fragment> fragments;

    public MainFragmentAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
        fragments = new ArrayList<>();
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return fragments.get(position);
    }

    @Override
    public int getItemCount() {
        return fragments.size();
    }

    public void addFrag(Fragment fragment) {
        fragments.add(fragment);
    }
}
