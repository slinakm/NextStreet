package com.example.nextstreet.trips;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.nextstreet.databinding.FragmentDriverRequestsBinding;
import com.example.nextstreet.databinding.FragmentHomeBinding;
import com.example.nextstreet.home.HomeFragment;
import com.example.nextstreet.home.QueryResponder;
import com.example.nextstreet.models.PackageRequest;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class DriverRequestsFragment extends Fragment implements QueryResponder {
    private static final String TAG = HomeFragment.class.getSimpleName();

    private final List<PackageRequest> requests = new ArrayList<>();
    private FragmentDriverRequestsBinding binding;
    private DriverRequestsAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        binding = FragmentDriverRequestsBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.requestsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        AppCompatActivity appCompatActivityOfThis = (AppCompatActivity) getActivity();
        adapter = new DriverRequestsAdapter(appCompatActivityOfThis, requests);
        binding.requestsRecyclerView.setAdapter(adapter);

        queryMostRecentPackage();
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

        query.findInBackground(new com.example.nextstreet.home.RequestQueryCallback(this));
    }

    @Override
    public void respondToQuery(List<PackageRequest> requests) {
        for (PackageRequest request : requests) {
            Log.i(TAG, "respondToQuery: received " + request);
        }
        this.requests.addAll(requests);
        this.adapter.notifyDataSetChanged();
    }
}
