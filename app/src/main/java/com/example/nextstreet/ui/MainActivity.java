package com.example.nextstreet.ui;

import android.os.Bundle;
import android.view.View;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.nextstreet.R;
import com.example.nextstreet.databinding.ActivityMainBinding;
import com.example.nextstreet.ui.home.ComposeFragmentOnClickListener;
import com.example.nextstreet.login.SignupActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.parse.ParseFile;
import com.parse.ParseUser;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new ComposeFragmentOnClickListener(TAG, this));

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        // Set current user information
        ParseUser currUser = ParseUser.getCurrentUser();
        View headerView = navigationView.getHeaderView(0);
        TextView name = headerView.findViewById(R.id.tvName);
        String strName = (String) currUser.get(SignupActivity.KEY_FIRSTNAME)
                + " " + (String) currUser.get(SignupActivity.KEY_FIRSTNAME);
        name.setText(strName);
        TextView username = headerView.findViewById(R.id.tvUsername);
        String strUsername = currUser.getUsername();
        username.setText(strUsername);
        ImageView profilePic = headerView.findViewById(R.id.ivProfilePic);
        profilePic.setOnClickListener(new ProfileFragmentOnClickListener(TAG, this));
        ParseFile image = currUser.getParseFile(SignupActivity.KEY_PROFILEPIC);
        if (image != null) {
            Glide.with(binding.getRoot()).load(image.getUrl()).into(profilePic);
        }

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_trips)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}