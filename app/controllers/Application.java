package controllers;

import play.*;
import play.cache.Cache;
import play.data.validation.Required;
import play.db.jpa.Blob;
import play.db.jpa.JPABase;
import play.i18n.Lang;
import play.libs.MimeTypes;
import play.mvc.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import org.apache.commons.codec.digest.DigestUtils;

import notifiers.Mails;
import models.*;
import play.*;

public class Application extends Controller {

	public static void index() {
		// Mails.welcome();
		// System.out.println(Security.isConnected());
		List<Course> myCourses = new ArrayList<Course>();
		User user = null;
		if (MySecurity.isConnected()) {
			user = Cache.get(session.getId() + "-user", User.class);
			if (user == null) {
				user = User.getUserByEmail(session.get("email"));
				Cache.set(session.getId() + "-user", user, "30mn");
			}
			myCourses = Course.getMyOwnCourses(user);
		}

		for (int i = 0; i < myCourses.size(); i++) {
			System.out.println(myCourses.get(i));
		}
		List<Course> universityCourses = Course.getUniversityCourses();
		List<Course> userCourses = Course.getUserCourses();

		render(universityCourses, userCourses, user);
	}

	public static void downloadFiles() {

		List<User> users = User.findAll();
		List<Homework> homeworks = Homework.findAll();
		List<Lesson> lessons = Lesson.findAll();
		List<Course> courses = Course.findAll();
//		System.out.println("1");
//		for (int i = 0; i < users.size(); i++) {
//			final User user = User.findById(users.get(i).getId());
//			notFoundIfNull(user);
//			if (user.avatar.type() != null) {
//				System.out.println(user.avatar.type());
//				response.setContentTypeIfNotSet(user.avatar.type());
//				renderBinary(user.avatar.get(), user.avatar.toString());
//			}
//		}
//		for (int i = 0; i < lessons.size(); i++) {
//			List<Files> files = lessons.get(i).files;
//			for (int j = 0; j < files.size(); j++) {
//				final Files file = Files.findById(files.get(j).getId());
//				notFoundIfNull(file);
//				response.setContentTypeIfNotSet(file.file.type());
//				renderBinary(file.file.get(), file.file.getUUID());
//			}
//		}
		render(users, homeworks, lessons, courses);
	}

	public static void manageCourses() {
		List<Course> activeCourses = Course.getActiveCourses();
		List<Course> notActiveCourses = Course.getNotActiveCourses();
		List<Course> allCourses = Course.findAll();
		User user = Cache.get(session.getId() + "-user", User.class);
		if (user == null) {
			user = User.getUserByEmail(session.get("email"));
			Cache.set(session.getId() + "-user", user, "30mn");
		}
		render(activeCourses, notActiveCourses, allCourses, user);
	}

	public static void signup() {
		render();
	}

	public static void code() {
		User user = Cache.get(session.getId() + "-user", User.class);
		render(user);
	}

	public static void jsCode() {
		render();
	}

	public static void coursePhoto(long id) {
		final Course course = Course.findById(id);
		notFoundIfNull(course);
		System.out.println(id + "a111111111111");
		response.setContentTypeIfNotSet(course.photo.type());
		renderBinary(course.photo.get());
	}

	public static void save(Long id,
			@Required(message = "Email is required") String email,
			@Required(message = "Password is required") String pwd,
			@Required(message = "Password confirmation is required") String cpwd)
			throws Throwable {

		System.out.println(email);
		System.out.println(pwd);
		System.out.println(cpwd);

		if (!pwd.equals(cpwd)) {
			flash.keep("url");
			flash.error("passwords are not equal");
			params.flash();
			signup();
		}
		User user1 = User.getUserByEmail(email);
		if (user1 != null) {
			flash.keep("url");
			flash.error("username with this name is already exists");
			params.flash();
			signup();
		}

		validation.valid(user1);
		if (validation.hasErrors()) {
			render("Application/signup.html", user1);
		}
		pwd = DigestUtils.md5Hex(pwd);
		User user = new User(email, pwd);
		if (email.contains("edu")) {
			user.isTeacher = true;
		}
		user.isActive = true;
		// Save
		user.save();
		String address = email;
		// Mails.verifyUser(email, address);
		MySecure.login();
	}

	public static void verify(String address) {

		ArrayList<User> users = (ArrayList<User>) User.getnotActiveUsersl();

		for (int i = 0; i < users.size(); i++) {
			if (address.equals(users.get(i).email)) {
				users.get(i).isActive = true;
				users.get(i).save();
				render(address);
				break;
			}
		}
		render("errors/404.html");
	}

	public static void changeLanguage(String lang) {
		System.out.println(lang);
		Lang.change(lang);
		profile();
	}

	public static void profile() {
		User user = Cache.get(session.getId() + "-user", User.class);
		if (user == null) {
			user = User.getUserByEmail(session.get("email"));
			Cache.set(session.getId() + "-user", user, "30mn");
		}
		List<models.MyCourse> myCourses = models.MyCourse
				.getMyCoursesByUser(user);
		List<Course> myOwnCourses = Course.getMyOwnCourses(user);
		List<Question> questions = Question.getUserQuestions(user);
		render(user, myCourses, myOwnCourses, questions);
	}

	public static void editAccount() {
		User user = Cache.get(session.getId() + "-user", User.class);
		if (user == null) {
			user = User.getUserByEmail(session.get("email"));
			Cache.set(session.getId() + "-user", user, "30mn");
		}
		user = User.getUserByEmail(session.get("email"));
		DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
		String birthday = formatter.format(new Date());
		if (user.birthday != null) {
			birthday = formatter.format(user.birthday);
		}
		System.out.println(user);
		render(user, birthday);
	}

	public static void userPhoto(long id) {
		final User user = User.findById(id);
		notFoundIfNull(user);
		response.setContentTypeIfNotSet(user.avatar.type());
		renderBinary(user.avatar.get());
	}

	public static void saveProfile(String username, String firstName,
			String lastName, String gender, String birthday, String location,
			String aboutMe, File avatar) throws FileNotFoundException {
		// User user = Cache.get(session.getId() + "-user",User.class);
		// if(user==null){
		// user = User.getUserByEmail(session.get("email"));
		// Cache.set(session.getId() + "-user", user, "30mn");
		// }
		User user = User.getUserByEmail(session.get("email"));
		System.out.println(avatar);
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-mm-yyyy");
		Date convertedDate = null;
		try {
			convertedDate = dateFormat.parse(birthday);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// System.out.println(avatar);
		// User user1 = new User(user.email, user.pwd,avatar);
		// user1.save();

		user.username = username;
		user.photoFileName = avatar.getName();
		user.firstName = firstName;
		user.lastName = lastName;
		user.gender = gender;
		user.birthday = convertedDate;
		user.location = location;
		user.aboutMe = aboutMe;
		user.avatar = new Blob();
		user.avatar.set(new FileInputStream(avatar),
				MimeTypes.getContentType(avatar.getName()));
		user.save();
		editAccount();
	}

	public static void help() {
		render("code/deckCode/introduction/index.html");
	}
}