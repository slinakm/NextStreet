package com.example.nextstreet.utilities;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.nextstreet.BuildConfig;
import com.example.nextstreet.R;
import com.example.nextstreet.databinding.ContentDetailBinding;
import com.example.nextstreet.databinding.ItemRequestBinding;
import com.example.nextstreet.models.PackageRequest;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FetchPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.snackbar.Snackbar;
import com.google.common.base.Preconditions;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Response;

import static androidx.test.core.app.ApplicationProvider.getApplicationContext;

public class DetailsMaterialCard {

  private static String TAG = DetailsMaterialCard.class.getSimpleName();

  public static void setUpCard(ItemRequestBinding card, PackageRequest request, Context context) {

    TextView descriptionTextView = card.descriptionTextView;
    descriptionTextView.setText(request.getDescription());

    setLocationTextViews(card, request, context);

    card.timeTextView.setText(request.getRelativeTimeAgo());
    setUpImage(card, request, context);
  }

  private static void setLocationTextViews(ItemRequestBinding card, PackageRequest request, Context context) {
    ParseGeoPoint destination = request.getDestination();
    ParseGeoPoint origin = request.getOrigin();
    Preconditions.checkNotNull(destination, "Destination should not be null");
    Preconditions.checkNotNull(origin, "Origin should not be null");

    String destinationPlaceId = request.getDestinationPlaceId();
    String originPlaceId = request.getOriginPlaceId();

    if (destinationPlaceId == null) {
      card.destinationTextView.setText(
              new LatLng(destination.getLatitude(), destination.getLongitude()).toString());
    } else {
      Place destPlace = getPlaceFromId(destinationPlaceId, context);
      card.destinationTextView.setText(destPlace.getAddress());
    }

    if (originPlaceId == null) {
      card.originTextView.setText(new LatLng(origin.getLatitude(), origin.getLongitude()).toString());
    } else {
      Place originPlace = getPlaceFromId(originPlaceId, context);
      card.originTextView.setText(originPlace.getAddress());
    }

    card.distanceTextView.setText(
            MessageFormat.format(
                    "{0} {1}",
                    (int) origin.distanceInMilesTo(destination),
                    context.getResources().getString(R.string.miles)));
  }

  private static Place getPlaceFromId(String placeId, Context context) {
    final Place[] place = {null};
    final List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME);

    final FetchPlaceRequest request = FetchPlaceRequest.newInstance(placeId, placeFields);
    Places.initialize(getApplicationContext(), BuildConfig.MAPS_API_KEY);
    PlacesClient placesClient = Places.createClient(context);

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
        place[0] = fetchPlaceResponse.getPlace();
        Log.i(TAG, "Place found: " + place[0].getName());
      }
    });

    return place[0];
  }

  private static void setUpImage(ItemRequestBinding card, PackageRequest request, Context context) {
    ParseFile image = request.getImage();
    if (image != null) {
      card.packageImageView.setVisibility(View.VISIBLE);
      Glide.with(context)
          .load(image.getUrl())
          .transform(
              new RoundedCorners(context.getResources().getInteger(R.integer.rounded_corners)))
          .into(card.packageImageView);
    } else {
      card.packageImageView.setVisibility(View.GONE);
    }
  }
}
