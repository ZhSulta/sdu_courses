package controllers;

import models.User;

public class MySecurity extends MySecure.Security {
    
    static void onDisconnected() {
        Application.index();
    }
    static boolean check(String profile) {
        if("admin".equals(profile)) {
            return User.find("byEmail", connected()).<User>first().isAdmin;
        }
        return false;
    }
}
