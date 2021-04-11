package com.nishasimran.betweenus.ViewModels;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.nishasimran.betweenus.DataClasses.Message;
import com.nishasimran.betweenus.Repositories.MessageRepository;

import java.util.List;

public class MessageViewModel extends AndroidViewModel {

    private final MessageRepository mRepository;

    private LiveData<List<Message>> mAllMessages;

    public MessageViewModel (Application application) {
        super(application);
        mRepository = new MessageRepository(application);
        mAllMessages = mRepository.getAllMessages();
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
