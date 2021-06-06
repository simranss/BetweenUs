package com.nishasimran.betweenus.Fragments;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;

import androidx.appcompat.widget.PopupMenu;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.nishasimran.betweenus.Adapters.ChatAdapter;
import com.nishasimran.betweenus.DataClasses.Message;
import com.nishasimran.betweenus.R;
import com.nishasimran.betweenus.Utils.BlurBuilder;
import com.nishasimran.betweenus.Utils.Utils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class ChatFragment extends Fragment {

    private final MainFragment mainFragment;

    private ConstraintLayout root;
    private ImageView navOpen, callImageView, menuImageView, sendImageView;
    private TextView nameTextView;
    private RecyclerView recyclerView;
    private EditText messageEditText;
    private PopupMenu popupMenu;

    private ChatAdapter adapter;

    private ArrayList<Message> messages;

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
        nameTextView.setOnClickListener(v -> {
            // TODO: add an activity that shows details of the partner and lets you edit them as well
        });

        callImageView.setOnClickListener(v -> {
            // TODO: add a call option in future
        });


        popupMenu.setOnMenuItemClickListener(item -> {
            // TODO: add a switch statement that has all menu id(s) as cases
            return false;
        });
        menuImageView.setOnClickListener(v -> popupMenu.show());

        sendImageView.setOnClickListener(v -> sendMessage());
    }

    private void sendMessage() {
        messageEditText.setText(null);
    }

    private void initViews(View parent) {
        root = parent.findViewById(R.id.chat_root);
        navOpen = parent.findViewById(R.id.chat_nav_open);
        nameTextView = parent.findViewById(R.id.chat_name);
        callImageView = parent.findViewById(R.id.chat_call);
        menuImageView = parent.findViewById(R.id.chat_menu);
        recyclerView = parent.findViewById(R.id.chat_recycler);
        messageEditText = parent.findViewById(R.id.chat_message);
        sendImageView = parent.findViewById(R.id.chat_send);

        createPopupMenu();

        initRecyclerView();
    }

    private void initRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        messages = new ArrayList<>();
        adapter = new ChatAdapter(this, messages, mainFragment.getUid());

        recyclerView.setAdapter(adapter);

    }

    private void createPopupMenu() {
        if (getContext() != null) {
            popupMenu = new PopupMenu(getContext(), menuImageView);
            popupMenu.inflate(R.menu.chat_menu);
        }
    }

    private void blurBackground() {
        if (getContext() != null) {
            if (root.getWidth() > 0) {
                Bitmap blurredBitmap = BlurBuilder.blur(getContext(), Utils.getBackgroundId(mainFragment.activity.getApplication()), null, null);
                root.setBackground(new BitmapDrawable(getResources(), blurredBitmap));
            } else {
                root.getViewTreeObserver().addOnGlobalLayoutListener(onGlobalLayoutListener);
            }
        }
    }

    private void initOnGlobalLayoutListener() {
        if (getContext() != null)
            onGlobalLayoutListener = () -> {
                Bitmap blurredBitmap = BlurBuilder.blur(getContext(), Utils.getBackgroundId(mainFragment.activity.getApplication()), null, null);
                if (blurredBitmap != null) {
                    root.setBackground(new BitmapDrawable(getResources(), blurredBitmap));
                } else {
                    root.setBackgroundResource(Utils.getBackgroundId(mainFragment.activity.getApplication()));
                }
            };
    }

    @Override
    public void onPause() {
        root.getViewTreeObserver().removeOnGlobalLayoutListener(onGlobalLayoutListener);
        super.onPause();
    }
}