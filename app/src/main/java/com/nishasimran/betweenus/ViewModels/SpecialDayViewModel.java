package com.nishasimran.betweenus.ViewModels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.nishasimran.betweenus.DataClasses.SpecialDay;
import com.nishasimran.betweenus.Repositories.SpecialDayRepository;

import java.util.List;

public class SpecialDayViewModel extends AndroidViewModel {

    private final String TAG = "SpecialDayVM";
    private static SpecialDayViewModel INSTANCE = null;

    private final SpecialDayRepository repository;

    private final LiveData<List<SpecialDay>> allSpecialDays;

    public SpecialDayViewModel (Application application) {
        super(application);
        repository = new SpecialDayRepository(application);
        allSpecialDays = repository.getAllSpecialDays();
    }

    public static SpecialDayViewModel getInstance(@NonNull ViewModelStoreOwner owner, @NonNull Application application) {
        if (INSTANCE == null) {
            ViewModelProvider.AndroidViewModelFactory factory = new ViewModelProvider.AndroidViewModelFactory(application);
            INSTANCE = new ViewModelProvider(owner, factory).get(SpecialDayViewModel.class);
        }
        return INSTANCE;
    }

    LiveData<List<SpecialDay>> getAllSpecialDays() { return allSpecialDays; }

    public void insert(SpecialDay specialDay) { repository.insert(specialDay); }

    public void update(SpecialDay specialDay) { repository.update(specialDay); }

    public void delete(SpecialDay specialDay) { repository.delete(specialDay); }

    public void deleteAll() { repository.deleteAll(); }

    SpecialDay findSpecialDay(String dayId, List<SpecialDay> specialDays) {
        return repository.findSpecialDay(dayId, specialDays);
    }
}
