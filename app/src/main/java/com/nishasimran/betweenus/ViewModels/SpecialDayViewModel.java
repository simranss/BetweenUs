package com.nishasimran.betweenus.ViewModels;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.nishasimran.betweenus.DataClasses.SpecialDay;
import com.nishasimran.betweenus.Repositories.SpecialDayRepository;

import java.util.List;

public class SpecialDayViewModel extends AndroidViewModel {

    private final SpecialDayRepository repository;

    private LiveData<List<SpecialDay>> allSpecialDays;

    public SpecialDayViewModel (Application application) {
        super(application);
        repository = new SpecialDayRepository(application);
        allSpecialDays = repository.getAllSpecialDays();
    }

    LiveData<List<SpecialDay>> getAllSpecialDays() { return allSpecialDays; }

    public void insert(SpecialDay specialDay) { repository.insert(specialDay); }

    public void update(SpecialDay specialDay) { repository.update(specialDay); }

    public void delete(SpecialDay specialDay) { repository.delete(specialDay); }

    public void deleteAll() { repository.deleteAll(); }

    List<SpecialDay> findSpecialDays(String text) {
        return repository.findSpecialDays(text);
    }
}
