package com.example.nextstreet.profile;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.example.nextstreet.BuildConfig;
import com.example.nextstreet.R;
import com.example.nextstreet.compose.BitmapManipulation;
import com.example.nextstreet.compose.CameraLauncher;
import com.example.nextstreet.compose.CameraOnClickListener;
import com.example.nextstreet.databinding.FragmentDriverProfileBinding;
import com.example.nextstreet.home.HomeFragment;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FetchPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.snackbar.Snackbar;
import com.google.common.base.Preconditions;
import com.parse.ParseFile;
import com.parse.ParseUser;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class DriverProfileFragment extends Fragment implements CameraLauncher {

  private static final String TAG = HomeFragment.class.getSimpleName();
  private static final int AUTOCOMPLETE_DESTINATION_REQUEST_CODE = 20;
  private static final String PROFILE_PIC = "profilePic";
  private static final String HOME_PLACE = "homePlaceId";

  private FragmentDriverProfileBinding binding;
  private CameraOnClickListener cameraOnClickListener;

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

    cameraOnClickListener = new CameraOnClickListener(this);

    binding.chooseHomeButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG);
        Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, placeFields)
                .build(getContext());
        startActivityForResult(intent, AUTOCOMPLETE_DESTINATION_REQUEST_CODE);
      }
    });

    ParseUser currUser = ParseUser.getCurrentUser();
    String homePlace = currUser.getString(HOME_PLACE);
    if (homePlace != null) {
      setPlaceFromId(homePlace);
    }

    binding.profilePictureButton.setOnClickListener(cameraOnClickListener);
    binding.logoutButton.setOnClickListener(new LogoutOnClickListener(getActivity()));
    getActivity().setTitle(getResources().getText(R.string.personal_profile));

    ParseFile profilePic = ParseUser.getCurrentUser().getParseFile(PROFILE_PIC);

    if (profilePic != null) {
      Glide.with(getContext())
              .load(profilePic.getUrl())
              .placeholder(R.mipmap.ic_launcher_round)
              .transform(new CircleCrop())
              .into(binding.profilePictureImageView);
    } else {
      Glide.with(getContext()).load(R.mipmap.ic_launcher_round).into(binding.profilePictureImageView);
    }
  }

  private void setPlaceFromId(String placeId) {
    final List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS);

    final FetchPlaceRequest request = FetchPlaceRequest.newInstance(placeId, placeFields);
    Places.initialize(getContext().getApplicationContext(), BuildConfig.MAPS_API_KEY);
    PlacesClient placesClient = Places.createClient(getContext());

    View rootView = getActivity().findViewById(android.R.id.content).getRootView();
    Snackbar.make(rootView, R.string.toast_place_loading, Snackbar.LENGTH_SHORT);

    placesClient.fetchPlace(request).addOnFailureListener(new OnFailureListener() {
      @Override
      public void onFailure(@NonNull Exception e) {
        if (e instanceof ApiException) {
          ApiException apiException = (ApiException) e;
          int statusCode = apiException.getStatusCode();
          // TODO: Handle error with given status code.
          Log.e(TAG, "Place not found: " + e.getMessage());
        }
      }
    }).addOnSuccessListener(new OnSuccessListener<FetchPlaceResponse>() {
      @Override
      public void onSuccess(FetchPlaceResponse fetchPlaceResponse) {
        Place place = fetchPlaceResponse.getPlace();
        Log.i(TAG, "Place found: " + place.getName() + fetchPlaceResponse);

        CharSequence name = place.getName();
        if (name == null) {
          name = place.getAddress();
        }

        binding.chooseHomeButton.setText(String.format("%s %s", getString(R.string.location), name));
      }
    });
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    if (requestCode == AUTOCOMPLETE_DESTINATION_REQUEST_CODE) {
      if (resultCode == RESULT_OK) {
        Preconditions.checkNotNull(data);
        Place place = Autocomplete.getPlaceFromIntent(data);
        Log.i(TAG, "Place: " + place.getName() + ", " + place.getId());

        binding.chooseHomeButton.setText(place.getName());
        ParseUser currUser = ParseUser.getCurrentUser();
        currUser.put(HOME_PLACE, place.getId());
        currUser.saveInBackground();
      } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
        Status status = Autocomplete.getStatusFromIntent(data);
        Log.e(TAG, status.getStatusMessage());
      }
    } else if (requestCode == BitmapManipulation.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
      File tempPhotoFile = cameraOnClickListener.getPhotoFile();
      Log.i(TAG, "onActivityResult: taking photo");

      if (resultCode == RESULT_OK && tempPhotoFile != null) {
        Log.i(TAG, "onActivityResult: took photo");
        Bitmap takenImage = BitmapFactory.decodeFile(tempPhotoFile.getAbsolutePath());

        Bitmap resizedBitmap =
                BitmapManipulation.scaleToFitWidth(
                        takenImage, (int) getResources().getDimension((R.dimen.resized_post_image)));

        binding.profileHeaderImageView.setImageBitmap(resizedBitmap);
        File resizedPhotoFile = writeResizedBitmap(photoFileName, resizedBitmap, "_resized");

        Glide.with(getContext())
                .load(resizedPhotoFile)
                .transform(new CircleCrop())
                .into(binding.profilePictureImageView);

        ParseUser currentUser = ParseUser.getCurrentUser();
        currentUser.put(PROFILE_PIC, new ParseFile(resizedPhotoFile));
        currentUser.saveInBackground();
      }
    }
  }

  @Override
  public File launchCamera() {
    Log.i(TAG, "launchCamera: ");
    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
    File photoFile = getPhotoFileUri(photoFileName);

    Uri fileProvider =
            FileProvider.getUriForFile(getContext(), getString(R.string.file_authority), photoFile);
    intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

    if (intent.resolveActivity(getContext().getPackageManager()) != null) {
      startActivityForResult(intent, BitmapManipulation.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
    }

    return photoFile;
  }

  @Override
  public File getPhotoFileUri(String photoFileName) {
    File mediaStorageDir =
            new File(getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG);

    if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
      Log.d(TAG, "getPhotoFileUri: failed to create directory");
    }

    return new File(mediaStorageDir.getPath() + File.separator + photoFileName);
  }

  @Override
  public File writeResizedBitmap(String photoFileName, Bitmap changedBitmap, String resized) {
    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    changedBitmap.compress(Bitmap.CompressFormat.JPEG, 40, bytes);

    String filename = photoFileName + "_" + resized;
    File resizedFile = getPhotoFileUri(filename);

    try {
      resizedFile.createNewFile();
      FileOutputStream fos = new FileOutputStream(resizedFile);
      fos.write(bytes.toByteArray());
      fos.close();
    } catch (IOException e) {
      Log.e(TAG, "writeResizedBitmap: error writing image to new file", e);
    }
    return resizedFile;
  }

  @Override
  public Bitmap loadFromUri(Context context, Uri photoUri) {
    Bitmap image = null;
    try {
      // check version of Android on device
      if (Build.VERSION.SDK_INT > 27) {
        ImageDecoder.Source source =
                ImageDecoder.createSource(context.getContentResolver(), photoUri);
        image = ImageDecoder.decodeBitmap(source);
      } else {
        // support older versions of Android by using getBitmap
        image = MediaStore.Images.Media.getBitmap(context.getContentResolver(), photoUri);
      }
    } catch (IOException e) {
      Log.e(TAG, "loadFromUri: error loading image from file", e);
    }
    return image;
  }
}
