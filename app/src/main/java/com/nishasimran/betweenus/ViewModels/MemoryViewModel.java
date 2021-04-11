package com.nishasimran.betweenus.ViewModels;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.nishasimran.betweenus.DataClasses.Memory;
import com.nishasimran.betweenus.Repositories.MemoryRepository;

import java.util.List;

public class MemoryViewModel extends AndroidViewModel {

    private final MemoryRepository repository;

    private LiveData<List<Memory>> allMemories;

    public MemoryViewModel (Application application) {
        super(application);
        repository = new MemoryRepository(application);
        allMemories = repository.getAllMemories();
    }

    LiveData<List<Memory>> getAllMemories() { return allMemories; }

    public void insert(Memory memory) { repository.insert(memory); }

    public void update(Memory memory) { repository.update(memory); }

    public void delete(Memory memory) { repository.delete(memory); }

    public void deleteAll() { repository.deleteAll(); }

    List<Memory> findMemories(String text) {
        return repository.findMemories(text);
    }
}
