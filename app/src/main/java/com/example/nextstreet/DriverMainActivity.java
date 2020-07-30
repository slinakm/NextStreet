package com.example.nextstreet;

import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.nextstreet.databinding.ActivityDriverMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.common.base.Preconditions;

public class DriverMainActivity extends AppCompatActivity {
  private static final String TAG = MainActivity.class.getSimpleName();

  private AppBarConfiguration mAppBarConfiguration;
  private ActivityDriverMainBinding binding;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    binding = ActivityDriverMainBinding.inflate(getLayoutInflater());
    setContentView(binding.getRoot());

    Toolbar toolbar = findViewById(R.id.driver_toolbar);
    toolbar.setNavigationIcon(null);
    setSupportActionBar(toolbar);

    BottomNavigationView navigationView = findViewById(R.id.nav_driver_view);
    // Passing each menu ID as a set of Ids because each
    // menu should be considered as top level destinations.
    mAppBarConfiguration =
        new AppBarConfiguration.Builder(R.id.nav_driver_requests, R.id.nav_driver_profile).build();
    NavController navController = Navigation.findNavController(this, R.id.nav_driver_host_fragment);
    NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
    NavigationUI.setupWithNavController(navigationView, navController);

    ActionBar actionBar = getSupportActionBar();
    Preconditions.checkNotNull(actionBar);
    actionBar.setHomeButtonEnabled(false);
    actionBar.setDisplayHomeAsUpEnabled(false);
  }
}
