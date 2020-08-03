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
import com.google.common.base.Preconditions;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;

import static com.example.nextstreet.login.SignupAbstractActivity.KEY_FIRSTNAME;
import static com.example.nextstreet.login.SignupAbstractActivity.KEY_LASTNAME;

public class DetailsMaterialCard {

  private static String TAG = DetailsMaterialCard.class.getSimpleName();

  public static void setUpCard(ItemRequestBinding card, PackageRequest request, Context context) {

    TextView descriptionTextView = card.descriptionTextView;
    descriptionTextView.setText(request.getDescription());

    ParseUser driver = request.getDriver();
    if (driver != null && request.getBoolean(PackageRequest.KEY_ISFULFILLED)) {
      card.driverTextView.setText(
              String.format("%s %s", driver.get(KEY_FIRSTNAME), driver.get(KEY_LASTNAME)));
    }

    setLocationTextViews(card, request, context);

    card.timeTextView.setText(request.getRelativeTimeAgo());
    setUpImage(card, request, context);
  }

  private static void setLocationTextViews(ItemRequestBinding card, PackageRequest request, Context context) {
    ParseGeoPoint destination = request.getDestination();
    ParseGeoPoint origin = request.getOrigin();
    Preconditions.checkNotNull(destination, "Destination should not be null");
    Preconditions.checkNotNull(origin, "Origin should not be null");

    card.destinationTextView.setText(
            new LatLng(destination.getLatitude(), destination.getLongitude()).toString());

    card.originTextView.setText(new LatLng(origin.getLatitude(), origin.getLongitude()).toString());

    String destinationPlaceId = request.getDestinationPlaceId();
    if (destinationPlaceId != null) {
      getPlaceFromId(card, destinationPlaceId, context, false);
    }

    String originPlaceId = request.getOriginPlaceId();
    if (originPlaceId != null) {
      getPlaceFromId(card, originPlaceId, context, true);
    }

    card.distanceTextView.setText(
            MessageFormat.format(
                    "{0} {1}",
                    (int) origin.distanceInMilesTo(destination),
                    context.getResources().getString(R.string.miles)));
  }

  private static void getPlaceFromId(final ItemRequestBinding card, String placeId,
                                      Context context, final boolean isOrigin) {
    final List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS);

    final FetchPlaceRequest request = FetchPlaceRequest.newInstance(placeId, placeFields);
    Places.initialize(context.getApplicationContext(), BuildConfig.MAPS_API_KEY);
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
        Place place = fetchPlaceResponse.getPlace();
        Log.i(TAG, "Place found: " + place.getName() + fetchPlaceResponse);

        CharSequence name = place.getName();
        if (name == null) {
          name = place.getAddress();
        }

        if (isOrigin) {
          card.originTextView.setText(name);
        } else {
          card.destinationTextView.setText(name);
        }
      }
    });
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
