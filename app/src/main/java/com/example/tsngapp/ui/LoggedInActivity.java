package com.example.tsngapp.ui;

import androidx.annotation.StringRes;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.tsngapp.R;
import com.example.tsngapp.api.AuthManager;
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

public class LoggedInActivity extends AppCompatActivity implements
        ProfileFragment.ProfileFragmentActionListener,
        BaseFragmentActionListener {

    private final int DASHBOARD_MENU_ITEM_POSITION = 0;
    private final int STATE_MENU_ITEM_POSITION = 1;
    private final int PROFILE_MENU_ITEM_POSITION = 2;

    private BottomNavigationView bottomNav;

    private ActionBar actionBar;
    private FragmentManager fragmentManager;

    /**
     * Auxiliary variable to save the action bar subtitle when changing fragments
     */
    private @StringRes Integer currentSubtitleStringRes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logged_in);

        bindViews();
        bottomNav.setOnNavigationItemSelectedListener(navItemClickListener);

        fragmentManager = getSupportFragmentManager();
//        fragmentManager.addOnBackStackChangedListener(this::printBackStack);
        loadFragment(R.string.label_home, new DashboardFragment(), false);
    }

    @Override
    public void onBackPressed() {
        /* Loops through all the fragments on the manager and execute their back press
        * handler, only after that it performs it's own back press actions */
        boolean handled = false;
        for (Fragment f : fragmentManager.getFragments()) {
            if (f instanceof BaseFragment) {
                handled = ((BaseFragment) f).onBackPressed();
                if (handled) {
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
        if (title != null) {
            actionBar.setSubtitle(title);
        } else {
            actionBar.setSubtitle(null);
        }
        currentSubtitleStringRes = title;
    }

    private void changeTitleAndNavigationFromTag(String className) {
        if (className != null && className.equals(ProfileFragment.class.getName())) {
            bottomNav.getMenu().getItem(PROFILE_MENU_ITEM_POSITION).setChecked(true);
            actionBar.setTitle(R.string.label_profile);
        } else if (className != null && className.equals(StateFragment.class.getName())) {
            bottomNav.getMenu().getItem(STATE_MENU_ITEM_POSITION).setChecked(true);
            actionBar.setTitle(R.string.label_house_state);
        } else {
            bottomNav.getMenu().getItem(DASHBOARD_MENU_ITEM_POSITION).setChecked(true);
            actionBar.setTitle(R.string.label_home);
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
//        if (currentSubtitleStringRes != null && fragment instanceof StateFragment) {
//            actionBar.setSubtitle(currentSubtitleStringRes);
//        } else {
//            actionBar.setSubtitle(null);
//        }
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
                    Toast.makeText(this, "Logout Success", Toast.LENGTH_SHORT).show();

                    //todo - retira token das shared preferences e coloca user a null e passa para a login activity
                    AuthManager.getInstance().setUser(null);
                    LoginManager.getInstance().removeFromSharedPreference(this);

                    startActivity(new Intent(this, LoginActivity.class));
                    this.finish();
                } else {
                    Toast.makeText(this, "Logout Success", Toast.LENGTH_SHORT).show();
                }
            }, AuthManager.getInstance().getUser().getAcessToken()
        ).execute(Constants.LOGOUT_URL);
    }

    private void bindViews() {
        actionBar = getSupportActionBar();
        bottomNav = findViewById(R.id.bnv_logged_in_navigation);
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
