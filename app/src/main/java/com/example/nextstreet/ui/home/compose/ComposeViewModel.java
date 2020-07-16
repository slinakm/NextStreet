package com.example.nextstreet.ui.home.compose;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.io.File;

public class ComposeViewModel extends ViewModel {

    private MutableLiveData<String> description;
    private MutableLiveData<File> image;

    public ComposeViewModel() {
        description = new MutableLiveData<>();
        image = new MutableLiveData<>();
    }

    public LiveData<String> getDescription() {
        return description;
    }

    public LiveData<File> getImage() {
        return image;
    }
}
