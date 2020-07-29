package com.example.nextstreet.trips;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nextstreet.R;
import com.example.nextstreet.databinding.ContentDriverDetailsBinding;
import com.example.nextstreet.databinding.ContentDriverRequestsBinding;
import com.example.nextstreet.databinding.FragmentDriverRequestsBinding;
import com.example.nextstreet.databinding.FragmentHomeBinding;
import com.example.nextstreet.home.HomeFragment;
import com.example.nextstreet.home.QueryResponder;
import com.example.nextstreet.models.PackageRequest;
import com.google.common.base.Preconditions;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DriverRequestsFragment extends Fragment implements QueryResponder {
    private static final String TAG = HomeFragment.class.getSimpleName();

    private final List<PackageRequest> requests = new ArrayList<>();
    private final Set<PackageRequest> rejectedRequests = new HashSet<>();
    private ContentDriverRequestsBinding layoutDriverRequests;
    private ContentDriverDetailsBinding layoutDriverDetails;
    private DriverRequestsAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        com.example.nextstreet.databinding.FragmentDriverRequestsBinding binding
                = FragmentDriverRequestsBinding.inflate(getLayoutInflater());
        layoutDriverRequests = binding.layoutDriverRequests;
        layoutDriverDetails = binding.layoutDriverDetails;

        Preconditions.checkNotNull(layoutDriverDetails);
        Preconditions.checkNotNull(layoutDriverRequests);

        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (layoutDriverRequests.getRoot().getVisibility() == View.VISIBLE) {
            setUpRecyclerView();
        } else {
            setUpDetailsPage();
        }

        queryMostRecentPackage();
    }

    private void setUpRecyclerView() {
        RecyclerView requestsRecyclerView = layoutDriverRequests.getRoot().
                findViewById(R.id.requestsRecyclerView);
        requestsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        AppCompatActivity appCompatActivityOfThis = (AppCompatActivity) getActivity();
        adapter = new DriverRequestsAdapter(appCompatActivityOfThis, requests, rejectedRequests,
                layoutDriverRequests.getRoot(), layoutDriverDetails.getRoot());
        requestsRecyclerView.setAdapter(adapter);
    }

    private void setUpDetailsPage() {
        layoutDriverDetails.getRoot().findViewById(R.id.card);
    }

    private void queryMostRecentPackage() {
        Log.d(TAG, "queryMostRecentPackage: ");
        ParseQuery<PackageRequest> query = ParseQuery.getQuery(PackageRequest.class);
        query.orderByDescending(PackageRequest.KEY_CREATEDAT);
        query.include(PackageRequest.KEY_USER);
        query.include(PackageRequest.KEY_IMAGE);
        query.include(PackageRequest.KEY_DESCRIPTION);
        query.include(PackageRequest.KEY_ORIGIN);
        query.include(PackageRequest.KEY_DRIVER);
        query.include(PackageRequest.KEY_DESTINATION);
        query.include(PackageRequest.KEY_IMAGE);
        query.include(PackageRequest.KEY_ISFULFILLED);

        query.whereEqualTo(PackageRequest.KEY_ISFULFILLED, false);
        query.whereEqualTo(PackageRequest.KEY_DRIVER, ParseUser.getCurrentUser());

        query.findInBackground(new com.example.nextstreet.home.RequestQueryCallback(this));
    }

    @Override
    public void respondToQuery(List<PackageRequest> requests) {
        for (PackageRequest request : requests) {
            Log.i(TAG, "respondToQuery: received " + request);
        }
        if (requests.removeAll(rejectedRequests)
                || rejectedRequests.size() == 0) {
            adapter.addAll(requests);
        } else {
            Log.d(TAG, "respondToQuery: error removing rejectedRequests");
        }
    }
}
