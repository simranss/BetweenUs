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

    public MessageViewModel (Application application) {
        super(application);
        mRepository = new MessageRepository(application);
    }

    public static MessageViewModel getInstance(@NonNull ViewModelStoreOwner owner, @NonNull Application application) {
        if (INSTANCE == null) {
            ViewModelProvider.AndroidViewModelFactory factory = new ViewModelProvider.AndroidViewModelFactory(application);
            INSTANCE = new ViewModelProvider(owner, factory).get(MessageViewModel.class);
        }
        return INSTANCE;
    }

    public LiveData<List<Message>> getAllMessages() { return mRepository.getAllMessages(); }

    public LiveData<List<Message>> getHundredMessages(int offset) { return mRepository.getHundredMessages(offset); }

    public void insert(Message message) { mRepository.insert(message); }

    public void update(Message message) { mRepository.update(message); }

    public void delete(Message message) { mRepository.delete(message); }

    public void deleteAll() { mRepository.deleteAll(); }

    public Message findMessage(String messageId, List<Message> messages) {
        return mRepository.findMessage(messageId, messages);
    }
}
