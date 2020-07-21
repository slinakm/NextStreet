package com.example.nextstreet.login;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class LoginViewModel extends ViewModel {

  private MutableLiveData<String> username;
  private MutableLiveData<String> password;

  public LoginViewModel() {
    username = new MutableLiveData<>();
    password = new MutableLiveData<>();
  }

  public LiveData<String> getUsername() {
    return username;
  }

  public LiveData<String> getPassword() {
    return password;
  }
}
