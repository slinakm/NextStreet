package com.example.nextstreet.profile;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.nextstreet.R;
import com.example.nextstreet.compose.CameraLauncher;
import com.example.nextstreet.databinding.FragmentDriverProfileBinding;
import com.example.nextstreet.home.HomeFragment;

import java.io.File;

public class DriverProfileFragment extends Fragment implements CameraLauncher {
  private static final String TAG = HomeFragment.class.getSimpleName();

  private FragmentDriverProfileBinding binding;

  @Nullable
  @Override
  public View onCreateView(
      LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    super.onCreateView(inflater, container, savedInstanceState);

    binding = FragmentDriverProfileBinding.inflate(getLayoutInflater());
    return binding.getRoot();
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    binding.logoutButton.setOnClickListener(new LogoutOnClickListener(getActivity()));
    getActivity().setTitle(getResources().getText(R.string.personal_profile));
  }

  @Override
  public File launchCamera() {
    return null;
  }

  @Override
  public File getPhotoFileUri(String photoFileName) {
    return null;
  }

  @Override
  public File writeResizedBitmap(String photoFileName, Bitmap changedBitmap, String resized) {
    return null;
  }

  @Override
  public Bitmap loadFromUri(Context context, Uri photoUri) {
    return null;
  }
}
