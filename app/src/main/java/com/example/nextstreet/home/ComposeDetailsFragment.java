package com.example.nextstreet.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.example.nextstreet.R;
import com.example.nextstreet.databinding.FragmentComposeDetailsBinding;

public class ComposeDetailsFragment extends DialogFragment {

    private static final String TAG = ComposeDetailsFragment.class.getSimpleName();

    private FragmentComposeDetailsBinding binding;
    private Toolbar toolbar;

    public static ComposeDetailsFragment display(FragmentManager fragmentManager) {
        ComposeDetailsFragment composeDetailsFragment = new ComposeDetailsFragment();
        composeDetailsFragment.show(fragmentManager, TAG);
        return composeDetailsFragment;
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentComposeDetailsBinding.inflate(getLayoutInflater());
        toolbar = binding.toolbarDialog;

        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();

        getDialog()
                .getWindow()
                .setLayout(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        toolbar.setTitle(getResources().getString(R.string.compose_details));
    }
}
