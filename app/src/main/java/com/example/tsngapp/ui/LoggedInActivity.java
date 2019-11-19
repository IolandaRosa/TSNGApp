package com.example.tsngapp.ui;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.tsngapp.R;
import com.example.tsngapp.helpers.Constants;
import com.example.tsngapp.model.User;
import com.example.tsngapp.network.AsyncTaskPostLogout;
import com.example.tsngapp.ui.fragment.DashboardFragment;
import com.example.tsngapp.ui.fragment.ProfileFragment;
import com.example.tsngapp.view_managers.LoginManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class LoggedInActivity extends AppCompatActivity {

    private BottomNavigationView bottomNav;

    private User user;
    private AsyncTaskPostLogout logoutTask;

    private FragmentManager fragmentManager;
    private Fragment dashboardFragment;
    private Fragment profileFragment;
    private Fragment activeFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logged_in);

        bindViews();
        bottomNav.setOnNavigationItemSelectedListener(navItemClickListener);

        Intent intent = this.getIntent();
        Bundle extras = intent.getExtras();
        user = extras.getParcelable(Constants.INTENT_USER_KEY);

        fragmentManager = getSupportFragmentManager();


        profileFragment = ProfileFragment.newInstance();
        dashboardFragment = DashboardFragment.newInstance(user);
        fragmentManager
                .beginTransaction()
                .add(R.id.frame_container, profileFragment, "profile")
                .hide(profileFragment)
                .commit();
        fragmentManager
                .beginTransaction()
                .add(R.id.frame_container, dashboardFragment, "dashboard")
                .commit();
        activeFragment = dashboardFragment;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.auth_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_logout:
                makeLogout();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navItemClickListener = item -> {
        switch (item.getItemId()) {
            case R.id.navigation_home:
                loadFragment(R.string.label_home, dashboardFragment);
                return true;
            case R.id.navigation_profile:
                loadFragment(R.string.label_profile, profileFragment);
                return true;
        }
        return false;
    };

    private void loadFragment(@StringRes int title, Fragment fragment) {
        loadFragment(getString(title), fragment);
    }

    private void loadFragment(String title, Fragment fragment) {
        getSupportActionBar().setTitle(title);
        FragmentTransaction transaction = fragmentManager
                .beginTransaction()
                .setCustomAnimations(R.anim.anim_fade_in, R.anim.anim_fade_out)
                .hide(activeFragment)
                .show(fragment);
        activeFragment = fragment;
        transaction.commit();
    }

    private void makeLogout(){
        this.logoutTask = new AsyncTaskPostLogout(jsonString -> {
            if(jsonString.equals(Constants.HTTP_OK)){
                Toast.makeText(this, "Logout Success", Toast.LENGTH_SHORT).show();

                //todo - retira token das shared preferences e coloca user a null e passa para a login activity
                user = null;
                LoginManager.getInstance().removeFromSharedPreference(this);

                startActivity(new Intent(this, LoginActivity.class));
                this.finish();
            }
            else{
                Toast.makeText(this, "Logout Success", Toast.LENGTH_SHORT).show();
            }
        }, user.getAcessToken());

        logoutTask.execute(Constants.LOGOUT_URL);
    }

    private void bindViews() {
        bottomNav = findViewById(R.id.bnv_logged_in_navigation);
    }
}
