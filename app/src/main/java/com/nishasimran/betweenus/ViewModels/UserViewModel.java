package com.nishasimran.betweenus.ViewModels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.nishasimran.betweenus.DataClasses.User;
import com.nishasimran.betweenus.Repositories.UserRepository;

import java.util.List;

public class UserViewModel extends AndroidViewModel {

    private final String TAG = "UserVM";
    private static UserViewModel INSTANCE = null;

    private final UserRepository mRepository;

    public UserViewModel (Application application) {
        super(application);
        mRepository = new UserRepository(application);
    }

    public static UserViewModel getInstance(@NonNull ViewModelStoreOwner owner, @NonNull Application application) {
        if (INSTANCE == null) {
            ViewModelProvider.AndroidViewModelFactory factory = new ViewModelProvider.AndroidViewModelFactory(application);
            INSTANCE = new ViewModelProvider(owner, factory).get(UserViewModel.class);
        }
        return INSTANCE;
    }

    public LiveData<List<User>> getAllUsers() { return mRepository.getAllUsers(); }

    public void insert(User user) { mRepository.insert(user); }

    public void update(User user) { mRepository.update(user); }

    public void delete(User user) { mRepository.delete(user); }

    public void deleteAll() { mRepository.deleteAll(); }

    public User getCurrentUser(List<User> users) {
        return mRepository.getCurrentUser(users);
    }

    public User getServerUser(List<User> users) {
        return mRepository.getServerUser(users);
    }

    public User findUserById(String id, List<User> users) {
        return mRepository.findUserById(id, users);
    }
}
