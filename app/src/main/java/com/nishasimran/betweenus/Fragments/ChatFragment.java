package com.nishasimran.betweenus.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.nishasimran.betweenus.Activities.BubbleChatActivity;
import com.nishasimran.betweenus.Activities.MainActivity;
import com.nishasimran.betweenus.Adapters.ChatAdapter;
import com.nishasimran.betweenus.DataClasses.Key;
import com.nishasimran.betweenus.DataClasses.Message;
import com.nishasimran.betweenus.DataClasses.User;
import com.nishasimran.betweenus.Encryption.Encryption;
import com.nishasimran.betweenus.Firebase.FirebaseDb;
import com.nishasimran.betweenus.FirebaseDataClasses.FMessage;
import com.nishasimran.betweenus.R;
import com.nishasimran.betweenus.Utils.Utils;
import com.nishasimran.betweenus.Values.CommonValues;
import com.nishasimran.betweenus.Values.FirebaseValues;
import com.nishasimran.betweenus.ViewModels.KeyViewModel;
import com.nishasimran.betweenus.ViewModels.MessageViewModel;
import com.nishasimran.betweenus.ViewModels.UserViewModel;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ChatFragment extends Fragment {

    private final String TAG = "ChatFrag";

    private MainFragment mainFragment;
    private final AppCompatActivity activity;

    private ImageView navOpen, callImageView, menuImageView, sendImageView;
    private TextView nameTextView, lastSeenTextView;
    private CardView noMessagesCard;
    private RecyclerView recyclerView;
    private EditText messageEditText;
    private PopupMenu popupMenu, sendPopupMenu;

    private ChatAdapter adapter;

    private List<Message> messages;
    private List<Key> keys;

    private User serverUser = null;

    private DatabaseReference lastSeenRef;
    private final DatabaseReference messagesRef = FirebaseValues.MESSAGE_REF;
    private ChildEventListener messageChildEventListener;
    private ValueEventListener lastSeenListener;

    public UserDetailsFragment fragment;

    //final private MediaPlayer mediaPlayer;

    public ChatFragment(BubbleChatActivity activity) {
        this.activity = activity;
    }

    public ChatFragment(MainFragment fragment, MainActivity activity) {
        this.mainFragment = fragment;
        this.activity = activity;
        //this.mediaPlayer = MediaPlayer.create(getContext(), R.raw.conv_tone);
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

        initMessageReceiver();

        return parent;
    }

    private void initMessageReceiver() {
        messageChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull @NotNull DataSnapshot snapshot, @Nullable @org.jetbrains.annotations.Nullable String previousChildName) {
                if (snapshot.exists()) {
                    Map<String, Object> map = (Map<String, Object>) snapshot.getValue();
                    if (map != null) {
                        FMessage fMessage = new FMessage(map);
                        Log.d(TAG, "fMessage: " + fMessage);
                        if (fMessage.getFrom() != null && fMessage.getTo() != null && fMessage.getMessage() != null) {
                            String uid = Utils.getStringFromSharedPreference(activity.getApplication(), CommonValues.SHARED_PREFERENCE_UID);
                            if (uid.trim().equals(fMessage.getTo().trim())) {
                                Key key = KeyViewModel.getInstance(activity, activity.getApplication()).findKeyByMyPublic(fMessage.getServerPublic(), keys);
                                if (key != null) {
                                    snapshot.getRef().removeValue();
                                    long readCurrMillis = System.currentTimeMillis();
                                    Log.d(TAG, "map iv: " + fMessage.getIv());
                                    Log.d(TAG, "map myPublic: " + fMessage.getMyPublic());
                                    Log.d(TAG, "map message: " + fMessage.getMessage());
                                    String messageTxt = decryptMessage(fMessage.getMyPublic(), key.getMyPrivate(), fMessage.getIv(), fMessage.getMessage());
                                    Message message = new Message(fMessage.getId(), messageTxt, fMessage.getFrom(), fMessage.getTo(), fMessage.getMessageType(), CommonValues.STATUS_DELIVERED, readCurrMillis, null, readCurrMillis, readCurrMillis);
                                    message.setUnread(false);
                                    if (fMessage.getSentCurrMillis() != null) {
                                        message.setCurrMillis(fMessage.getSentCurrMillis());
                                        message.setSentCurrMillis(fMessage.getSentCurrMillis());
                                    }
                                    FirebaseDb.getInstance().updateMessageStatus(fMessage.getId(), CommonValues.STATUS_SEEN, readCurrMillis);
                                    insertMessage(message);
                                    //mediaPlayer.start();
                                    Integer index = adapter.getIndexOf(message);
                                    if (index != null)
                                        recyclerView.scrollToPosition(index);
                                    Key key1 = new Key(UUID.randomUUID().toString(), null, fMessage.getServerPublic(), fMessage.getMyPublic(), fMessage.getCurrMillis());
                                    insertKey(key1);
                                } else {
                                    Log.d(TAG, "key not found");
                                }
                            } else {
                                Log.d(TAG, "message not sent to you");
                                if (fMessage.getSentCurrMillis() != null) {
                                    String messageId = snapshot.getRef().getKey();
                                    Message message = MessageViewModel.getInstance(activity, activity.getApplication()).findMessage(messageId, messages);
                                    if (message != null) {
                                        Integer index = adapter.getIndexOf(message);
                                        if (message.getSentCurrMillis() == null) {
                                            message.setStatus(CommonValues.STATUS_SENT);
                                            message.setSentCurrMillis(fMessage.getSentCurrMillis());
                                            updateMessage(message, index);
                                        } else {
                                            Log.d(TAG, "sentCurrMillis not null: " + message.getSentCurrMillis());
                                        }
                                    }
                                }
                            }
                        } else if (fMessage.getSentCurrMillis() != null) {
                            String messageId = snapshot.getRef().getKey();
                            Message message = MessageViewModel.getInstance(activity, activity.getApplication()).findMessage(messageId, messages);
                            if (message != null) {
                                Integer index = adapter.getIndexOf(message);
                                if (message.getSentCurrMillis() == null) {
                                    message.setStatus(CommonValues.STATUS_SENT);
                                    message.setSentCurrMillis(fMessage.getSentCurrMillis());
                                    updateMessage(message, index);
                                } else {
                                    Log.d(TAG, "sentCurrMillis not null: " + message.getSentCurrMillis());
                                }

                                if (fMessage.getDeliveredCurrMillis() != null) {
                                    if (message.getDeliveredCurrMillis() == null) {
                                        message.setStatus(CommonValues.STATUS_DELIVERED);
                                        message.setDeliveredCurrMillis(fMessage.getDeliveredCurrMillis());
                                        updateMessage(message, index);
                                    } else {
                                        Log.d(TAG, "sentCurrMillis not null: " + message.getSentCurrMillis());
                                    }
                                }

                                if (fMessage.getReadCurrMillis() != null) {
                                    if (message.getReadCurrMillis() == null) {
                                        message.setStatus(CommonValues.STATUS_SEEN);
                                        message.setReadCurrMillis(fMessage.getReadCurrMillis());
                                        updateMessage(message, index);
                                    } else {
                                        Log.d(TAG, "sentCurrMillis not null: " + message.getSentCurrMillis());
                                    }
                                }
                            }
                        } else if (fMessage.getDeliveredCurrMillis() != null) {
                            String messageId = snapshot.getRef().getKey();
                            Message message = MessageViewModel.getInstance(activity, activity.getApplication()).findMessage(messageId, messages);
                            if (message != null) {
                                Integer index = adapter.getIndexOf(message);
                                if (message.getDeliveredCurrMillis() == null) {
                                    message.setStatus(CommonValues.STATUS_DELIVERED);
                                    message.setDeliveredCurrMillis(fMessage.getDeliveredCurrMillis());
                                    updateMessage(message, index);
                                } else {
                                    Log.d(TAG, "sentCurrMillis not null: " + message.getSentCurrMillis());
                                }
                            }
                        } else if (fMessage.getReadCurrMillis() != null) {
                            String messageId = snapshot.getRef().getKey();
                            Message message = MessageViewModel.getInstance(activity, activity.getApplication()).findMessage(messageId, messages);
                            if (message != null) {
                                Integer index = adapter.getIndexOf(message);
                                if (message.getReadCurrMillis() == null) {
                                    message.setStatus(CommonValues.STATUS_SEEN);
                                    message.setReadCurrMillis(fMessage.getReadCurrMillis());
                                    updateMessage(message, index);
                                } else {
                                    Log.d(TAG, "sentCurrMillis not null: " + message.getSentCurrMillis());
                                }
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
        };
        messagesRef.addChildEventListener(messageChildEventListener);
    }

    private void insertKey(Key key) {
        KeyViewModel.getInstance(activity, activity.getApplication()).insert(key);
    }

    private void populateTheChats() {
        Log.d(TAG, "populateTheChats");
        initMessagesListener();
        initKeyListener();
    }

    private void initMessagesListener() {
        MessageViewModel.getInstance(activity, activity.getApplication()).getHundredMessages(0).observe(activity, messages1 -> {
            if (messages1 != null) {
                Log.d(TAG, "messages: " + messages1);
                messages = messages1;
                adapter.setMessages(messages1);
                if (messages1.isEmpty()) {
                    noMessages(true);
                } else {
                    recyclerView.scrollToPosition(messages1.size() - 1);
                    noMessages(false);
                }
            } else {
                noMessages(true);
            }
        });
    }

    @Override
    public void onResume() {
        Log.d(TAG, "onResume()");
        if (mainFragment != null) {
            if (mainFragment.isDocsExpanded()) {
                mainFragment.setDocsExpanded(false);
            }
            mainFragment.checkMenuItem(R.id.menu_chat);
        }
        Log.d(TAG, "onResume: now super");
        super.onResume();
    }

    private void listenersForViews() {
        Log.d(TAG, "listenersForViews");
        if (mainFragment != null && activity instanceof MainActivity) {
            navOpen.setOnClickListener(v -> mainFragment.openDrawer());
            nameTextView.setOnClickListener(v -> {
                if (serverUser != null) {
                    fragment = new UserDetailsFragment((MainActivity) activity, serverUser);
                    Utils.showFragment(activity.getSupportFragmentManager(), R.id.root_fragment_container, fragment);
                }
            });
        } else {
            navOpen.setVisibility(View.GONE);
        }

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

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                /*
                 -1 = up
                 1 = down
                 0 = returns false
                 */
                if (!recyclerView.canScrollVertically(-1)) {
                    MessageViewModel.getInstance(activity, activity.getApplication()).getHundredMessages(100).observe(activity, messages1 -> {
                        messages.addAll(0, messages1);
                        adapter.setMessages(messages);
                    });
                }
            }
        });

        //mediaPlayer.setOnCompletionListener(MediaPlayer::release);
    }

    private void sendMessage() {
        Log.d(TAG, "sendMessage");
        String serverUid = Utils.getStringFromSharedPreference(activity.getApplication(), CommonValues.SHARED_PREFERENCE_SERVER_UID);
        String uid = Utils.getStringFromSharedPreference(activity.getApplication(), CommonValues.SHARED_PREFERENCE_UID);
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
                            uid,
                            serverUid,
                            CommonValues.MESSAGE_TYPE_TEXT,
                            CommonValues.STATUS_SENDING,
                            currMillis,
                            null,
                            null,
                            null);
                    insertMessage(message);
                    activity.runOnUiThread(() -> {
                        Integer index = adapter.getIndexOf(message);
                        if (index != null)
                            recyclerView.scrollToPosition(index);
                    });

                    if (Utils.isNetworkAvailable(activity)) {
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
        insertKey(key);
    }

    private Map<String, String> encryptMessage(String text) {
        Key key = KeyViewModel.getInstance(activity, activity.getApplication()).getLastKeyWithServerPublic(keys);
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
        navOpen = parent.findViewById(R.id.chat_nav_open);
        nameTextView = parent.findViewById(R.id.chat_name);
        lastSeenTextView = parent.findViewById(R.id.chat_last_seen);
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

    private void initLastSeenListener(String serverUid) {
        lastSeenListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Object o = snapshot.getValue();
                    if (o != null) {
                        if (o instanceof String) {
                            String lastSeen = (String) o;
                            Log.d(TAG, "lastSeen str: " + lastSeen);
                            if (lastSeen.trim().isEmpty()) {
                                Log.d(TAG, "lastSeen empty");
                            } else {
                                if (lastSeen.trim().equals(CommonValues.STATUS_ONLINE)) {
                                    FirebaseDb.root.child(FirebaseValues.DISCONNECT).child(serverUid).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (snapshot.exists()) {
                                                Object disconnectLastSeen = snapshot.getValue();
                                                if (disconnectLastSeen != null) {
                                                    if (disconnectLastSeen instanceof String && !((String) disconnectLastSeen).trim().isEmpty() && CommonValues.NULL.equals(disconnectLastSeen)) {
                                                        lastSeenTextView.setText(R.string.online);
                                                    } else if (disconnectLastSeen instanceof Long) {
                                                        Long lastSeen = (Long) disconnectLastSeen;
                                                        Log.d(TAG, "lastSeen long: " + lastSeen);
                                                        String lastSeenStr = Utils.getFormattedLastSeenTime(lastSeen).trim();
                                                        Log.d(TAG, "lastSeen calculated: " + lastSeenStr);
                                                        lastSeenTextView.setText(lastSeenStr);
                                                    }
                                                } else {
                                                    lastSeenTextView.setText(R.string.online);
                                                }
                                            }
                                        }
                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) { }
                                    });
                                } else {
                                    Log.d(TAG, "last seen error");
                                }
                            }
                        } else if (o instanceof Long) {
                            Long lastSeen = (Long) o;
                            Log.d(TAG, "lastSeen long: " + lastSeen);
                            String lastSeenStr = Utils.getFormattedLastSeenTime(lastSeen).trim();
                            Log.d(TAG, "lastSeen calculated: " + lastSeenStr);
                            lastSeenTextView.setText(lastSeenStr);
                        } else {
                            Log.d(TAG, "lastSeen class: " + o.getClass().getName() + o.toString());
                        }
                    } else {
                        Log.d(TAG, "lastSeen null");
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        };
    }

    private void showNameAndDp() {
        UserViewModel.getInstance(activity, activity.getApplication()).getAllUsers().observe(activity, users -> {
            if (users != null && !users.isEmpty()) {
                Log.d(TAG, "showNameAndDp users: " + users);
                serverUser = UserViewModel.getInstance(activity, activity.getApplication()).getServerUser(users, Utils.getStringFromSharedPreference(activity.getApplication(), CommonValues.SHARED_PREFERENCE_SERVER_UID));
                if (serverUser != null) {
                    nameTextView.setText(serverUser.getName());
                    initLastSeenListener(serverUser.getId());
                    updateLastSeenListeners(serverUser.getId());
                }
            }
        });
    }

    private void updateLastSeenListeners(String serverUid) {
        if (lastSeenListener != null) {
            if (lastSeenRef != null) {
                lastSeenRef.removeEventListener(lastSeenListener);
            }
            lastSeenRef = FirebaseDb.root.child(FirebaseValues.LAST_SEEN).child(serverUid);

            lastSeenRef.addValueEventListener(lastSeenListener);
        } else {
            initLastSeenListener(serverUid);
        }
    }

    private void initRecyclerView() {
        Log.d(TAG, "initRecyclerView");
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        String uid = Utils.getStringFromSharedPreference(activity.getApplication(), CommonValues.SHARED_PREFERENCE_UID);
        adapter = new ChatAdapter(this, uid);

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

    @Override
    public void onPause() {
        super.onPause();
    }

    private void insertMessage(Message message) {
        MessageViewModel.getInstance(activity, activity.getApplication()).insert(message);
    }

    private void updateMessage(Message message, Integer index) {
        MessageViewModel.getInstance(activity, activity.getApplication()).update(message);
        if (index != null)
            adapter.updateMessage(index, message);
    }

    public void noMessages(boolean value) {
        noMessagesCard.setVisibility(value?View.VISIBLE:View.GONE);
    }

    private void initKeyListener() {
        KeyViewModel.getInstance(activity, activity.getApplication()).getAllKeys().observe(activity, keys1 -> keys = keys1);
    }

    public void removeMessageListener() {
        messagesRef.removeEventListener(messageChildEventListener);
    }
}