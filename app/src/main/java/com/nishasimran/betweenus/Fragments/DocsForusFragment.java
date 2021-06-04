package com.nishasimran.betweenus.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nishasimran.betweenus.R;

import org.jetbrains.annotations.NotNull;

public class DocsForusFragment extends Fragment {

    private final MainFragment mainFragment;

    public DocsForusFragment(MainFragment fragment) {
        mainFragment = fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (!mainFragment.isDocsExpanded()) {
            mainFragment.setDocsExpanded(true);
        }
        mainFragment.checkMenuItem(R.id.menu_docs_forus);
        // Inflate the layout for this fragment
        View parent = inflater.inflate(R.layout.fragment_docs_forus, container, false);

        return parent;
    }
}