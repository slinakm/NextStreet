package com.example.nextstreet.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.nextstreet.databinding.FragmentComposeDetailsBinding;
import com.example.nextstreet.databinding.FragmentProfileBinding;
import com.example.nextstreet.profile.ProfileFragment;

public class ComposeDetailsFragment extends DialogFragment {

    private static final String TAG = ComposeDetailsFragment.class.getSimpleName();

    private FragmentComposeDetailsBinding binding;

    public static ComposeDetailsFragment newInstance() {
        Bundle args = new Bundle();

        ComposeDetailsFragment fragment = new ComposeDetailsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentComposeDetailsBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }
}
