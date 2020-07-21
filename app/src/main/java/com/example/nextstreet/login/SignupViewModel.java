package com.example.nextstreet.login;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SignupViewModel extends ViewModel {

  private MutableLiveData<String> username;
  private MutableLiveData<String> password;
  private MutableLiveData<String> passwordAgain;
  private MutableLiveData<String> email;

  public SignupViewModel() {
    username = new MutableLiveData<>();
    password = new MutableLiveData<>();
    passwordAgain = new MutableLiveData<>();
    email = new MutableLiveData<>();
  }

  public LiveData<String> getUsername() {
    return username;
  }

  public LiveData<String> getPassword() {
    return password;
  }

  public LiveData<String> getPasswordAgain() {
    return passwordAgain;
  }

  public LiveData<String> getEmail() {
    return email;
  }
}
