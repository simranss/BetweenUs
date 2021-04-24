package com.nishasimran.betweenus.ViewModels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.nishasimran.betweenus.DataClasses.Message;
import com.nishasimran.betweenus.Repositories.MessageRepository;

import java.util.List;

public class MessageViewModel extends AndroidViewModel {

    private final String TAG = "MessageVM";
    private static MessageViewModel INSTANCE = null;

    private final MessageRepository mRepository;

    private final LiveData<List<Message>> mAllMessages;

    public MessageViewModel (Application application) {
        super(application);
        mRepository = new MessageRepository(application);
        mAllMessages = mRepository.getAllMessages();
    }

    public static MessageViewModel getInstance(@NonNull ViewModelStoreOwner owner, @NonNull Application application) {
        if (INSTANCE == null) {
            ViewModelProvider.AndroidViewModelFactory factory = new ViewModelProvider.AndroidViewModelFactory(application);
            INSTANCE = new ViewModelProvider(owner, factory).get(MessageViewModel.class);
        }
        return INSTANCE;
    }

    LiveData<List<Message>> getAllMessages() { return mAllMessages; }

    public void insert(Message message) { mRepository.insert(message); }

    public void update(Message message) { mRepository.update(message); }

    public void delete(Message message) { mRepository.delete(message); }

    public void deleteAll() { mRepository.deleteAll(); }

    List<Message> findMessages(String text) {
        return mRepository.findMessages(text);
    }
}
