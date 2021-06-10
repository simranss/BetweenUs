package com.nishasimran.betweenus.Fragments;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.nishasimran.betweenus.Adapters.ChatAdapter;
import com.nishasimran.betweenus.DataClasses.Key;
import com.nishasimran.betweenus.DataClasses.Message;
import com.nishasimran.betweenus.DataClasses.User;
import com.nishasimran.betweenus.Encryption.Encryption;
import com.nishasimran.betweenus.Firebase.FirebaseDb;
import com.nishasimran.betweenus.FirebaseDataClasses.FMessage;
import com.nishasimran.betweenus.R;
import com.nishasimran.betweenus.Utils.BlurBuilder;
import com.nishasimran.betweenus.Utils.Utils;
import com.nishasimran.betweenus.Values.CommonValues;
import com.nishasimran.betweenus.Values.FirebaseValues;
import com.nishasimran.betweenus.ViewModels.KeyViewModel;
import com.nishasimran.betweenus.ViewModels.MessageViewModel;
import com.nishasimran.betweenus.ViewModels.UserViewModel;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ChatFragment extends Fragment {

    private final String TAG = "ChatFrag";

    private final MainFragment mainFragment;

    private ConstraintLayout root;
    private ImageView navOpen, callImageView, menuImageView, sendImageView;
    private TextView nameTextView;
    private CardView noMessagesCard;
    private RecyclerView recyclerView;
    private EditText messageEditText;
    private PopupMenu popupMenu, sendPopupMenu;

    private ChatAdapter adapter;

    private List<Message> messages;
    private List<Key> keys;

    private User serverUser = null;

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

        populateTheChats();

        listenersForViews();

        initOnGlobalLayoutListener();

        blurBackground();

        initMessageReceiver();

        return parent;
    }

    private void initMessageReceiver() {
        DatabaseReference messagesRef = FirebaseValues.MESSAGE_REF;
        messagesRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull @NotNull DataSnapshot snapshot, @Nullable @org.jetbrains.annotations.Nullable String previousChildName) {
                if (snapshot.exists()) {
                    Map<String, String> map = (Map<String, String>) snapshot.getValue();
                    if (map != null) {
                        FMessage fMessage = new FMessage(map);
                        Log.d(TAG, "fMessage: " + fMessage);
                        if (fMessage.getFrom() != null && fMessage.getTo() != null && fMessage.getMessage() != null) {
                            if (mainFragment.getUid().trim().equals(fMessage.getTo().trim())) {
                                Key key = KeyViewModel.getInstance(mainFragment.activity, mainFragment.activity.getApplication()).findKeyByMyPublic(fMessage.getServerPublic(), keys);
                                if (key != null) {
                                    snapshot.getRef().removeValue();
                                    long deliveredCurrMillis = System.currentTimeMillis();
                                    Log.d(TAG, "map iv: " + fMessage.getIv());
                                    Log.d(TAG, "map myPublic: " + fMessage.getMyPublic());
                                    Log.d(TAG, "map message: " + fMessage.getMessage());
                                    String messageTxt = decryptMessage(fMessage.getMyPublic(), key.getMyPrivate(), fMessage.getIv(), fMessage.getMessage());
                                    Message message = new Message(fMessage.getId(), messageTxt, fMessage.getFrom(), fMessage.getTo(), fMessage.getMessageType(), CommonValues.STATUS_DELIVERED, deliveredCurrMillis, null, deliveredCurrMillis, null);
                                    if (fMessage.getSentCurrMillis() != null) {
                                        message.setSentCurrMillis(fMessage.getSentCurrMillis());
                                    }
                                    FirebaseDb.getInstance().updateMessageStatus(fMessage.getId(), CommonValues.STATUS_DELIVERED, deliveredCurrMillis);
                                    messages.add(message);
                                    insertMessage(message);
                                    adapter.notifyItemInserted(messages.indexOf(message));
                                    recyclerView.scrollToPosition(messages.indexOf(message));
                                    Key key1 = new Key(UUID.randomUUID().toString(), null, fMessage.getServerPublic(), fMessage.getMyPublic(), fMessage.getCurrMillis());
                                    mainFragment.insertKey(key1);
                                } else {
                                    Log.d(TAG, "key not found");
                                }
                            } else {
                                Log.d(TAG, "message not sent to you");
                                if (fMessage.getSentCurrMillis() != null) {
                                    String messageId = snapshot.getRef().getKey();
                                    Message message = MessageViewModel.getInstance(mainFragment.activity, mainFragment.activity.getApplication()).findMessage(messageId, messages);
                                    message.setSentCurrMillis(fMessage.getSentCurrMillis());
                                    updateMessage(message);
                                }
                            }
                        } else if (fMessage.getSentCurrMillis() != null) {
                            String messageId = snapshot.getRef().getKey();
                            Message message = MessageViewModel.getInstance(mainFragment.activity, mainFragment.activity.getApplication()).findMessage(messageId, messages);
                            if (message.getSentCurrMillis() == null) {
                                message.setSentCurrMillis(fMessage.getSentCurrMillis());
                                updateMessage(message);
                            } else {
                                Log.d(TAG, "sentCurrMillis not null: " + message.getSentCurrMillis());
                            }
                        } else if (fMessage.getDeliveredCurrMillis() != null) {
                            String messageId = snapshot.getRef().getKey();
                            Message message = MessageViewModel.getInstance(mainFragment.activity, mainFragment.activity.getApplication()).findMessage(messageId, messages);
                            if (message.getDeliveredCurrMillis() == null) {
                                message.setDeliveredCurrMillis(fMessage.getDeliveredCurrMillis());
                                updateMessage(message);
                            } else {
                                Log.d(TAG, "sentCurrMillis not null: " + message.getSentCurrMillis());
                            }
                        }
                    }
                }
            }
            @Override
            public void onChildChanged(@NonNull @NotNull DataSnapshot snapshot, @Nullable @org.jetbrains.annotations.Nullable String previousChildName) { }
            @Override
            public void onChildRemoved(@NonNull @NotNull DataSnapshot snapshot) { }
            @Override
            public void onChildMoved(@NonNull @NotNull DataSnapshot snapshot, @Nullable @org.jetbrains.annotations.Nullable String previousChildName) { }
            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) { }
        });
    }

    private void populateTheChats() {
        Log.d(TAG, "populateTheChats");
        messages.clear();
        initMessagesListener();
        initKeyListener();
    }

    private void initMessagesListener() {
        MessageViewModel.getInstance(mainFragment.activity, mainFragment.activity.getApplication()).getAllMessages().observe(mainFragment.activity, messages1 -> {
            if (messages.size() < messages1.size()) {
                messages.clear();
                messages.addAll(messages1);
                adapter.notifyDataSetChanged();
                recyclerView.scrollToPosition(messages.size() - 1);
                noMessages(false);
            }
            if (messages1.isEmpty()) {
                noMessages(true);
            }
        });
    }

    @Override
    public void onResume() {
        Log.d(TAG, "onResume()");
        if (mainFragment.isDocsExpanded()) {
            mainFragment.setDocsExpanded(false);
        }
        mainFragment.checkMenuItem(R.id.menu_chat);
        Log.d(TAG, "onResume: now super");
        super.onResume();
    }

    private void listenersForViews() {
        Log.d(TAG, "listenersForViews");
        navOpen.setOnClickListener(v -> mainFragment.openDrawer());
        nameTextView.setOnClickListener(v -> {
            // TODO: add an activity that shows details of the partner and lets you edit them as well
        });

        callImageView.setOnClickListener(v -> {
            // TODO: add a call option in future
        });

        callImageView.setOnLongClickListener(v -> {
            // TODO: add a popup menu for having options for different calling options
            return false;
        });

        popupMenu.setOnMenuItemClickListener(item -> {
            // TODO: add a switch statement that has all menu id(s) as cases
            return false;
        });
        sendPopupMenu.setOnMenuItemClickListener(item -> {
            // TODO: add a switch statement that has all menu id(s) as cases
            return false;
        });
        menuImageView.setOnClickListener(v -> popupMenu.show());

        sendImageView.setOnClickListener(v -> sendMessage());
        sendImageView.setOnLongClickListener(v -> {
            sendPopupMenu.show();
            return false;
        });
    }

    private void sendMessage() {
        Log.d(TAG, "sendMessage");
        String serverUid = Utils.getStringFromSharedPreference(mainFragment.activity.getApplication(), CommonValues.SHARED_PREFERENCE_SERVER_UID);
        if (serverUid.equals(CommonValues.NULL)) {
            Toast.makeText(getContext(), "No partner found", Toast.LENGTH_SHORT).show();
        } else {
            String messageTxt = messageEditText.getText().toString().trim();
            if (!messageTxt.equals("")) {
                messageEditText.setText(null);
                new Thread(() -> {
                    long currMillis = System.currentTimeMillis();
                    Message message = new Message(Utils.getUniqueMessageId(),
                            messageTxt,
                            mainFragment.getUid(),
                            serverUid,
                            CommonValues.MESSAGE_TYPE_TEXT,
                            CommonValues.STATUS_SENDING,
                            currMillis,
                            null,
                            null,
                            null);
                    messages.add(message);
                    insertMessage(message);
                    mainFragment.activity.runOnUiThread(() -> {
                        adapter.notifyItemInserted(messages.indexOf(message));
                        recyclerView.scrollToPosition(messages.indexOf(message));
                    });

                    if (mainFragment.isInternetAvailable()) {
                        Map<String, String> map = encryptMessage(message.getMessage());
                        if (map != null)
                            createAndSendMessage(map, message);
                    }

                }).start();
            }
        }
    }

    private void createAndSendMessage(Map<String, String> map, Message message) {
        String serverPublic = map.get(CommonValues.SERVER_KEY);
        String myPublic = map.get(CommonValues.MY_PUBLIC_KEY);
        String myPrivate = map.get(CommonValues.MY_PRIVATE_KEY);
        String iv = map.get(CommonValues.IV);
        FMessage fMessage = new FMessage(message.getId(), map.get(CommonValues.ENCRYPTED_MESSAGE), message.getFrom(), message.getTo(), message.getMessageType(), CommonValues.STATUS_SENT, message.getCurrMillis(), null, null, null, serverPublic, myPublic, iv);
        FirebaseDb.getInstance().sendMessage(fMessage);
        Key key = new Key(UUID.randomUUID().toString(), myPrivate, myPublic, serverPublic, message.getCurrMillis());
        mainFragment.insertKey(key);
    }

    private Map<String, String> encryptMessage(String text) {
        Key key = KeyViewModel.getInstance(mainFragment, mainFragment.activity.getApplication()).getLastKeyWithServerPublic(keys);
        if (key != null) {
            String serverPublic = key.getServerPublic();
            return Encryption.encryptText(text, serverPublic);
        }
        return null;
    }

    private String decryptMessage(String serverPublic, String myPrivateKey, String iv, String encryptedMessage) {
        return Encryption.decryptText(serverPublic, myPrivateKey, iv, encryptedMessage);
    }

    private void initViews(View parent) {
        Log.d(TAG, "initViews");
        root = parent.findViewById(R.id.chat_root);
        navOpen = parent.findViewById(R.id.chat_nav_open);
        nameTextView = parent.findViewById(R.id.chat_name);
        callImageView = parent.findViewById(R.id.chat_call);
        menuImageView = parent.findViewById(R.id.chat_menu);
        noMessagesCard = parent.findViewById(R.id.chat_no_messages);
        recyclerView = parent.findViewById(R.id.chat_recycler);
        messageEditText = parent.findViewById(R.id.chat_message);
        sendImageView = parent.findViewById(R.id.chat_send);

        createPopupMenu();

        initRecyclerView();

        showNameAndDp();
    }

    private void showNameAndDp() {
        UserViewModel.getInstance(mainFragment.activity, mainFragment.activity.getApplication()).getAllUsers().observe(mainFragment.activity, users -> {
            if (users != null && !users.isEmpty()) {
                Log.d(TAG, "showNameAndDp users: " + users);
                serverUser = UserViewModel.getInstance(mainFragment.activity, mainFragment.activity.getApplication()).getServerUser(users);
                if (serverUser != null) {
                    nameTextView.setText(serverUser.getName());
                }
            }
        });
    }

    private void initRecyclerView() {
        Log.d(TAG, "initRecyclerView");
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        messages = new ArrayList<>();
        adapter = new ChatAdapter(this, messages, mainFragment.getUid());

        recyclerView.setAdapter(adapter);
    }

    private void createPopupMenu() {
        Log.d(TAG, "createPopupMenu");
        if (getContext() != null) {
            popupMenu = new PopupMenu(getContext(), menuImageView);
            popupMenu.inflate(R.menu.chat_menu);

            sendPopupMenu = new PopupMenu(getContext(), sendImageView);
            sendPopupMenu.inflate(R.menu.chat_send_menu);
        }
    }

    private void blurBackground() {
        Log.d(TAG, "blurBackground");
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
        Log.d(TAG, "initOnGlobalLayoutListener");
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

    private void insertMessage(Message message) {
        MessageViewModel.getInstance(mainFragment.activity, mainFragment.activity.getApplication()).insert(message);
    }

    private void updateMessage(Message message) {
        MessageViewModel.getInstance(mainFragment.activity, mainFragment.activity.getApplication()).update(message);
    }

    public void noMessages(boolean value) {
        noMessagesCard.setVisibility(value?View.VISIBLE:View.GONE);
    }

    private void initKeyListener() {
        KeyViewModel.getInstance(mainFragment.activity, mainFragment.activity.getApplication()).getAllKeys().observe(mainFragment.activity, keys1 -> keys = keys1);
    }
}