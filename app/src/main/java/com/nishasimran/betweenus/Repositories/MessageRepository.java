package com.nishasimran.betweenus.Repositories;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.nishasimran.betweenus.DAOs.MessageDao;
import com.nishasimran.betweenus.DataClasses.Message;
import com.nishasimran.betweenus.Database.CommonDatabase;

import java.util.List;

public class MessageRepository {

    private final MessageDao messageDao;
    private final LiveData<List<Message>> allMessages;

    // Note that in order to unit test the MessageRepository, you have to remove the Application
    // dependency. This adds complexity and much more code, and this sample is not about testing.
    // See the BasicSample in the android-architecture-components repository at
    // https://github.com/googlesamples
    public MessageRepository(Application application) {
        CommonDatabase db = CommonDatabase.getDatabase(application);
        messageDao = db.messageDao();
        allMessages = messageDao.getAllMessages();
    }

    // Room executes all queries on a separate thread.
    // Observed LiveData will notify the observer when the data has changed.
    public LiveData<List<Message>> getAllMessages() {
        return allMessages;
    }

    // You must call this on a non-UI thread or your app will throw an exception. Room ensures
    // that you're not doing any long running operations on the main thread, blocking the UI.
    public void insert(Message message) {
        CommonDatabase.databaseWriteExecutor.execute(() -> messageDao.insert(message));
    }

    public void update(Message message) {
        CommonDatabase.databaseWriteExecutor.execute(() -> messageDao.update(message));
    }

    public void delete(Message message) {
        CommonDatabase.databaseWriteExecutor.execute(() -> messageDao.delete(message));
    }

    public void deleteAll() {
        CommonDatabase.databaseWriteExecutor.execute(messageDao::deleteAll);
    }

    public Message findMessage(String id, List<Message> messages) {
        if (messages != null) {
            for (Message message : messages) {
                if (id.equals(message.getId())) {
                    return message;
                }
            }
        }
        return null;
    }
}
