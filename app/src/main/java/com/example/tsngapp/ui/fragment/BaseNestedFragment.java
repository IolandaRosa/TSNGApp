package com.example.tsngapp.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.tsngapp.ui.fragment.listener.StateMenuFragmentActionListener;

public abstract class BaseNestedFragment<L extends StateMenuFragmentActionListener> extends BaseFragment {
    protected L parentListener;

    @Override
    protected void onCreateViewActions(@NonNull LayoutInflater inflater,
                                       @Nullable ViewGroup container,
                                       @Nullable Bundle savedInstanceState) {
        onAttachToParentFragment(getParentFragment());
        onCreateViewPostActions(inflater, container, savedInstanceState);
    }

    /**
     * Attaches the fragment listener to the parent fragment
     * @param fragment
     */
    private void onAttachToParentFragment(Fragment fragment) {
        try {
            parentListener = (L)fragment;
        } catch (ClassCastException e) {
            throw new ClassCastException(
                    fragment.toString() + " must implement StateMenuFragmentActionListener");
        }
    }

    /**
     * Actions that would be called on the onCreateView lifecycle event
     * @param inflater
     * @param container
     * @param savedInstanceState
     */
    protected abstract void onCreateViewPostActions(@NonNull LayoutInflater inflater,
                                                    @Nullable ViewGroup container,
                                                    @Nullable Bundle savedInstanceState);
}
