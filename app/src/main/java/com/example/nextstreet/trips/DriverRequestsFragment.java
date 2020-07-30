package com.example.nextstreet.trips;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.nextstreet.R;
import com.example.nextstreet.databinding.ContentDriverDetailsBinding;
import com.example.nextstreet.databinding.ContentDriverRequestsBinding;
import com.example.nextstreet.databinding.FragmentDriverRequestsBinding;
import com.example.nextstreet.home.QueryResponder;
import com.example.nextstreet.models.PackageRequest;
import com.google.common.base.Preconditions;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.livequery.ParseLiveQueryClient;
import com.parse.livequery.SubscriptionHandling;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DriverRequestsFragment extends Fragment implements QueryResponder {

  private static final String TAG = DriverRequestsFragment.class.getSimpleName();

  private static final String NOTIFICATION_CHANNEL_ID = "NextStreet_Channel";
  private static final int NOTIFICATION_NEW_DRIVER_ID = 22;
  private static final int YES_REQUEST_CODE = 1;
  private static final int NO_REQUEST_CODE = 0;
  private static final int FULLSCREEN_REQUEST_CODE = 2;

  private final List<PackageRequest> requests = new ArrayList<>();
  private final Set<PackageRequest> rejectedRequests = new HashSet<>();
  private ContentDriverRequestsBinding layoutDriverRequests;
  private ContentDriverDetailsBinding layoutDriverDetails;
  private DriverRequestsAdapter adapter;

  private SubscriptionHandling<PackageRequest> subscriptionHandling;
  private SwipeRefreshLayout swipeContainer;

  @Nullable
  @Override
  public View onCreateView(
      LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    super.onCreateView(inflater, container, savedInstanceState);

    com.example.nextstreet.databinding.FragmentDriverRequestsBinding binding =
        FragmentDriverRequestsBinding.inflate(getLayoutInflater());
    layoutDriverRequests = binding.layoutDriverRequests;
    layoutDriverDetails = binding.layoutDriverDetails;

    Preconditions.checkNotNull(layoutDriverDetails);
    Preconditions.checkNotNull(layoutDriverRequests);

    setUpRecyclerView();
    setUpListener();
    createNotificationChannel();
    return binding.getRoot();
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    swipeContainer = layoutDriverRequests.getRoot().findViewById(R.id.swipeContainer);
    swipeContainer.setOnRefreshListener(
        new SwipeRefreshLayout.OnRefreshListener() {
          @Override
          public void onRefresh() {
            queryMostRecentPackage();
          }
        });
    swipeContainer.setColorSchemeResources(
        android.R.color.holo_blue_bright,
        android.R.color.holo_green_light,
        android.R.color.holo_orange_light,
        android.R.color.holo_red_light);
  }

  @Override
  public void onResume() {
    super.onResume();
    createNotification();
    queryMostRecentPackage();
  }

  private void setUpListener() {
    ParseLiveQueryClient parseLiveQueryClient = ParseLiveQueryClient.Factory.getClient();

    ParseQuery<PackageRequest> parseQuery = ParseQuery.getQuery(PackageRequest.class);
    parseQuery.include(PackageRequest.KEY_DRIVER);
    parseQuery.whereEqualTo(PackageRequest.KEY_DRIVER, ParseUser.getCurrentUser());
    parseQuery.whereEqualTo(PackageRequest.KEY_ISDONE, false);
    parseQuery.whereEqualTo(PackageRequest.KEY_ISFULFILLED, false);

    subscriptionHandling = parseLiveQueryClient.subscribe(parseQuery);

    subscriptionHandling.handleEvents(
        new SubscriptionHandling.HandleEventsCallback<PackageRequest>() {
          @Override
          public void onEvents(
              ParseQuery<PackageRequest> query,
              SubscriptionHandling.Event event,
              PackageRequest requestReceived) {
            swipeContainer.post(
                new Runnable() {
                  @Override
                  public void run() {
                    swipeContainer.setRefreshing(true);
                  }
                });
            ParseUser driver = requestReceived.getParseUser(PackageRequest.KEY_DRIVER);
            Preconditions.checkNotNull(driver);
            Log.i(TAG, "onEvent: new package request was received with Driver");
            Preconditions.checkNotNull(requestReceived);
            createNotification();
            queryMostRecentPackage();
          }
        });
  }

  // TODO: make the notification show up as a heads up
  private void createNotification() {
    Log.d(TAG, "createNotification:here");

    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
      Log.i(TAG, "createNotification: creating new package notification");

      NotificationCompat.Action acceptAction =
          createNotificationAction(
              YES_REQUEST_CODE,
              getResources().getString(R.string.notification_driver_accept_action));
      NotificationCompat.Action rejectAction =
          createNotificationAction(
              NO_REQUEST_CODE, getResources().getString(R.string.notification_driver_reject));

      Intent actionIntent = new Intent();
      PendingIntent pendingFullScreenIntent =
          PendingIntent.getBroadcast(getContext(), FULLSCREEN_REQUEST_CODE, actionIntent, 0);

      NotificationCompat.Builder driverFoundNotification =
          new NotificationCompat.Builder(getContext(), NOTIFICATION_CHANNEL_ID)
              .setSmallIcon(R.drawable.package_notification_icon)
              .setContentTitle(getResources().getString(R.string.notification_newDriver_title))
              .setContentText(getResources().getString(R.string.notification_newDriver_description))
              .setPriority(NotificationCompat.PRIORITY_HIGH)
              .setFullScreenIntent(pendingFullScreenIntent, true)
              .addAction(acceptAction)
              .addAction(rejectAction);
      NotificationManager mNotificationManager =
          (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);

      mNotificationManager.notify(NOTIFICATION_NEW_DRIVER_ID, driverFoundNotification.build());
    }
  }

  private NotificationCompat.Action createNotificationAction(
      int ACTION_CODE, String stringForUser) {
    Intent actionIntent = new Intent();
    PendingIntent pendingActionIntent =
        PendingIntent.getBroadcast(getContext(), ACTION_CODE, actionIntent, 0);

    NotificationCompat.Action actionToReturn =
        new NotificationCompat.Action.Builder(
                R.drawable.ic_input_black_24dp, stringForUser, pendingActionIntent)
            .build();

    return actionToReturn;
  }

  /**
   * Create the NotificationChannel, but only on API 26+ because the NotificationChannel class is
   * new and not in the support library.
   */
  private void createNotificationChannel() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      CharSequence name = getString(R.string.channel_name);
      String description = getString(R.string.channel_description);
      int importance = NotificationManager.IMPORTANCE_HIGH;
      NotificationChannel channel =
          new NotificationChannel(NOTIFICATION_CHANNEL_ID, name, importance);
      channel.setDescription(description);
      NotificationManager notificationManager =
          getActivity().getSystemService(NotificationManager.class);
      notificationManager.createNotificationChannel(channel);
    }
  }

  private void setUpRecyclerView() {
    RecyclerView requestsRecyclerView =
        layoutDriverRequests.getRoot().findViewById(R.id.requestsRecyclerView);
    requestsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    AppCompatActivity appCompatActivityOfThis = (AppCompatActivity) getActivity();
    adapter =
        new DriverRequestsAdapter(
            appCompatActivityOfThis,
            requests,
            rejectedRequests,
            layoutDriverRequests.getRoot(),
            layoutDriverDetails.getRoot());
    requestsRecyclerView.setAdapter(adapter);
  }

  private void queryMostRecentPackage() {
    Log.d(TAG, "queryMostRecentPackage: ");
    ParseQuery<PackageRequest> query = ParseQuery.getQuery(PackageRequest.class);
    query.orderByDescending(PackageRequest.KEY_CREATEDAT);
    query.include(PackageRequest.KEY_USER);
    query.include(PackageRequest.KEY_IMAGE);
    query.include(PackageRequest.KEY_DESCRIPTION);
    query.include(PackageRequest.KEY_ORIGIN);
    query.include(PackageRequest.KEY_DRIVER);
    query.include(PackageRequest.KEY_DESTINATION);
    query.include(PackageRequest.KEY_IMAGE);
    query.include(PackageRequest.KEY_ISFULFILLED);

    query.whereEqualTo(PackageRequest.KEY_ISDONE, false);
    query.whereEqualTo(PackageRequest.KEY_ISFULFILLED, false);
    query.whereEqualTo(PackageRequest.KEY_DRIVER, ParseUser.getCurrentUser());

    query.findInBackground(new com.example.nextstreet.home.RequestQueryCallback(this));
  }

  @Override
  public void respondToQuery(List<PackageRequest> requests) {
    for (PackageRequest request : requests) {
      Log.i(TAG, "respondToQuery: received " + request);
    }
    if (requests.removeAll(rejectedRequests) || rejectedRequests.size() == 0) {
      changeVisibilityOfBackgroundText(requests);
      adapter.clear();
      adapter.addAll(requests);
      swipeContainer.setRefreshing(false);
    } else {
      Log.e(TAG, "respondToQuery: error removing rejectedRequests");
    }
  }

  private void changeVisibilityOfBackgroundText(List<PackageRequest> requests) {
    TextView noRequests = layoutDriverRequests.getRoot().findViewById(R.id.noRequestsTextView);

    if (requests.size() == 0) {
      noRequests.setVisibility(View.VISIBLE);
    } else {
      noRequests.setVisibility(View.GONE);
    }
  }
}
