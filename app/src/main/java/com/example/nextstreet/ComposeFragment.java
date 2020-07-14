package com.example.nextstreet;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.nextstreet.databinding.FragmentComposeBinding;
import com.example.nextstreet.databinding.FragmentProfileBinding;
import com.example.nextstreet.listeners.CameraOnClickListener;
import com.example.nextstreet.listeners.DismissOnClickListener;
import com.example.nextstreet.listeners.SnackbarOnClickListener;
import com.example.nextstreet.listeners.TextObserver;
import com.example.nextstreet.ui.trips.TripsViewModel;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;

import static android.app.Activity.RESULT_OK;

public class ComposeFragment extends DialogFragment {

    private static final String TAG = ComposeFragment.class.getSimpleName();
    private static final String photoFileName = "photo.jpg";

    private FragmentComposeBinding binding;
    private ComposeViewModel composeViewModel;

    private CameraOnClickListener cameraOnClickListener;
    private File photoFile;

    public static ComposeFragment newInstance() {
        Bundle args = new Bundle();

        ComposeFragment fragment = new ComposeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        composeViewModel =
                ViewModelProviders.of(this).get(ComposeViewModel.class);
        binding = FragmentComposeBinding.inflate(getLayoutInflater());
        binding.ivCancel.setOnClickListener(new DismissOnClickListener(TAG, this));

        cameraOnClickListener = new CameraOnClickListener(TAG, getActivity(),
                this, photoFileName);
        binding.btnCamera.setOnClickListener(cameraOnClickListener);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        composeViewModel.getDescription().observe(getViewLifecycleOwner(),
                new TextObserver(binding.etDescription));
    }

    @Override
    public void onResume() {
        super.onResume();

        getDialog().getWindow().setLayout(ViewGroup.LayoutParams.FILL_PARENT,
                ViewGroup.LayoutParams.FILL_PARENT);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == BitmapManipulation.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            File tempPhotoFile = cameraOnClickListener.getPhotoFile();

            if (resultCode == RESULT_OK
                    && tempPhotoFile != null) {
                Bitmap takenImage = BitmapFactory.decodeFile(tempPhotoFile.getAbsolutePath());

                Bitmap resizedBitmap = BitmapManipulation.scaleToFitWidth(takenImage,
                        (int) getResources().getDimension((R.dimen.resized_post_image)));

                binding.ivPackage.setImageBitmap(resizedBitmap);
                binding.ivPackage.setVisibility(View.VISIBLE);

                photoFile =
                        BitmapManipulation.writeResizedBitmap(getContext(), photoFileName,
                                resizedBitmap, "resized", TAG);
            } else {
                Snackbar.make(binding.getRoot(), getString(R.string.toast_camera_err),
                        BaseTransientBottomBar.LENGTH_SHORT).show();
            }
        }
    }

}
