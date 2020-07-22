package com.example.nextstreet.home;

import com.example.nextstreet.models.PackageRequest;

import java.util.List;

public interface QueryResponder {

    /**
     * Called by RequestQueryCallback to handle changes.
     *
     * @param requests Package request from Parse.
     */
    void respondToQuery(List<PackageRequest> requests);
}
