package com.example.tsngapp.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public abstract class BaseFragment extends Fragment {
    /**
     * View returned from the layout inflater, useful to get context on fragments
     */
    protected View rootView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(getLayoutResourceId(), container, false);
        onCreateViewActions(inflater, container, savedInstanceState);
        return rootView;
    }

    /**
     * Actions that would be called on the onCreateView lifecycle event
     * @param inflater
     * @param container
     * @param savedInstanceState
     */
    protected abstract void onCreateViewActions(@NonNull LayoutInflater inflater,
                                                @Nullable ViewGroup container,
                                                @Nullable Bundle savedInstanceState);

    /**
     * Gets returns the id of the fragment layout resource to inflate on the onCreateView
     * @return the id of the fragment layout resource
     */
    protected abstract int getLayoutResourceId();
}
