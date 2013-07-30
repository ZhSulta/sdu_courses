package controllers;

import java.util.ArrayList;
import java.util.List;

import models.Course;
import models.User;
import play.cache.Cache;
import play.mvc.Controller;

public class Code extends Controller {
	
	public static void css() {
		render("code/css/index.html");
    }
	
	public static void js() {
		render("code/js/index.html");
    }
	
	public static void java() {
		render("code/java/java.html");
    }
	
	public static void scala() {
		render("code/scala/scala.html");
    }
	
	public static void python() {
		render("code/python/index.html");
    }
	
	public static void ruby() {
		render("code/ruby/index.html");
    }
	
	public static void html() {
		render("code/html/preview.html");
    }
	
	public static void sql() {
		render("code/sql/index.html");
    }
	
	public static void c() {
		render("code/c/index.html");
    }
	
	public static void coffeescript() {
		render("code/coffescript/index.html");
    }
	
	public static void clojure() {
		render("code/clojure/index.html");
    }
	
	public static void php() {
		render("code/php/index.html");
    }
}
