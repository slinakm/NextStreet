package com.example.nextstreet;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.nextstreet.databinding.FragmentDriverProfileBinding;
import com.example.nextstreet.home.HomeFragment;

public class DriverProfileFragment extends Fragment {
    private static final String TAG = HomeFragment.class.getSimpleName();

    private FragmentDriverProfileBinding binding;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        binding = FragmentDriverProfileBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }
}
