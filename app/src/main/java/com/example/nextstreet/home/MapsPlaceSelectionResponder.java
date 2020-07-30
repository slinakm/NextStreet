package com.example.nextstreet.home;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.model.Place;

public interface MapsPlaceSelectionResponder {

    void setDestination(LatLng destination);
    void setOrigin(LatLng origin);
    LatLng getDestination();
    LatLng getOrigin();

    void setDestinationPlace(Place destinationPlace);
    void setOriginPlace(Place originPlace);
    Place getDestinationPlace();
    Place getOriginPlace();
}
