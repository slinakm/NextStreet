package com.example.nextstreet.compose;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.nextstreet.R;
import com.example.nextstreet.databinding.FragmentComposeDetailsBinding;
import com.example.nextstreet.databinding.FragmentDriverDetailsBinding;
import com.example.nextstreet.databinding.FragmentUsersBinding;
import com.example.nextstreet.home.QueryResponder;
import com.example.nextstreet.home.RequestQueryCallback;
import com.example.nextstreet.models.PackageRequest;
import com.example.nextstreet.utilities.DetailsMaterialCard;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class UsersFragment extends DialogFragment implements FragmentCallback {

    private static final String TAG = UsersFragment.class.getSimpleName();
    private static final String IS_DRIVER = "isDriver";
    private static final String HOME_PLACE = "homePlaceId";

    private FragmentUsersBinding binding;
    private UsersAdapter adapter;
    private Toolbar toolbar;
    private FragmentCallback callback;

    public static UsersFragment display(FragmentManager fragmentManager) {
        UsersFragment usersFragment = new UsersFragment();
        usersFragment.show(fragmentManager, TAG);
        return usersFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            callback = (FragmentCallback) getParentFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException("Calling fragment must implement Callback interface");
        }
        setStyle(DialogFragment.STYLE_NORMAL, R.style.AppTheme_FullScreenDialog);
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentUsersBinding.inflate(getLayoutInflater());
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
        toolbar.setTitle(getResources().getString(R.string.users));
        toolbar.setTitleTextColor(getResources().getColor(R.color.primaryTextColor));

        adapter = new UsersAdapter(getActivity(), new ArrayList<ParseUser>(), callback, this);
        binding.usersRecyclerView.setAdapter(adapter);
        binding.usersRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        queryUsers();
    }

    private void queryUsers() {
        ParseQuery<ParseUser> query = ParseQuery.getQuery(ParseUser.class);

        query.whereEqualTo(IS_DRIVER, false);
        query.whereExists(HOME_PLACE);
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                adapter.clear();
                adapter.addAll(objects);
            }
        });
    }

    @Override
    public void call(ParseUser user) {
        dismiss();
    }
}
