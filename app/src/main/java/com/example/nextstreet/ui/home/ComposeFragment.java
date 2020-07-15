package com.example.nextstreet.ui.home;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.nextstreet.R;
import com.example.nextstreet.databinding.FragmentComposeBinding;
import com.example.nextstreet.listeners.DismissOnClickListener;
import com.example.nextstreet.listeners.TextObserver;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.parse.ParseUser;

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

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        composeViewModel.getDescription().observe(getViewLifecycleOwner(),
                new TextObserver(binding.etDescription));

        LatLng dest = HomeFragment.getDestination();
        LatLng origin = HomeFragment.getOrigin();
        if (dest != null) {
            binding.tvDestination.setText(dest.toString());
        }
        if (origin != null) {
            binding.tvOrigin.setText(origin.toString());
        }

        binding.ivCancel.setOnClickListener(new DismissOnClickListener(TAG, this));
        cameraOnClickListener = new CameraOnClickListener(TAG, getActivity(),
                this, photoFileName);
        binding.btnCamera.setOnClickListener(cameraOnClickListener);
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

    private void checkPostable(){
        binding.pbLoading.setVisibility(ProgressBar.VISIBLE);

        String desc = binding.etDescription.getText().toString();

        Log.d(TAG, "checkPostable: description =" + desc);
        Log.d(TAG, "checkPostable: get size of image" + photoFile.getTotalSpace());

        if (desc.isEmpty()) {
            Toast.makeText(getContext(),
                    R.string.toast_desc_empt, Toast.LENGTH_SHORT).show();
            binding.pbLoading.setVisibility(ProgressBar.INVISIBLE);
        } else if (photoFile == null
                || binding.ivPackage.getDrawable() == null) {
            Toast.makeText(getContext(),
                    R.string.toast_img_empt, Toast.LENGTH_SHORT).show();
            binding.pbLoading.setVisibility(ProgressBar.INVISIBLE);
        } else {
            ParseUser currUser = ParseUser.getCurrentUser();
            savePost(desc, photoFile, currUser);
        }
    }

}