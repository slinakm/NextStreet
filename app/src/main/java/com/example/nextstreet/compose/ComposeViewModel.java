package com.example.nextstreet.compose;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ComposeViewModel extends ViewModel {

  private MutableLiveData<String> description;

  public ComposeViewModel() {
    description = new MutableLiveData<>();
  }

  public LiveData<String> getDescription() {
    return description;
  }
}
