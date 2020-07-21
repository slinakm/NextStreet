package com.example.nextstreet.ui.home.compose;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.io.File;

public class ComposeViewModel extends ViewModel {

  private MutableLiveData<String> description;

  public ComposeViewModel() {
    description = new MutableLiveData<>();
  }

  public LiveData<String> getDescription() {
    return description;
  }

}
