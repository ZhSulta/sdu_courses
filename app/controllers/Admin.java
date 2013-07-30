package controllers;

import play.*;
import play.mvc.*;
 
import java.util.*;
 
import models.*;

@With(MySecure.class)
//@Check1("admin")
public class Admin extends Controller {
    
    @Before
    static void setConnectedUser() {
        if(MySecurity.isConnected()) {
            User user = User.find("byEmail", MySecurity.connected()).first();
            renderArgs.put("user", user);
        }
    }
 
    public static void index() {
    	System.out.println("admin");
        render();
    }
    
}