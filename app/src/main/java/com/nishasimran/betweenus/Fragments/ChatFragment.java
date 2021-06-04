package com.nishasimran.betweenus.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nishasimran.betweenus.R;

public class ChatFragment extends Fragment {

    private final MainFragment mainFragment;

    public ChatFragment(MainFragment fragment) {
        this.mainFragment = fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mainFragment.isDocsExpanded()) {
            mainFragment.setDocsExpanded(false);
        }
        mainFragment.checkMenuItem(R.id.menu_chat);
        // Inflate the layout for this fragment
        View parent = inflater.inflate(R.layout.fragment_chat, container, false);

        return parent;
    }
}