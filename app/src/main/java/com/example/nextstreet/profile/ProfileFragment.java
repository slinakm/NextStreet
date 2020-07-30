package com.example.nextstreet.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.nextstreet.databinding.FragmentProfileBinding;
import com.example.nextstreet.utilities.DismissOnClickListener;

public class ProfileFragment extends DialogFragment {

  private static final String TAG = ProfileFragment.class.getSimpleName();

  private FragmentProfileBinding binding;

  public static ProfileFragment newInstance() {

    Bundle args = new Bundle();

    ProfileFragment fragment = new ProfileFragment();
    fragment.setArguments(args);
    return fragment;
  }

  public View onCreateView(
      @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    binding = FragmentProfileBinding.inflate(getLayoutInflater());
    return binding.getRoot();
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    binding.logoutButton.setOnClickListener(new LogoutOnClickListener(getActivity()));
    binding.ivCancel.setOnClickListener(new DismissOnClickListener(this));
  }

  @Override
  public void onResume() {
    super.onResume();

    getDialog()
        .getWindow()
        .setLayout(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT);
  }
}
