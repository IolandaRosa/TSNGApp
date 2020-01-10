package com.example.tsngapp.ui;

import androidx.annotation.StringRes;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.tsngapp.BuildConfig;
import com.example.tsngapp.R;
import com.example.tsngapp.api.SMARTAAL;
import com.example.tsngapp.helpers.StateManager;
import com.example.tsngapp.helpers.Constants;
import com.example.tsngapp.helpers.DialogUtil;
import com.example.tsngapp.network.AsyncTaskPostLogout;
import com.example.tsngapp.ui.fragment.BaseFragment;
import com.example.tsngapp.ui.fragment.DashboardFragment;
import com.example.tsngapp.ui.fragment.ProfileFragment;
import com.example.tsngapp.ui.fragment.StateFragment;
import com.example.tsngapp.ui.fragment.listener.BaseFragmentActionListener;
import com.example.tsngapp.view_managers.LoginManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.pusher.client.Pusher;
import com.pusher.client.PusherOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class LoggedInActivity extends AppCompatActivity implements
        ProfileFragment.ProfileFragmentActionListener,
        BaseFragmentActionListener {

    private final int DASHBOARD_MENU_ITEM_POSITION = 0;
    private final int STATE_MENU_ITEM_POSITION = 1;
    private final int PROFILE_MENU_ITEM_POSITION = 2;

    private BottomNavigationView bottomNav;

    private ActionBar actionBar;
    private FragmentManager fragmentManager;

    private NotificationManager notificationManager;
    private Pusher pusher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);

        setupNotificationChannels();

