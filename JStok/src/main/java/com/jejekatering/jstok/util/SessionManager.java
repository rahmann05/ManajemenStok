package com.jejekatering.jstok.util;

import com.jejekatering.jstok.model.Pengguna;

public class SessionManager {
    private static Pengguna currentUser;

    public static void setCurrentUser(Pengguna user) {
        currentUser = user;
    }

    public static Pengguna getCurrentUser() {
        return currentUser;
    }

    public static void logout() {
        currentUser = null;
    }
}