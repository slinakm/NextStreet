package com.example.nextstreet.login;

public interface FBLoginResponder {
  void respondToFBLoginSuccessLogin();

  void respondToFBLoginSuccessSignUp();

  void respondToFBLoginFailure();
}
