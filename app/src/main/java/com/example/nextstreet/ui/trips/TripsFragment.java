package com.example.nextstreet.ui.trips;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nextstreet.databinding.FragmentTripsBinding;

public class TripsFragment extends Fragment {

  private FragmentTripsBinding binding;
  private TripsViewModel tripsViewModel;

  private RecyclerView rvPackages;

  public View onCreateView(
      @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    tripsViewModel = ViewModelProviders.of(this).get(TripsViewModel.class);

    binding = FragmentTripsBinding.inflate(getLayoutInflater());

    rvPackages = binding.rvPackages;
    rvPackages.setLayoutManager(new LinearLayoutManager(getContext()));

    return binding.getRoot();
  }
}