//        PushNotifications.start(getApplicationContext(), BuildConfig.PUSHER_BEAMS_INSTANCE_ID);
//        PushNotifications.addDeviceInterest(Constants.APP_TAG);

        // Try to restore data saved on Shared Preferences
        if (!StateManager.getInstance().isAuthenticationInfoLoaded()) {
            final boolean dataWasRestored = StateManager.getInstance().loadStoredAuthenticationInfo(this);
            if (!dataWasRestored) performPostLogoutActions();
        }
        setContentView(R.layout.activity_logged_in);

        bindViewsAndActions();

        fragmentManager = getSupportFragmentManager();
        loadFragment(R.string.label_home, new DashboardFragment(), false);

        bindSockets();
    }

    @Override
    public void onBackPressed() {
        /* Loops through all active fragments on the manager and execute their back press
        * handler, only after that it performs it's own back press actions */
        boolean handled = false;
        for (Fragment f : fragmentManager.getFragments()) {
            if (f instanceof BaseFragment) {
                if (handled = ((BaseFragment) f).onBackPressed()) {
                    break;
                }
            }
        }

        if (!handled) {
            performControlledBackPress();
        }
    }

    /**
     * Listens for logout button clicks on the Profile Fragment
     */
    @Override
    public void onLogoutClicked() {
        DialogUtil.createOkCancelDialog(this, null, R.string.logout_confirmation,
            (dialog, which) -> {
                if (which == AlertDialog.BUTTON_POSITIVE) {
                    makeLogout();
                }
            }).show();
    }

    /**
     * Listen for title changing requests from fragments
     * @param title
     */
    @Override
    public void setTitleFromFragment(@StringRes Integer title) {
        /* The setSubtitle method doesn't accept a null Integer, only a null to clear the title */
        if (title != null) {
            actionBar.setSubtitle(title);
        } else {
            actionBar.setSubtitle(null);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (pusher != null) {
            pusher.unsubscribe(Constants.Pusher.CHANNEL_DOOR_ANOMALY_VALUE);
        }
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navItemClickListener = item -> {
        switch (item.getItemId()) {
            case R.id.navigation_home:
                DashboardFragment df = new DashboardFragment();
                loadFragment(R.string.label_home, df);
                return true;
            case R.id.navigation_state:
                StateFragment sf = new StateFragment(this);
                loadFragment(R.string.label_house_state, sf);
                return true;
            case R.id.navigation_profile:
                ProfileFragment pf = new ProfileFragment();
                pf.setActionListener(this);
                loadFragment(R.string.label_profile, pf);
                return true;
        }
        return false;
    };

    private void setupNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            final List<NotificationChannel> channels = new ArrayList<>();
            NotificationChannel generalChannel = new NotificationChannel(
                    Constants.GENERAL_NOTIFICATIONS_CHANNEL_ID,
                    Constants.GENERAL_NOTIFICATIONS_CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT);
            generalChannel.setDescription("General notifications");
            channels.add(generalChannel);

            NotificationChannel urgentChannel = new NotificationChannel(
                    Constants.URGENT_NOTIFICATIONS_CHANNEL_ID,
                    Constants.URGENT_NOTIFICATIONS_CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH);
            urgentChannel.setDescription("Urgent notification like the trigger of an SOS button");
            channels.add(urgentChannel);

            notificationManager = (NotificationManager)
                    this.getSystemService(NOTIFICATION_SERVICE);
            if (notificationManager != null) {
                notificationManager.createNotificationChannels(channels);
            }
        }
    }

    private void bindSockets() {
        PusherOptions options = new PusherOptions();
        options.setCluster("eu");
        pusher = new Pusher(BuildConfig.PUSHER_KEY, options);

        pusher
            .subscribe(Constants.Pusher.CHANNEL_DOOR_ANOMALY_VALUE)
            .bind(Constants.Pusher.EVENT_NEW_DOOR_ANOMALY, event -> {
                try {
                    final int currentElderId = StateManager.getInstance().getElder().getId();

                    final String data = event.getData();
                    final JSONArray arr = new JSONObject(data).getJSONArray("values");
                    final JSONArray innerArr = arr.getJSONArray(0);
                    final int elderId = innerArr.getInt(0);
                    if (elderId == currentElderId) {
                        final String value = innerArr.getString(1);
                        if (value.equals("E")) {
                            showNotification(StateManager.getInstance().getRng().nextInt(),
                                    getString(R.string.label_door_anomaly),
                                    getString(R.string.door_anomaly_should_be_outside));
                        } else {
                            showNotification(StateManager.getInstance().getRng().nextInt(),
                                    getString(R.string.label_door_anomaly),
                                    getString(R.string.door_anomaly_should_be_inside));
                        }
                    }
                } catch (JSONException e) {
                    Log.d(Constants.DEBUG_TAG, String.format(
                            "Failed to parse %s EVENT from Pusher %s CHANNEL",
                            Constants.Pusher.CHANNEL_DOOR_ANOMALY_VALUE,
                            Constants.Pusher.EVENT_NEW_DOOR_ANOMALY));
                }
            });

        pusher.connect();
    }

    private void showNotification(int id, String title, String message) {
        Notification notification;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notification = new NotificationCompat.Builder(
                            this, Constants.URGENT_NOTIFICATIONS_CHANNEL_ID)
                            .setSmallIcon(R.drawable.ic_warning_black_24dp)
                            .setContentTitle(title)
                            .setContentText(message)
                            .build();
        } else {
            notification = new NotificationCompat.Builder(this)
                            .setSmallIcon(R.drawable.ic_warning_black_24dp)
                            .setContentTitle(title)
                            .setContentText(message)
                            .build();
        }

        if (notificationManager != null && notification != null) {
            notificationManager.notify(id, notification);
        } else {
            Log.d(Constants.DEBUG_TAG, "Couldn't create notification");
        }
    }

    private void changeTitleAndNavigationFromTag(String className) {
        if (className != null && className.equals(ProfileFragment.class.getName())) {
            bottomNav.getMenu().getItem(PROFILE_MENU_ITEM_POSITION).setChecked(true);
            actionBar.setTitle(R.string.label_profile);
            actionBar.setSubtitle(null);
        } else if (className != null && className.equals(StateFragment.class.getName())) {
            bottomNav.getMenu().getItem(STATE_MENU_ITEM_POSITION).setChecked(true);
            actionBar.setTitle(R.string.label_house_state);
            actionBar.setSubtitle(null);
        } else {
            bottomNav.getMenu().getItem(DASHBOARD_MENU_ITEM_POSITION).setChecked(true);
            actionBar.setTitle(R.string.label_home);
            actionBar.setSubtitle(null);
        }
    }

    /**
     * Performs back press actions, giving priority to fragment's back stack popping, in order
     * to simulate an activity-like behavior on fragment navigation
     */
    private void performControlledBackPress() {
        if (!isFragmentBackStackEmpty()) {
            fragmentManager.popBackStackImmediate();
            // If there's only one fragment on the back stack, goes back to the root (Dashboard)
            final String topFragmentTag = getTopFragmentTagFromBackStack();
            changeTitleAndNavigationFromTag(topFragmentTag);
        } else {
            super.onBackPressed();
        }
    }

    private void loadFragment(@StringRes int title, Fragment fragment) {
        loadFragment(getString(title), fragment, true);
    }

    private void loadFragment(@StringRes int title, Fragment fragment, boolean addToBackStack) {
        loadFragment(getString(title), fragment, addToBackStack);
    }

    private void loadFragment(String title, Fragment fragment) {
        loadFragment(title, fragment, true);
    }

    private void loadFragment(String title, Fragment fragment, boolean addToBackStack) {
        actionBar.setTitle(title);
        actionBar.setSubtitle(null);

        final String tag = fragment.getClass().getName();
        if (isCurrentFragment(tag)) {
            return;
        }

        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.setCustomAnimations(R.anim.anim_fade_in, R.anim.anim_fade_out)
            .replace(R.id.main_fragment_container, fragment, tag);
        if (addToBackStack) {
            ft.addToBackStack(tag);
        }
        ft.commit();
    }

    private void makeLogout() {
        new AsyncTaskPostLogout(jsonString -> {
                if (jsonString.equals(Constants.HTTP_OK)) {
                    performPostLogoutActions();
                } else {
                    Toast.makeText(this, R.string.logout_failed, Toast.LENGTH_SHORT).show();
                    Log.d(Constants.DEBUG_TAG, "Couldn't sign out, didn't receive an HTTP success code.");
                }
            }, StateManager.getInstance().getUser().getAcessToken()
        ).execute(Constants.LOGOUT_URL);
    }

    private void performPostLogoutActions() {
        // Clear the authentication manager(s)
        StateManager.getInstance().setUser(null);
        LoginManager.getInstance().clearAuthenticationInfo(this);

        // Delete cached information
        new File(getFilesDir().getPath() + "/" + Constants.STORAGE_DASHBOARD_FILENAME).delete();
        new File(getFilesDir().getPath() + "/" + Constants.STORAGE_ELDER_PROFILE_PICTURE_FILENAME).delete();

        // Redirect to the login activity
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    private void bindViewsAndActions() {
        actionBar = getSupportActionBar();
        bottomNav = findViewById(R.id.bnv_logged_in_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navItemClickListener);
    }

    //region Helper methods
    /**
     * Tries to find a fragment by its tag and checks if it's visible
     * @param tag
     * @return true is the fragment is being shown, otherwise false
     */
    private boolean isCurrentFragment(String tag) {
        final Fragment f = fragmentManager.findFragmentByTag(tag);
        return f != null && f.isVisible();
    }

    /**
     * Checks if the fragment back stack is empty
     * @return true is the back stack is empty, otherwise false
     */
    private boolean isFragmentBackStackEmpty() {
        return fragmentManager.getBackStackEntryCount() == 0;
    }

    /**
     * Checks is the given tag corresponds to a fragment that's on the top of the back stack,
     * meaning that it was the previous fragment being shown
     * @param tag
     * @return true if the tag is on the top of the stack, otherwise false
     */
    private boolean isFragmentOnTopOfBackStack(String tag) {
        final int entryCount = fragmentManager.getBackStackEntryCount();
        return entryCount > 0 && fragmentManager
                .getBackStackEntryAt(entryCount - 1).getName().equals(tag);
    }

    /**
     * Checks is the given tag corresponds to a fragment that's on the back stack
     * @param tag
     * @return true if the tab is found on the back stack
     */
    private boolean isFragmentOnBackStack(String tag) {
        for (int i = 0; i < fragmentManager.getBackStackEntryCount(); i++) {
            final String name = fragmentManager.getBackStackEntryAt(i).getName();
            if (name != null && name.equals(tag)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the tag (class name) of the fragment on top of the back stack
     * @return string tag of the fragment
     */
    private String getTopFragmentTagFromBackStack() {
        return isFragmentBackStackEmpty() ? null :
                fragmentManager.getBackStackEntryAt(
                        fragmentManager.getBackStackEntryCount() - 1
                ).getName();
    }

    /**
     * Prints the fragment back stack to the log console
     */
    private void printBackStack() {
        Log.d(Constants.DEBUG_TAG, "Current fragment back stack:");
        for (int i = 0; i < fragmentManager.getBackStackEntryCount(); i++) {
            final FragmentManager.BackStackEntry entry = fragmentManager.getBackStackEntryAt(i);
            Log.d(Constants.DEBUG_TAG, String.format("%d [%d]: %s", i, entry.getId(), entry.getName()));
        }
        Log.d(Constants.DEBUG_TAG, "\n");
    }
    //endregion
}
