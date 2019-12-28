package com.example.tsngapp.ui.fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.tsngapp.R;
import com.example.tsngapp.model.Elder;
import com.example.tsngapp.model.User;
import com.example.tsngapp.ui.fragment.listener.BaseFragmentActionListener;
import com.example.tsngapp.ui.fragment.listener.StateMenuFragmentActionListener;

import java.util.List;

/**
 * Fragment for the house and elder detailed state
 */
public class StateFragment extends BaseFragment
    implements
        StateMenuFragment.FragmentActionListener,
        StateMenuFragmentActionListener {

    private BaseFragmentActionListener actionListener;
    private FragmentManager fragmentManager;

    public StateFragment(BaseFragmentActionListener actionListener) {
        this.actionListener = actionListener;
    }

    @Override
    protected void onCreateViewActions(@NonNull LayoutInflater inflater,
                                       @Nullable ViewGroup container,
                                       @Nullable Bundle savedInstanceState) {
        fragmentManager = getChildFragmentManager();
        loadFragment(new StateMenuFragment());
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_state;
    }

    private void loadFragment(Fragment fragment, @StringRes Integer title) {
        // TODO: Fix back presses

        FragmentTransaction transaction = fragmentManager
                .beginTransaction()
                .setCustomAnimations(R.anim.anim_fade_in, R.anim.anim_fade_out)
                .replace(R.id.state_fragment_container, fragment);
        transaction.commit();
        actionListener.setTitleFromFragment(title);
    }

    private void loadFragment(Fragment fragment) {
        loadFragment(fragment, null);
    }

    @Override
    public void onMenuItemClicked(StateMenuFragment.MenuItem item) {
        switch (item) {
            case CURRENT:
                loadFragment(new ElectricalCurrentStateFragment(), item.getTitle());
                break;
            case DOOR:
                loadFragment(new DoorStateFragment(), item.getTitle());
                break;
            case BED:
                loadFragment(new BedStateFragment(), item.getTitle());
                break;
            case DIVISIONS:
                loadFragment(new DivisionStateFragment(), item.getTitle());
                break;
            case WINDOWS:
                loadFragment(new WindowStateFragment(), item.getTitle());
                break;
            case LIGHTS:
                loadFragment(new LightStateFragment(), item.getTitle());
                break;
            case SOS:
                loadFragment(new SOSStateFragment(), item.getTitle());
                break;
            default:
                loadFragment(new StateMenuFragment());
        }
    }

    /* Loops through all active fragments on the manager and execute their back press
     * handler, only after that it performs it's own back press actions */
    @Override
    public boolean onBackPressed() {
        for (Fragment f : fragmentManager.getFragments()) {
            if (f instanceof BaseFragment && ((BaseFragment) f).onBackPressed()) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void onBackToMenuPressed() {
        loadFragment(new StateMenuFragment());
    }

    @Override
    public void setTitleFromFragment(Integer title) {
        actionListener.setTitleFromFragment(title);
    }


}
