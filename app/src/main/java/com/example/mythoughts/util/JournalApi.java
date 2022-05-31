package com.example.mythoughts.util;

import android.app.Application;

public class JournalApi extends Application {
    private String username;
    private String usersId;
    private static JournalApi instance;

    public JournalApi(String username, String usersId) {
        this.username = username;
        this.usersId = usersId;
    }

    public static JournalApi getInstance() {
        if (instance == null)
            instance = new JournalApi();
        return instance;
    }

    public JournalApi() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsersId() {
        return usersId;
    }

    public void setUsersId(String userId) {
        this.usersId = usersId;
    }
}
