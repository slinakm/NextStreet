package com.example.nextstreet.utilities;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.nextstreet.R;
import com.example.nextstreet.databinding.ItemRequestBinding;
import com.example.nextstreet.models.PackageRequest;
import com.google.android.gms.maps.model.LatLng;
import com.google.common.base.Preconditions;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;

import java.text.MessageFormat;

public class DetailsMaterialCard {

  public static void setUpCard(ItemRequestBinding card, PackageRequest request, Context context) {

    TextView descriptionTextView = card.descriptionTextView;
    descriptionTextView.setText(request.getDescription());

    ParseGeoPoint destination = request.getDestination();
    ParseGeoPoint origin = request.getOrigin();
    Preconditions.checkNotNull(destination, "Destination should not be null");
    Preconditions.checkNotNull(origin, "Origin should not be null");

    card.destinationTextView.setText(
        new LatLng(destination.getLatitude(), destination.getLongitude()).toString());
    card.originTextView.setText(new LatLng(origin.getLatitude(), origin.getLongitude()).toString());

    card.timeTextView.setText(request.getRelativeTimeAgo());
    card.distanceTextView.setText(
        MessageFormat.format(
            "{0} {1}",
            (int) origin.distanceInMilesTo(destination),
            context.getResources().getString(R.string.miles)));
    setUpImage(card, request, context);
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
