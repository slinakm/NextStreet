package com.example.nextstreet;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.nextstreet.databinding.FragmentDriverRequestsBinding;
import com.example.nextstreet.databinding.FragmentHomeBinding;
import com.example.nextstreet.home.HomeFragment;

public class DriverRequestsFragment extends Fragment {
    private static final String TAG = HomeFragment.class.getSimpleName();

    private FragmentDriverRequestsBinding binding;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        binding = FragmentDriverRequestsBinding.inflate(getLayoutInflater());

        return binding.getRoot();
    }
}
