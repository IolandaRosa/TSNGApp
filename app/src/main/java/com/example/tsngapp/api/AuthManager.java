package com.example.tsngapp.api;

import com.example.tsngapp.model.Elder;
import com.example.tsngapp.model.User;

public class AuthManager {
    private static AuthManager instance = new AuthManager();

    private User user;
    private Elder elder;

    private AuthManager() {}

    public static AuthManager getInstance() {
        if (instance == null) {
            instance = new AuthManager();
        }
        return instance;
    }

    public User getUser() {
        return user;
    }

    public AuthManager setUser(User user) {
        this.user = user;
        return this;
    }

    public Elder getElder() {
        return elder;
    }

    public AuthManager setElder(Elder elder) {
        this.elder = elder;
        return this;
    }
}
