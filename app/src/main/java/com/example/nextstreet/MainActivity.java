package com.example.nextstreet;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.bumptech.glide.Glide;
import com.example.nextstreet.compose.ComposeFragmentOnClickListener;
import com.example.nextstreet.databinding.ActivityMainBinding;
import com.example.nextstreet.login.SignupActivity;
import com.example.nextstreet.models.PackageRequest;
import com.example.nextstreet.profile.ProfileFragmentOnClickListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.common.base.Preconditions;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.livequery.ParseLiveQueryClient;
import com.parse.livequery.SubscriptionHandling;

public class MainActivity extends AppCompatActivity {

  private static final String TAG = MainActivity.class.getSimpleName();
  private static final String NOTIFICATION_CHANNEL_ID = "NextStreet_Channel";
  private static final int NOTIFICATION_NEW_DRIVER_ID = 22;

  private AppBarConfiguration mAppBarConfiguration;
  private ActivityMainBinding binding;
  private SubscriptionHandling<PackageRequest> subscriptionHandling;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    binding = ActivityMainBinding.inflate(getLayoutInflater());
    setContentView(binding.getRoot());

    Toolbar toolbar = findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    FloatingActionButton composeFloatingActionButton = findViewById(R.id.fab);
    composeFloatingActionButton.setOnClickListener(new ComposeFragmentOnClickListener(this));

    DrawerLayout drawer = findViewById(R.id.drawer_layout);
    NavigationView navigationView = findViewById(R.id.nav_view);
    setUpUser(navigationView);
    // Passing each menu ID as a set of Ids because each
    // menu should be considered as top level destinations.
    mAppBarConfiguration =
        new AppBarConfiguration.Builder(R.id.nav_home, R.id.nav_trips)
            .setDrawerLayout(drawer)
            .build();
    NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
    NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
    NavigationUI.setupWithNavController(navigationView, navController);

    createNotificationChannel();
    setUpListener();
  }

  private void setUpListener() {
    ParseLiveQueryClient parseLiveQueryClient = ParseLiveQueryClient.Factory.getClient();

    ParseQuery<PackageRequest> parseQuery = ParseQuery.getQuery(PackageRequest.class);
    parseQuery.include(PackageRequest.KEY_DRIVER);
    parseQuery.whereEqualTo(PackageRequest.KEY_USER, ParseUser.getCurrentUser());
    parseQuery.whereEqualTo(PackageRequest.KEY_ISDONE, false);
    parseQuery.whereEqualTo(PackageRequest.KEY_ISFULFILLED, true);

    subscriptionHandling = parseLiveQueryClient.subscribe(parseQuery);

    subscriptionHandling.handleEvent(
        SubscriptionHandling.Event.ENTER,
        new SubscriptionHandling.HandleEventCallback<PackageRequest>() {
          @Override
          public void onEvent(ParseQuery<PackageRequest> query, PackageRequest requestReceived) {
            ParseUser driver = requestReceived.getParseUser(PackageRequest.KEY_DRIVER);
            Log.i(TAG, "onEvent: new package request was received with Driver "
                    + driver.getUsername());
            Preconditions.checkNotNull(requestReceived);
            createNotification();
          }
        });
  }

  private void createNotification() {
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
      NotificationCompat.Builder driverFoundNotification =
              new NotificationCompat.Builder(MainActivity.this, NOTIFICATION_CHANNEL_ID)
                      .setSmallIcon(R.drawable.package_notification_icon)
                      .setContentTitle(getResources().getString(R.string.notification_newDriver_title))
                      .setContentText(getResources().getString(R.string.notification_newDriver_description))
                      .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                      .setCategory(NotificationCompat.CATEGORY_MESSAGE);
      NotificationManager mNotificationManager =
              (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

      mNotificationManager.notify(NOTIFICATION_NEW_DRIVER_ID, driverFoundNotification.build());
    }
  }

  /**
   * Create the NotificationChannel, but only on API 26+ because
   * the NotificationChannel class is new and not in the support library.
   */
  private void createNotificationChannel() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      CharSequence name = getString(R.string.channel_name);
      String description = getString(R.string.channel_description);
      int importance = NotificationManager.IMPORTANCE_DEFAULT;
      NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, name, importance);
      channel.setDescription(description);
      NotificationManager notificationManager = getSystemService(NotificationManager.class);
      notificationManager.createNotificationChannel(channel);
    }
  }

  private void setUpUser(NavigationView navigationView) {
    ParseUser currUser = ParseUser.getCurrentUser();
    View headerView = navigationView.getHeaderView(0);

    TextView name = headerView.findViewById(R.id.tvName);
    String strName =
        (String) currUser.get(SignupActivity.KEY_FIRSTNAME)
            + " "
            + (String) currUser.get(SignupActivity.KEY_FIRSTNAME);
    name.setText(strName);

    TextView username = headerView.findViewById(R.id.tvUsername);
    String strUsername = currUser.getUsername();
    username.setText(strUsername);

    ImageView profilePic = headerView.findViewById(R.id.ivProfilePic);
    profilePic.setOnClickListener(new ProfileFragmentOnClickListener(this));
    username.setOnClickListener(new ProfileFragmentOnClickListener(this));
    name.setOnClickListener(new ProfileFragmentOnClickListener(this));
    ParseFile image = currUser.getParseFile(SignupActivity.KEY_PROFILEPIC);

    if (image != null) {
      Glide.with(binding.getRoot()).load(image.getUrl()).into(profilePic);
    }
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
