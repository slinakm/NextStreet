package com.example.nextstreet.compose;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.nextstreet.R;
import com.example.nextstreet.databinding.ItemRequestBinding;
import com.example.nextstreet.databinding.ItemUserBinding;
import com.example.nextstreet.home.CurrentRequestsAdapter;
import com.example.nextstreet.models.PackageRequest;
import com.parse.ParseFile;
import com.parse.ParseUser;

import java.util.List;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.ViewHolder>{

    private static final String TAG = UsersAdapter.class.getSimpleName();
    private static final String FIRST_NAME = "firstName";
    private static final String LAST_NAME = "lastName";
    private static final String PROFILE_PIC = "profilePic";
    private final Activity context;
    private final List<ParseUser> users;
    private final FragmentCallback callback;
    private final FragmentCallback containingFragment;

    UsersAdapter(Activity context, List<ParseUser> users, FragmentCallback callback, FragmentCallback containingFragment) {
        this.context = context;
        this.users = users;
        this.callback = callback;
        this.containingFragment = containingFragment;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemUserBinding binding =
                ItemUserBinding.inflate(context.getLayoutInflater(), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(users.get(position));
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    void clear() {
        users.clear();
        notifyDataSetChanged();
    }

    void addAll(List<ParseUser> users) {
        this.users.addAll(users);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ItemUserBinding binding;

        public ViewHolder(ItemUserBinding binding) {
            super(binding.getRoot());

            this.binding = binding;
        }

        private void bind(final ParseUser user) {
            binding.nameTextView.setText(String.format("%s %s", user.getString(FIRST_NAME), user.getString(LAST_NAME)));
            binding.usernameTextView.setText(user.getUsername());

            ParseFile profilePic = user.getParseFile(PROFILE_PIC);
            if (profilePic != null) {
                Glide.with(context)
                        .load(profilePic.getUrl())
                        .placeholder(R.mipmap.ic_launcher_round)
                        .transform(new RoundedCorners(R.integer.rounded_corners))
                        .into(binding.profilePictureImageView);
            } else {
                Glide.with(context)
                        .load(R.mipmap.ic_launcher)
                        .transform(new RoundedCorners(R.integer.rounded_corners))
                        .into(binding.profilePictureImageView);
            }
            binding.toUserDetailsImageView.setVisibility(View.INVISIBLE);
      binding
          .getRoot()
          .setOnClickListener(
              new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                  Log.i(TAG, "onClick: " + user.getUsername());
                  callback.call(user);
                  containingFragment.call(user);
                }
              });
        }
    }
}
