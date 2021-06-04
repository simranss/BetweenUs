package com.nishasimran.betweenus.Fragments;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import com.nishasimran.betweenus.R;
import com.nishasimran.betweenus.Utils.BlurBuilder;

import org.jetbrains.annotations.NotNull;

public class ChatFragment extends Fragment {

    private final MainFragment mainFragment;

    private ConstraintLayout root;
    private ImageView navOpen;

    private ViewTreeObserver.OnGlobalLayoutListener onGlobalLayoutListener;

    public ChatFragment(MainFragment fragment) {
        this.mainFragment = fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View parent = inflater.inflate(R.layout.fragment_chat, container, false);

        initViews(parent);

        listenersForViews();

        initOnGlobalLayoutListener();

        blurBackground();



        return parent;
    }

    @Override
    public void onResume() {
        if (mainFragment.isDocsExpanded()) {
            mainFragment.setDocsExpanded(false);
        }
        mainFragment.checkMenuItem(R.id.menu_chat);
        super.onResume();
    }

    private void listenersForViews() {
        navOpen.setOnClickListener(v -> mainFragment.openDrawer());
    }

    private void initViews(View parent) {
        root = parent.findViewById(R.id.chat_root);
        navOpen = parent.findViewById(R.id.chat_nav_open);

    }

    private void blurBackground() {
        if (getContext() != null) {
            if (root.getWidth() > 0) {
                Bitmap blurredBitmap = BlurBuilder.blur(getContext(), R.drawable.background_01_img, 0.7f, null);
                root.setBackground(new BitmapDrawable(getResources(), blurredBitmap));
            } else {
                root.getViewTreeObserver().addOnGlobalLayoutListener(onGlobalLayoutListener);
            }
        }
    }

    private void initOnGlobalLayoutListener() {
        if (getContext() != null)
            onGlobalLayoutListener = () -> {
                Bitmap blurredBitmap = BlurBuilder.blur(getContext(), R.drawable.background_01_img, 0.7f, null);
                if (blurredBitmap != null) {
                    root.setBackground(new BitmapDrawable(getResources(), blurredBitmap));
                } else {
                    root.setBackgroundResource(R.drawable.background_01_img);
                }
            };
    }

    @Override
    public void onPause() {
        root.getViewTreeObserver().removeOnGlobalLayoutListener(onGlobalLayoutListener);
        super.onPause();
    }
}