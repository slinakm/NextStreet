package com.example.nextstreet.ui.home;

import com.example.nextstreet.models.PackageRequest;

public interface QueryResponder {

    /**
     * Called by RequestQueryCallback to handle changes.
     *
     * @param request Package request from Parse.
     */
    void respondToQuery(PackageRequest request);
}
