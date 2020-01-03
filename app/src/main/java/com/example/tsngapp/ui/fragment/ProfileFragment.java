package com.example.tsngapp.ui.fragment;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.tsngapp.R;
import com.example.tsngapp.helpers.StateManager;
import com.example.tsngapp.helpers.Constants;
import com.example.tsngapp.helpers.DateUtil;
import com.example.tsngapp.helpers.DrawableUtil;
import com.example.tsngapp.model.Elder;
import com.example.tsngapp.model.User;
import com.example.tsngapp.model.UserGender;
import com.example.tsngapp.network.AsyncTaskResult;
import com.example.tsngapp.network.OnResultListener;

import java.io.FileOutputStream;
import java.io.IOException;

public class ProfileFragment extends BaseFragment {
    private ProfileFragmentActionListener actionListener;

    private ImageView ivProfilePicture;
    private TextView tvElderName;
    private TextView tvElderInfo;
    private TextView tvName;
    private TextView tvUsername;
    private TextView tvEmail;
    private TextView btnLogout;

    public ProfileFragment() {}

    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_profile;
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

    private View.OnClickListener logoutClickListener = view -> {
        actionListener.onLogoutClicked();
    };

    @SuppressLint("DefaultLocale")
    private void fillProfileInfo() {
        final User user = StateManager.getInstance().getUser();
        if (user != null) {
            final Elder elder = StateManager.getInstance().getElder();

            restoreImageBitmap(
                    bitmap -> ivProfilePicture.setImageBitmap(bitmap),
                    () -> downloadImage(elder.getPhotoUrl())
            );
            tvElderName.setText(elder.getName());

            final String elderInfo = String.format("%s • %s years old",
                    getGenderString(elder.getGender()),
                    DateUtil.getAgeFromDate(elder.getBirthDate()));
            tvElderInfo.setText(elderInfo);
            tvName.setText(user.getName());
            tvUsername.setText(user.getUsername());
            tvEmail.setText(user.getEmail());
        }
    }

    private void downloadImage(String url) {
        if (url != null && !url.isEmpty()) {
            new DrawableUtil.DownloadImageTask(url, bitmap -> {
                ivProfilePicture.setImageBitmap(bitmap);
                saveImageBitmap(bitmap);
            }).execute();
        }
    }

    private String getGenderString(UserGender gender) {
        switch (gender) {
            case MALE: return getString(R.string.male);
            case FEMALE: return getString(R.string.female);
            default: return getString(R.string.undefined_gender);
        }
    }

    //region Data persisting
    private void saveImageBitmap(Bitmap bitmap) {
        new SaveImageTask(getStoragePath(), bitmap).execute();
    }

    private void restoreImageBitmap(OnResultListener<Bitmap> resultListener,
                                    OnCompleteListener completeListener) {
        new RestoreImageTask(getStoragePath(), resultListener, completeListener).execute();
    }

    private String getStoragePath() {
        return rootView.getContext().getFilesDir().getPath() + "/" +
                Constants.STORAGE_ELDER_PROFILE_PICTURE_FILENAME;
    }
    //endregion

    private void bindViews() {
        ivProfilePicture = rootView.findViewById(R.id.iv_profile_picture);
        tvElderName = rootView.findViewById(R.id.tv_profile_elder_name);
        tvElderInfo = rootView.findViewById(R.id.tv_profile_elder_info);
        tvName = rootView.findViewById(R.id.tv_profile_name);
        tvUsername = rootView.findViewById(R.id.tv_profile_username);
        tvEmail = rootView.findViewById(R.id.tv_profile_email);
        btnLogout = rootView.findViewById(R.id.btn_profile_log_out);

        btnLogout.setOnClickListener(logoutClickListener);
    }

    public interface ProfileFragmentActionListener {
        void onLogoutClicked();
    }

    private interface OnCompleteListener {
        void onComplete();
    }

    static class RestoreImageTask extends AsyncTask<Void, Void, AsyncTaskResult<Bitmap>> {
        private String imagePath;
        private OnResultListener<Bitmap> resultListener;
        private OnCompleteListener completeListener;


        public RestoreImageTask(String imagePath, OnResultListener<Bitmap> resultListener,
                                OnCompleteListener completeListener) {
            this.imagePath = imagePath;
            this.resultListener = resultListener;
            this.completeListener = completeListener;
        }

        @Override
        protected AsyncTaskResult<Bitmap> doInBackground(Void... voids) {
            try {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                Bitmap bitmap = BitmapFactory.decodeFile(imagePath, options);
                return new AsyncTaskResult<>(bitmap);
            } catch (Exception e) {
                Log.d(Constants.DEBUG_TAG, "Couldn't restore image bitmap: " + e.getLocalizedMessage());
                return new AsyncTaskResult<>(e);
            }
        }

        @Override
        protected void onPostExecute(AsyncTaskResult<Bitmap> result) {
            if (!result.hasError()) {
                resultListener.onResult(result.getResult());
            }
            completeListener.onComplete();
        }
    }

    static class SaveImageTask extends AsyncTask<Void, Void, Void> {
        private String path;
        private Bitmap bitmap;

        public SaveImageTask(String path, Bitmap bitmap) {
            this.path = path;
            this.bitmap = bitmap;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try (FileOutputStream out = new FileOutputStream(path)) {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            } catch (IOException e) {
                Log.d(Constants.DEBUG_TAG, "Couldn't write image bitmap: " + e.getLocalizedMessage());
            }
            return null;
        }
    }
}
