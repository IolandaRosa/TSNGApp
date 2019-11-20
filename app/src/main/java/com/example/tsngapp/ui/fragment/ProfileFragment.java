package com.example.tsngapp.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.tsngapp.R;
import com.example.tsngapp.helpers.Constants;
import com.example.tsngapp.helpers.DrawableUtil;
import com.example.tsngapp.model.User;
import com.example.tsngapp.network.AsyncTaskPostLogout;
import com.example.tsngapp.ui.LoginActivity;
import com.example.tsngapp.view_managers.LoginManager;

public class ProfileFragment extends BaseFragment {
    public static final String PARAM_USER = "PARAM_USER";

    private ProfileFragmentActionListener actionListener;

    private ImageView ivProfilePicture;
    private TextView tvName;
    private TextView tvUsername;
    private TextView tvEmail;
    private TextView btnLogout;

    private User user;

    public ProfileFragment() {}

    public static ProfileFragment newInstance(User user) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putParcelable(PARAM_USER, user);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.user = getArguments().getParcelable(PARAM_USER);
        }
    }

    @Override
    protected void onCreateViewActions(@NonNull LayoutInflater inflater,
                                       @Nullable ViewGroup container,
                                       @Nullable Bundle savedInstanceState) {
        bindViews();
        fillProfileInfo();
    }

    public void setActionListener(ProfileFragmentActionListener listener) {
        this.actionListener = listener;
    }

    private void fillProfileInfo() {
        if (user != null) {
            if (!user.getPhotoUrl().isEmpty()) {
                new DrawableUtil.DownloadImageTask(user.getPhotoUrl(), bitmap -> {
                    ivProfilePicture.setImageBitmap(bitmap);
                }).execute();
            }
            tvName.setText(user.getName());
            tvUsername.setText(user.getUsername());
            tvEmail.setText(user.getEmail());
        }
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_profile;
    }

    private View.OnClickListener logoutClickListener = view -> {
        actionListener.onLogoutClicked();
    };

    private void bindViews() {
        ivProfilePicture = rootView.findViewById(R.id.iv_profile_picture);
        tvName = rootView.findViewById(R.id.tv_profile_name);
        tvUsername = rootView.findViewById(R.id.tv_profile_username);
        tvEmail = rootView.findViewById(R.id.tv_profile_email);
        btnLogout = rootView.findViewById(R.id.btn_profile_log_out);

        btnLogout.setOnClickListener(logoutClickListener);
    }

    public interface ProfileFragmentActionListener {
        void onLogoutClicked();
    }
}
