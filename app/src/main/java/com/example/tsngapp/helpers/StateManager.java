package com.example.tsngapp.helpers;

import android.content.Context;

import com.example.tsngapp.model.Elder;
import com.example.tsngapp.model.User;
import com.example.tsngapp.view_managers.LoginManager;

import java.util.Date;
import java.util.Random;

public class StateManager {
    private static StateManager instance = new StateManager();

    private Random rng;
    private User user;
    private Elder elder;

    private Long currentReferenceTimestamp;

    private StateManager() {}

    public static StateManager getInstance() {
        if (instance == null) {
            instance = new StateManager();
        }
        return instance;
    }

    public User getUser() {
        return user;
    }

    public StateManager setUser(User user) {
        this.user = user;
        return this;
    }

    public Elder getElder() {
        return elder;
    }

    public StateManager setElder(Elder elder) {
        this.elder = elder;
        return this;
    }

    /**
     * Returns a pseudorandom number generator that is initialized the first time it's used
     * and is the same throughout the app
     * @return Random
     */
    public Random getRng() {
        if (rng == null) {
            rng = new Random(new Date().getTime());
        }
        return rng;
    }

    public Long getCurrentReferenceTimestamp() {
        return currentReferenceTimestamp;
    }

    public void setCurrentReferenceTimestamp(Long currentReferenceTimestamp) {
        this.currentReferenceTimestamp = currentReferenceTimestamp;
    }

    /**
     * Loads the user and elder from the LoginManager
     * @param context
     * @return true if both the user and elder were successfully loaded with some data
     */
    public boolean loadStoredAuthenticationInfo(Context context) {
        final LoginManager lm = LoginManager.getInstance();
        this.user = lm.retrieveUser(context);
        this.elder = lm.retrieveElder(context);
        return this.user != null && this.elder != null;
    }

    public boolean isAuthenticationInfoLoaded() {
        return this.user != null && this.elder != null;
    }
}
