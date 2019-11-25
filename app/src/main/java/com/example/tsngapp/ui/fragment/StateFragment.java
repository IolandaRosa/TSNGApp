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
import android.widget.Toast;

import com.example.tsngapp.R;

/**
 * Fragment for the house and elder detailed state
 */
public class StateFragment extends BaseFragment
    implements StateMenuFragment.FragmentActionListener{

    private FragmentManager fragmentManager;

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

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = fragmentManager
                .beginTransaction()
                .addToBackStack(fragment.getClass().getSimpleName())
                .setCustomAnimations(R.anim.anim_fade_in, R.anim.anim_fade_out)
                .replace(R.id.state_fragment_container, fragment);
        transaction.commit();
    }

    @Override
    public void onMenuItemClicked(Class fragmentClass) {
        Toast.makeText(rootView.getContext(), fragmentClass.getSimpleName(), Toast.LENGTH_SHORT).show();
    }
}
