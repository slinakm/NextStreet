package com.example.nextstreet.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.nextstreet.databinding.FragmentProfileBinding;
import com.example.nextstreet.listeners.DismissOnClickListener;
import com.example.nextstreet.login.LoginActivity;
import com.parse.ParseUser;

public class ProfileFragment extends DialogFragment {

    private static final String TAG = ProfileFragment.class.getSimpleName();

    private FragmentProfileBinding binding;

    public static ProfileFragment newInstance() {

        Bundle args = new Bundle();

        ProfileFragment fragment = new ProfileFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.btnLogout.setOnClickListener(new LogoutOnClickListener());
        binding.ivCancel.setOnClickListener(new DismissOnClickListener(TAG, this));
    }

    @Override
    public void onResume() {
        super.onResume();

        getDialog().getWindow().setLayout(ViewGroup.LayoutParams.FILL_PARENT,
                ViewGroup.LayoutParams.FILL_PARENT);
    }

    /**
     *   Logout button
     * */
    private class LogoutOnClickListener implements View.OnClickListener {
        public void onClick(View view) {
            Log.i(TAG, "onClick: submit button was clicked by user");

            ParseUser.logOut();

            Intent i = new Intent(ProfileFragment.this.getContext(), LoginActivity.class);
            getContext().startActivity(i);
            getActivity().finish();
        }
    }
}
