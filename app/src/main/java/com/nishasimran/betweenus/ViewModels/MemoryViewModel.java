package com.nishasimran.betweenus.ViewModels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.nishasimran.betweenus.DataClasses.Memory;
import com.nishasimran.betweenus.Repositories.MemoryRepository;

import java.util.List;

public class MemoryViewModel extends AndroidViewModel {

    private final String TAG = "MemoryVM";
    private static MemoryViewModel INSTANCE = null;

    private final MemoryRepository repository;

    private final LiveData<List<Memory>> allMemories;

    public MemoryViewModel (Application application) {
        super(application);
        repository = new MemoryRepository(application);
        allMemories = repository.getAllMemories();
    }

    public static MemoryViewModel getInstance(@NonNull ViewModelStoreOwner owner, @NonNull Application application) {
        if (INSTANCE == null) {
            ViewModelProvider.AndroidViewModelFactory factory = new ViewModelProvider.AndroidViewModelFactory(application);
            INSTANCE = new ViewModelProvider(owner, factory).get(MemoryViewModel.class);
        }
        return INSTANCE;
    }

    private LiveData<List<Memory>> getAllMemories() { return allMemories; }

    private void insert(Memory memory) { repository.insert(memory); }

    private void update(Memory memory) { repository.update(memory); }

    private void delete(Memory memory) { repository.delete(memory); }

    private void deleteAll() { repository.deleteAll(); }

    Memory findMemory(String memoryId, List<Memory> memories) {
        return repository.findMemory(memoryId, memories);
    }
}
