package com.example.nextstreet;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.nextstreet.databinding.FragmentComposeBinding;
import com.example.nextstreet.databinding.FragmentProfileBinding;
import com.example.nextstreet.listeners.DismissOnClickListener;
import com.example.nextstreet.listeners.TextObserver;
import com.example.nextstreet.ui.trips.TripsViewModel;

public class ComposeFragment extends DialogFragment {

    private static final String TAG = ComposeFragment.class.getSimpleName();

    private FragmentComposeBinding binding;
    private ComposeViewModel composeViewModel;

    public static ComposeFragment newInstance() {
        Bundle args = new Bundle();

        ComposeFragment fragment = new ComposeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        composeViewModel =
                ViewModelProviders.of(this).get(ComposeViewModel.class);
        binding = FragmentComposeBinding.inflate(getLayoutInflater());
        binding.ivCancel.setOnClickListener(new DismissOnClickListener(TAG, this));
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        composeViewModel.getDescription().observe(getViewLifecycleOwner(),
                new TextObserver(binding.etDescription));
    }

    @Override
    public void onResume() {
        super.onResume();

        getDialog().getWindow().setLayout(ViewGroup.LayoutParams.FILL_PARENT,
                ViewGroup.LayoutParams.FILL_PARENT);
    }
}
