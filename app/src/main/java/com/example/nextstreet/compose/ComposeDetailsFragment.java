package com.example.nextstreet.compose;

import android.annotation.TargetApi;
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
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.nextstreet.R;
import com.example.nextstreet.databinding.FragmentComposeDetailsBinding;
import com.example.nextstreet.home.NewSubmissionListener;
import com.example.nextstreet.models.PackageRequest;
import com.example.nextstreet.utilities.CircularRevealDialogFragment;
import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.common.base.Preconditions;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class ComposeDetailsFragment extends CircularRevealDialogFragment implements CameraLauncher, NewSubmissionListener {

    private static final String TAG = ComposeDetailsFragment.class.getSimpleName();
    private static final int AUTOCOMPLETE_DESTINATION_REQUEST_CODE = 2;
    private static final int AUTOCOMPLETE_ORIGIN_REQUEST_CODE = 3;

    // Make enter and exit animation going to side
    private FragmentComposeDetailsBinding binding;
    private Toolbar toolbar;
    private ComposeHelper composeHelper;
    private CameraOnClickListener cameraOnClickListener;

    public static ComposeDetailsFragment display(Fragment targetFragment, FragmentManager fragmentManager) {
        ComposeDetailsFragment composeDetailsFragment = new ComposeDetailsFragment();
        composeDetailsFragment.setTargetFragment(targetFragment, 0);
        composeDetailsFragment.show(fragmentManager, TAG);
        return composeDetailsFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.AppTheme_FullScreenDialog);
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentComposeDetailsBinding.inflate(getLayoutInflater());
        composeHelper  = new ComposeHelper(binding, getContext());
        ComposeHelper.addNewSubmissionListener(this);
        toolbar = binding.toolbarDialog;

        final View root = binding.getRoot();

        root.addOnLayoutChangeListener(
                new View.OnLayoutChangeListener() {
                    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void onLayoutChange(
                            View v,
                            int left,
                            int top,
                            int right,
                            int bottom,
                            int oldLeft,
                            int oldTop,
                            int oldRight,
                            int oldBottom) {
                        v.removeOnLayoutChangeListener(this);
                        setCx(root.getWidth()/2);
                        setCy(root.getHeight());
                        setUpForShowAnimation(root);
                        animateShowingFragment(root);
                    }
                });
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();

        getDialog()
                .getWindow()
                .setLayout(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        toolbar.setTitle(getResources().getString(R.string.compose_details));
        toolbar.setTitleTextColor(getResources().getColor(R.color.primaryTextColor));

        cameraOnClickListener = new CameraOnClickListener(this);
        binding.cameraButton.setOnClickListener(cameraOnClickListener);
        setUpPlaceAutocompleteIntents();
        setUpButtons();

        composeHelper.onViewCreated(view, savedInstanceState, getTargetFragment());
    }

    private void setUpButtons() {
        binding.toDescriptionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isActivated = !binding.toDescriptionButton.isActivated();
                binding.toDescriptionButton.setActivated(isActivated);

                if (binding.etDescription.getVisibility() == View.GONE) {
                    binding.etDescription.setVisibility(View.VISIBLE);
                } else {
                    binding.etDescription.setVisibility(View.GONE);
                }
            }
        });

        binding.toImageButtonImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isActivated = !binding.toImageButtonImageView.isActivated();
                binding.toImageButtonImageView.setActivated(isActivated);

                if (binding.cameraButton.getVisibility() == View.GONE) {
                    binding.cameraButton.setVisibility(View.VISIBLE);
                } else {
                    binding.packageImageView.setVisibility(View.GONE);
                    binding.cameraButton.setVisibility(View.GONE);
                }
            }
        });
    }

    private void setUpPlaceAutocompleteIntents() {
        binding.toDestinationImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG);
                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, placeFields)
                        .build(getContext());
                startActivityForResult(intent, AUTOCOMPLETE_DESTINATION_REQUEST_CODE);
            }
        });

        binding.toOriginImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG);
                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, placeFields)
                        .build(getContext());
                startActivityForResult(intent, AUTOCOMPLETE_ORIGIN_REQUEST_CODE);
            }
        });
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
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i(TAG, "onActivityResult: Activity done.");

        if (requestCode == BitmapManipulation.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            File tempPhotoFile = cameraOnClickListener.getPhotoFile();
            Log.i(TAG, "onActivityResult: taking photo");

            if (resultCode == RESULT_OK && tempPhotoFile != null) {
                Log.i(TAG, "onActivityResult: took photo");
                Bitmap takenImage = BitmapFactory.decodeFile(tempPhotoFile.getAbsolutePath());

                Bitmap resizedBitmap =
                        BitmapManipulation.scaleToFitWidth(
                                takenImage, (int) getResources().getDimension((R.dimen.resized_post_image)));

                binding.packageImageView.setImageBitmap(resizedBitmap);
                binding.packageImageView.setVisibility(View.VISIBLE);

                composeHelper.setPhotoFile(writeResizedBitmap(photoFileName, resizedBitmap, "resized"));
            }
        } else if (requestCode == AUTOCOMPLETE_DESTINATION_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Preconditions.checkNotNull(data);
                Place destinationPlace = Autocomplete.getPlaceFromIntent(data);
                Log.i(TAG, "Place: " + destinationPlace.getName() + ", " + destinationPlace.getId());
                composeHelper.setDestinationPlace(destinationPlace);

                binding.chooseDestinationTextView.setText(String.format("%s: %s", getResources().getText(R.string.destination), destinationPlace.getName()));
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                Status status = Autocomplete.getStatusFromIntent(data);
                Log.e(TAG, status.getStatusMessage());
            }
        } else if (requestCode == AUTOCOMPLETE_ORIGIN_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Preconditions.checkNotNull(data);
                Place originPlace = Autocomplete.getPlaceFromIntent(data);
                Log.i(TAG, "Place: " + originPlace.getName() + ", " + originPlace.getId());
                composeHelper.setOriginPlace(originPlace);

                binding.chooseOriginTextView.setText(String.format("%s: %s", getResources().getText(R.string.origin), originPlace.getName()));
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                Status status = Autocomplete.getStatusFromIntent(data);
                Log.e(TAG, status.getStatusMessage());
            }
        }
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

    @Override
    public void respondToNewSubmission(PackageRequest request) {
    }
}
