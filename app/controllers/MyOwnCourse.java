package controllers;

import play.*;
import play.cache.Cache;
import play.db.jpa.Blob;
import play.db.jpa.GenericModel;
import play.libs.MimeTypes;
import play.mvc.*;
import utils.PaginationInfo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import org.apache.commons.io.IOUtils;

import com.mysql.jdbc.Connection;

import notifiers.Mails;
import models.*;
import models.Calendar;
@With(MySecure.class)
public class MyOwnCourse extends Controller{
	private static final String File = null;
	@Before
    static void setConnectedUser() {
        if(MySecurity.isConnected()) { 
        	User user = Cache.get(session.getId() + "-user",User.class);
        	if(user==null){
        		user = User.getUserByEmail(session.get("email"));
        		Cache.set(session.getId() + "-user", user, "30mn");
        	}        	
            renderArgs.put("user", user);            
        }
    }	
    public static void courseForm() {
    	User user = (User)renderArgs.get("user");
    	render(user);
    }
    public static void checkCourse(long courseId) {
//    	User user = (User)renderArgs.get("user");
//    	c
//    	List<Course> myCourses = Course.getMyOwnCourses(user);
////    	System.out.println(myCourses.size());
//    	for(int i=0;i<myCourses.size();i++){    		
////    		System.out.println(myCourses.get(i).id);
//    		if(myCourses.get(i).id == courseId){    			
//    			myOwnCourse(courseId,0);
//    			break;
//    		}
//    	}
//    	myCourse(courseId);
    	User user = (User)renderArgs.get("user");
    	System.out.println(user);
    	List<Course> myCourses = Course.getMyOwnCourses(user);
    	for(int i=0;i<myCourses.size();i++){    		
    		System.out.println(myCourses.get(i).name);    		
    	}
//    	System.out.println(myCourses.size());
    	for(int i=0;i<myCourses.size();i++){    		
    		System.out.println(myCourses.get(i).id);
    		if(myCourses.get(i).id == courseId){    			
    			MyOwnCourse.myOwnCourse(courseId,0);
    			break;
    		}
    	}
    	Course course = Cache.get(session.getId() + "_user-course_"+courseId,Course.class);    	
    	if(course==null){
    		course = Course.getCourseById(courseId);    		
    		Cache.set(session.getId() + "_user-course_"+courseId, course,"30mn");
    	}
    	boolean enrolled = false;
    	models.MyCourse c = models.MyCourse.getMyCourseByCourseAndUser(course, user);
    	if(c != null){
    		enrolled = true;
    	}
    	if(enrolled){
    		myOwnCourse(courseId,0);
    	}else{
    		welcomeToCourse(courseId);
    	}
    }
    public static void welcomeToCourse(long courseId) {
    	System.out.println(courseId);
    	Course course = Cache.get(session.getId() + "_user-course_"+courseId,Course.class);    	
    	if(course==null){
    		course = Course.getCourseById(courseId);    		
    		Cache.set(session.getId() + "_user-course_"+courseId, course,"30mn");
    	}
    	render(course);
    }
//    public static void myCourse(long courseId) {    	
//    	System.out.println(courseId);
//    	Course course = Cache.get(session.getId() + "_user-course_"+courseId,Course.class);    	
//    	if(course==null){
//    		course = Course.getCourseById(courseId);    		
//    		Cache.set(session.getId() + "_user-course_"+courseId, course,"30mn");
//    	}
//    	render(course);
//    }
    public static void myOwnCourse(long courseId, long videoId) {
    	Course course = Cache.get(session.getId() + "_user-course_"+courseId,Course.class);    	
    	if(course==null){
    		course = Course.getCourseById(courseId);    		
    		Cache.set(session.getId() + "_user-course_"+courseId, course,"30mn");
    	}
    	courseInfo(courseId);
    	ArrayList<Video> videos = (ArrayList<Video>) Video.getMyOwnVideos(course);
    	Video video = null;
    	if(videoId == 0){
    		if(videos.size()>0){
    			video = videos.get(0);
    		}
    	}else{
    		video = Video.findById(videoId);
    	}
    	render(course,videos,video);
    }
    public static void enroll(long courseId){
    	User user = (User)renderArgs.get("user");
    	Course course = Cache.get(session.getId() + "_user-course_"+courseId,Course.class);    	
    	if(course==null){
    		course = Course.getCourseById(courseId);    		
    		Cache.set(session.getId() + "_user-course_"+courseId, course,"30mn");
    	}
//    	course = Course.getCourseById(courseId);
    	models.MyCourse myCourse = new models.MyCourse(user, course);
    	myCourse.save();
    	myOwnCourse(courseId,0);
    }
    public static void activateCourse(long courseId){
    	Course course = Course.findById(courseId);
    	course.isActive = true;
    	course.save();
    	Application.manageCourses();
    }
    public static void deactivateCourse(long courseId){
    	Course course = Course.findById(courseId);
    	course.isActive = false;
    	course.save();
    	Application.manageCourses();
    }
    public static void deleteCourse(long courseId){
    	Course course = Course.findById(courseId);
    	course.delete();
    	Application.manageCourses();
    }
    public static void sendMessageToCourseOwner(long courseId,String email,String username){
    	System.out.println(courseId+" " + email+" "+username);
    	Mails.correctCourse(email,username);
    	Application.manageCourses();
    }
    public static void editCourse(Long Id,String courseName,String smallDescription,
    		String description,String university,
    		int duration, Blob photo,String videoUrl) {
    	System.out.println(Id+" course id ");
    	System.out.println(videoUrl.indexOf("watch"));
    	if(videoUrl.contains("watch")){
    		videoUrl = videoUrl.substring(0,videoUrl.indexOf("watch"))+"embed/"+videoUrl.substring(videoUrl.indexOf("=")+1);
    		videoUrl+="?autoplay=0&amp;color=red&amp;html5=1&amp;rel=0&amp;showinfo=0&amp;theme=light&amp;wmode=opaque&amp;enablejsapi=1&amp;";    	
        }
    	Course course = Course.getCourseById(Id);
    	course.name = courseName;
    	course.smallDescription = smallDescription;
    	course.description = description;
    	course.university = university;
    	course.duration = duration;
    	course.photo = photo;    	
//    	course.videoUrl = videoUrl;
    	course.save();
    	Cache.set(session.getId() + "_user-course_"+Id, course,"30mn");
    	MyOwnCourse.courseSettings(Id);
    }
    public static void editCourseTabs(Long courseId, boolean isTimeTable,
    		boolean isLessons, boolean isAssignments, boolean isDiscussions) {
    	System.out.println(isTimeTable+" "+isLessons+" "+isAssignments+" "+isDiscussions);
    	Course course = Course.getCourseById(courseId);
    	course.isTimeTable = isTimeTable;	
    	course.isLessons = isLessons;
    	course.isAssignments = isAssignments;
    	course.isDiscussions = isDiscussions;
    	course.save();
    	Cache.set(session.getId() + "_user-course_"+courseId, course,"30mn");
    	MyOwnCourse.courseTabs(courseId);
    }
    public static void createCourse(String courseName,String smallDescription,String description,String university,
    		int duration, Blob photo,String videoUrl) {
    	System.out.println(videoUrl.indexOf("watch"));
    	if(videoUrl.contains("watch")){
    		videoUrl = videoUrl.substring(0,videoUrl.indexOf("watch"))+"embed/"+videoUrl.substring(videoUrl.indexOf("=")+1);
    	}
    	videoUrl+="?autoplay=0&amp;color=red&amp;html5=1&amp;rel=0&amp;showinfo=0&amp;theme=light&amp;wmode=opaque&amp;enablejsapi=1&amp;";    	
    	Date startDate = new Date();
    	User user = (User)renderArgs.get("user");
    	String resourses = "";
    	Course course = new Course(courseName, university, duration, smallDescription, description, startDate, user, resourses, photo,videoUrl);
    	course.save();
    	
    	System.out.println(user);

    	MyOwnCourse.myOwnCourse(course.id,0);
    }
   
    public static void courseInfo(long courseId){
    	Course course = Cache.get(session.getId() + "_user-course_"+courseId,Course.class);    	
    	if(course==null){
    		course = Course.getCourseById(courseId);    		
    		Cache.set(session.getId() + "_user-course_"+courseId, course,"30mn");
    	}
    	ArrayList<Announcement> announcements = (ArrayList<Announcement>) Announcement.getCourseAnnouncements(course);
//    	for(int i=0;i<announcements.size();i++){
//    		System.out.println(announcements.get(i).text);
//    	}
    	render(course,announcements);
    }
    
    public static void code(long courseId){
    	Course course = Cache.get(session.getId() + "_user-course_"+courseId,Course.class);    	
    	if(course==null){
    		course = Course.getCourseById(courseId);    		
    		Cache.set(session.getId() + "_user-course_"+courseId, course,"30mn");
    	}
    	
    	render(course);
    }
    
    public static void addCourseInfo(Long id,String courseInfo, String topic,long courseId) {
    	System.out.println("id         sd   "+id);
    	Announcement announcement;
    	if(id==null){
    		Course course = Cache.get(session.getId() + "_user-course_"+courseId,Course.class);    	
        	if(course==null){
        		course = Course.getCourseById(courseId);    		
        		Cache.set(session.getId() + "_user-course_"+courseId, course,"30mn");
        	}
    		announcement = new Announcement(course, topic, courseInfo);
    	}else{
    		announcement = Announcement.findById(id);
    		announcement.topic = topic;
    		announcement.text = courseInfo;    		
    	}
    	
    	announcement.save();    	
    	//System.out.println(courseInfo);
    	courseInfo(courseId);
    }
    public static void deleteCourseInfo(long courseId,long announcementId){
    	System.out.println(courseId);
    	System.out.println(announcementId);
    	Announcement.deleteAnnouncement(announcementId);
    	courseInfo(courseId);
    }
    public static void syllabus(long courseId){
    	Course course = Cache.get(session.getId() + "_user-course_"+courseId,Course.class);    	
    	if(course==null){
    		course = Course.getCourseById(courseId);    		
    		Cache.set(session.getId() + "_user-course_"+courseId, course,"30mn");
    	}
    	render(course);
    }
    public static void editSyllabus(long courseId,String syllabus){    	
    	Course course = Course.getCourseById(courseId); 
    	course.syllabus = syllabus;    	
    	course.save();
    	Cache.set(session.getId() + "_user-course_"+courseId, course,"30mn");
    	syllabus(courseId);
    }
    public static void tutorials_resourses(long courseId){
    	Course course = Cache.get(session.getId() + "_user-course_"+courseId,Course.class);    	
    	if(course==null){
    		course = Course.getCourseById(courseId);    		
    		Cache.set(session.getId() + "_user-course_"+courseId, course,"30mn");
    	}
    	System.out.println(course);
    	render(course);
    }
    public static void myOwnCourses() {
    	User user = (User)renderArgs.get("user");
    	List<Course> myCourses = Course.getMyOwnCourses(user);
    	render(myCourses);
    }
    public static void myCourses() {
    	User user = (User)renderArgs.get("user");
    	List<MyCourse> myCourses = MyCourse.getMyCoursesByUser(user);
    	render(myCourses);
    }
    public static void editResourses(long courseId,String resources){    	
    	Course course = Course.getCourseById(courseId); 
    	course.resources = resources;    	
    	course.save();
    	Cache.set(session.getId() + "_user-course_"+courseId, course,"30mn");
    	tutorials_resourses(courseId);
    }
    public static void assignments(long courseId){
    	Course course = Cache.get(session.getId() + "_user-course_"+courseId,Course.class);    	
    	if(course==null){
    		course = Course.getCourseById(courseId);    		
    		Cache.set(session.getId() + "_user-course_" + courseId, course,"30mn");
    	}    	
    	List<Assignment> assignments = Assignment.getCourseAssignments(course);
    	Set<String> topics = new HashSet<String>();
    	for(int i=0;i<assignments.size();i++){
    		topics.add(assignments.get(i).topic);    		
    	}
    	List<Homework> homeworks= Homework.getCourseHomeworks(course);
    	Set<String> homeworkTopics = new HashSet<String>();
    	for(int i=0;i<homeworks.size();i++){
    		homeworkTopics.add(homeworks.get(i).topic);    		
    	}
    	System.out.println(homeworkTopics.size());
    	render(course,assignments,topics,homeworks,homeworkTopics);
    }
    public static void addAssignment(long courseId,String title, String topic,int attempts,String due_date){
    	Course course = Cache.get(session.getId() + "_user-course_"+courseId,Course.class);    	
    	if(course==null){
    		course = Course.getCourseById(courseId);    		
    		Cache.set(session.getId() + "_user-course_" + courseId, course,"30mn");
    	}    	
    	int position = 0;
    	
    	SimpleDateFormat dateFormat = new SimpleDateFormat("dd-mm-yyyy"); 
    	Date convertedDueDate = null;
		try {
			convertedDueDate = dateFormat.parse(due_date);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Date hard_deadline = convertedDueDate;
    	Assignment assignment = new Assignment(course, topic, title, convertedDueDate, hard_deadline, position, attempts);
    	assignment.save();
    	assignments(courseId);
    }
    public static void assignment(long courseId,long assignmentId){
    	Course course = Cache.get(session.getId() + "_user-course_"+courseId,Course.class);    	
    	if(course==null){
    		course = Course.getCourseById(courseId);    		
    		Cache.set(session.getId() + "_user-course_" + courseId, course,"30mn");
    	}    	
    	Assignment assignment = Assignment.findById(assignmentId);
    	List<AssignmentQuestion> questions = AssignmentQuestion.getAssignmentQuestions(assignment);
    	System.out.println(questions.size());
//    	for(int i=0;i<questions.size();i++){
//    		System.out.println(questions.get(i).answers.size());
//    	}
//    	List<AssignmentAnswer> answers = AssignmentAnswer.getAssignmentQuestionAnswers(question);
    	render(course,assignment,questions);
    }
    public static void addAssignmentQuestion(long courseId,long assignmentId,String title, String text,int score,String explanation){
    	int position = 0;
    	Assignment assignment = Assignment.findById(assignmentId);
    	int oneAnswer = 0;
    	AssignmentQuestion question = new AssignmentQuestion(assignment, title, text, position, score, explanation,oneAnswer);
    	question.save();
    	assignment(courseId,assignmentId);
    }
    public static void addAssignmentAnswer(long assignmentAnswerId,long courseId,long assignmentId,long questionId,
    		String text, boolean correctness){
    	System.out.println(assignmentAnswerId);
    	if(assignmentAnswerId==0){
    		AssignmentQuestion question = AssignmentQuestion.findById(questionId);
    		if(correctness){
    			question.oneAnswer++;
    		}
    		question.save();
        	AssignmentAnswer answer = new AssignmentAnswer(question, text, correctness);
        	answer.save();
    	}else{
    		AssignmentQuestion question = AssignmentQuestion.findById(questionId);
    		AssignmentAnswer answer = AssignmentAnswer.findById(assignmentAnswerId);
    		if(correctness&&!answer.correctness){
    			question.oneAnswer++;
    		}else if(!correctness&&answer.correctness){
    			question.oneAnswer--;
    		}
    		
    		question.save();
    		answer.text = text;
    		answer.correctness = correctness;
    		answer.save();
    	}
    	assignment(courseId,assignmentId);
    }
    public static void deleteAssignmentAnswer(long courseId,long assignmentId,long questionId, long answerId){
    	AssignmentAnswer answer = AssignmentAnswer.findById(answerId);
    	if(answer.correctness){
    		AssignmentQuestion assignmentQuestion = AssignmentQuestion.findById(questionId);    		
    		assignmentQuestion.oneAnswer--;
    		assignmentQuestion.save();
    	}    	
    	answer.delete();
    	assignment(courseId,assignmentId);
    }
    
    public static void checkAssignment(Long courseId,Long assignmentId,Long answer[]){  
    	System.out.println(courseId);
    	System.out.println(assignmentId);
    	if(answer!=null){
    		for(int i=0;i<answer.length;i++){
        		System.out.print(answer[i]+ " ");
        	}
    	}
    	

    	Date dateOfAttempt = new Date();
    	User user = Cache.get(session.getId() + "-user",User.class);
    	Assignment assignment = Assignment.findById(assignmentId);
    	int score = 0;
    	int totalScore = 0;
    	UserAssignment userAssignment = new UserAssignment(assignment, user, dateOfAttempt, score);
//    	AssignmentQuestion[] assignmentQuestions = (AssignmentQuestion[]) assignment.questions.toArray();
//    	for(int i=0;i<assignmentQuestions.length;i++){
//    		totalScore+=assignmentQuestions[i].score;
//    	}
    	for (AssignmentQuestion assignmentQuestion : assignment.questions){
    		totalScore+=assignmentQuestion.score;
    	}
    	UserAssignmentAnswer userAssignmentAnswer;
    	UserAssignmentAnswer userAssignmentAnswer1[] = null;
    	if(answer!=null){
    		userAssignmentAnswer1 = new UserAssignmentAnswer[answer.length];
    		for(int i=0;i<answer.length;i++){
	    		System.out.print(answer[i]+" ");
	    		AssignmentAnswer answer1 = AssignmentAnswer.findById(answer[i]);
	    		if(answer1.correctness){
	    			if(answer1.question.oneAnswer!=0){
	    				score+=answer1.question.score/answer1.question.oneAnswer;
	    			}	    			
	    		}
	        	userAssignmentAnswer = new UserAssignmentAnswer(userAssignment, answer1);
	        	userAssignmentAnswer1[i] = userAssignmentAnswer;	    
	        	userAssignment.userAssignmentAnswers.add(userAssignmentAnswer);
	    	}
    	}
    	System.out.println("score is "+score);
    	userAssignment.score = score;
    	userAssignment.save();
    	if(userAssignmentAnswer1!=null){
    		for(int i=0;i<userAssignmentAnswer1.length;i++){
    			userAssignmentAnswer1[i].save();
    		}
    	}
    	for(int i=0;i<userAssignment.userAssignmentAnswers.size();i++){
    		System.out.println(userAssignment.userAssignmentAnswers.size());
    	}
    	Course course = Cache.get(session.getId() + "_user-course_"+courseId,Course.class);    	
    	if(course==null){
    		course = Course.getCourseById(courseId);    		
    		Cache.set(session.getId() + "_user-course_" + courseId, course,"30mn");
    	}    
    	List<AssignmentQuestion> questions = AssignmentQuestion.getAssignmentQuestions(assignment);
    	List<UserAssignmentAnswer> userAssignmentAnswers = UserAssignmentAnswer.getUserAssignmentAnswers(userAssignment);
    	render("MyOwnCourse/assignmentFeedback.html",course,assignment,userAssignment,questions,userAssignmentAnswers,totalScore);

    	
    	
    	
//    	    	assignment(courseId,assignmentId);
//    	assignmentFeedback(courseId, assignmentId,userAssignment);
    }
    public static void editor(long id) {
    	System.out.println(id);
//       //final User user = User.findById(id);
  	    UserHomework file = UserHomework.findById(id);
  	  System.out.println(file);
//  	   notFoundIfNull(file);
//  	   response.setContentTypeIfNotSet(file.file.type());
//  	   renderBinary(file.file.get(), file.fileName);
// 	
//  	   
//    	User user = (User)renderArgs.get("user");
  	    System.out.println(file.file);
	  	InputStream in = file.file.get();
	  	System.out.println(file.fileName);
	  	String ext = "";
	  	if(file.fileName.contains(".")){
	  		int s = file.fileName.lastIndexOf('.');
	  		if(s+1<file.fileName.length()){
	  			ext = file.fileName.substring(s+1, file.fileName.length());
	  		}	  		
	  	}
	  	System.out.println(ext);
	  	if(!ext.equals("js")&&!ext.equals("java")){
	  		ext = "";
	  	}
	  	String out = "";
	  	try {
			out = IOUtils.toString(in, "UTF-8");
			
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	  	String out1 = out.substring(0,out.length()-1);
	  	render(out1,ext);
    }
    public static void createChecker(long courseId,long homeworkId){
    	Course course = Course.getCourseById(courseId);
    	Cache.set(session.getId() + "_user-course_"+courseId, course,"30mn");	
    	Homework  homework = Homework.findById(homeworkId);
    	render(course,homework);
    }
    public static void addChecker(long courseId,long homeworkId,String input, String output){
    	Course course = Course.getCourseById(courseId);
    	Cache.set(session.getId() + "_user-course_"+courseId, course,"30mn");	
    	Homework  homework = Homework.findById(homeworkId);
    	String lang = "java";
    	Checker checker = new Checker(homework, input, output, lang);
    	checker.save();
    	homework(courseId, homeworkId);
    }
    public static void createHomework(long courseId,long homeworkId){
    	Course course = Course.getCourseById(courseId);
    	Cache.set(session.getId() + "_user-course_"+courseId, course,"30mn");
    	
    	if(homeworkId!=0){
    		Homework homework = Homework.findById(homeworkId);
    		System.out.println(homework.files.size());
    		render(course,homework);
    	}else{
    		render(course);
    	}
    }
	public static void addHomework(long homeworkId,long courseId,String title, String topic,
    		int attempts,String due_date,String text, File file){
    	Course course = Cache.get(session.getId() + "_user-course_"+courseId,Course.class);    	
    	if(course==null){
    		course = Course.getCourseById(courseId);    		
    		Cache.set(session.getId() + "_user-course_" + courseId, course,"30mn");
    	}    	
    	int position = 0;
    	System.out.println(due_date);
    	SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy"); 
    	Date convertedDueDate = null;
		try {
			convertedDueDate = dateFormat.parse(due_date);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Date hard_deadline = convertedDueDate;
		System.out.println(hard_deadline);
		System.out.println(homeworkId+"homeworkId");
    	if(homeworkId!=0){
    		Homework homework = Homework.findById(homeworkId);
    		homework.title = title;
    		homework.topic = topic;
    		homework.attempts = attempts;
    		homework.due_date = hard_deadline;
    		homework.text = text;
    		if(file!=null){
        		System.out.println(file.getName());
            	Files files =  new Files(hard_deadline, file, homework);
            	files.save();
            }
    		Calendar calendar = new Calendar(course, hard_deadline, "Homework");
    		calendar.save();
        	homework.save();
        }else{
        	Homework homework = new Homework(course, topic, title, hard_deadline, hard_deadline,
    				position, attempts, text);		
        	homework.save();
        	Calendar calendar = new Calendar(course, hard_deadline, "Homework");
    		calendar.save();
        	if(file!=null){
        		System.out.println(file.getName());
            	Files files =  new Files(hard_deadline, file, homework);
            	files.save();
            }
        }
		assignments(courseId);
    }
    public static void homework(long courseId,long homeworkId){
    	Course course = Cache.get(session.getId() + "_user-course_"+courseId,Course.class);    	
    	if(course==null){
    		course = Course.getCourseById(courseId);    		
    		Cache.set(session.getId() + "_user-course_" + courseId, course,"30mn");
    	}    	
    	Homework homework = Homework.findById(homeworkId);
    	List <Comment> comments = Comment.getHomeworkComments(homework);
    	User user = (User)renderArgs.get("user");
    	if(user.email.equals(session.get("email"))){
    		List<UserHomework> userHomeworks = UserHomework.getUserHomeworks(homework);
    		render(course,homework,comments,userHomeworks);
    	}
//    	List<AssignmentQuestion> questions = AssignmentQuestion.getAssignmentQuestions(assignment);
//    	System.out.println(questions.size());
//    	for(int i=0;i<questions.size();i++){
//    		System.out.println(questions.get(i).answers.size());
//    	}
//    	List<AssignmentAnswer> answers = AssignmentAnswer.getAssignmentQuestionAnswers(question);
    	render(course,homework,comments);
    }
	public static void deleteHomeworkFile(long courseId, long homeworkId,long id) {
		final Files file = Files.findById(id);
		file.delete();
		homework(courseId,homeworkId);
	}
    public static void addHomeworkComment(long courseId,long homeworkId, String text,long parent){
    	System.out.println(parent);
    	
    	Date date = new Date();
    	User user = (User)renderArgs.get("user");
    	Homework homework = Homework.findById(homeworkId);
    	Comment comment = null;
    	if(parent!=0){
    		Comment commentParent = Comment.findById(parent);
        	comment = new Comment(homework, user, text, date, commentParent);
        }else{
        	comment = new Comment(homework, user, text, date);
    	}
    	comment.save();
    	homework(courseId, homeworkId);
    }
    public static void addUserHomework(long courseId,long homeworkId,File file){
    	Course course = Cache.get(session.getId() + "_user-course_"+courseId,Course.class);    	
    	if(course==null){
    		course = Course.getCourseById(courseId);    		
    		Cache.set(session.getId() + "_user-course_" + courseId, course,"30mn");
    	}    	
    	
    	//SimpleDateFormat dateFormat = new SimpleDateFormat("dd-mm-yyyy"); 
    	User user = (User)renderArgs.get("user");
    	Homework homework = Homework.findById(homeworkId);
    	Date date = new Date();
		UserHomework userHomework = new UserHomework(homework, user, date, 0, false,file);
		userHomework.save();
        
		assignments(courseId);
    }
    public static void editUserHomework(long courseId,long homeworkId, long userHomeworkId,int score,boolean pass){
    	Course course = Cache.get(session.getId() + "_user-course_"+courseId,Course.class);    	
    	if(course==null){
    		course = Course.getCourseById(courseId);    		
    		Cache.set(session.getId() + "_user-course_" + courseId, course,"30mn");
    	}    	
    	
    	//SimpleDateFormat dateFormat = new SimpleDateFormat("dd-mm-yyyy"); 
    	User user = (User)renderArgs.get("user");
    	UserHomework userHomework = UserHomework.findById(userHomeworkId);
    	userHomework.pass = pass;
    	userHomework.score = score;
    	userHomework.save();
        
    	homework(courseId,homeworkId);
    }
    public static void deleteUserHomeworkFile(long courseId, long homeworkId,long id) {
    	UserHomework userHomework = UserHomework.findById(id);
    	userHomework.delete();
		homework(courseId,homeworkId);
	}
    public static void discussion(long courseId,String search,int page){
    	Course course = Cache.get(session.getId() + "_user-course_"+courseId,Course.class);    	
    	if(course==null){
    		course = Course.getCourseById(courseId);    		
    		Cache.set(session.getId() + "_user-course_"+courseId, course,"30mn");
    	}
//    	List<Question> questions = null;
//    	if(sortBy==null||sortBy.equals("active")){
//    		questions = Question.getCourseQuestions(course,"active");
//    	}else if(sortBy.equals("new")){
//    		questions = Question.getCourseQuestions(course,"new");
//    	}else{
//    		questions = Question.getCourseQuestions(course,"vote");
//    	}
    	System.out.println("page is "+page);
    	int currentPage = 0;
    	if(page == 0){
    		currentPage = PaginationInfo.getCurrentPage();
    	}else{
    		currentPage = page;
    	}
    	int perPage = 10;
    	List<Question> aQuestions = Question.getCourseQuestions(course,"active",currentPage,perPage);
    	int total = models.Question.all().fetch().size();
    	System.out.println(currentPage);
    	System.out.println(aQuestions.size());
//    	List<Question> nQuestions = Question.getCourseQuestions(course,"new");
//    	List<Question> vQuestions = Question.getCourseQuestions(course,"vote");
    	
    	//ValuePaginator vpaginator = new ValuePaginator(aQuestions);    	
    	
    	
    	
    	PaginationInfo paginationInfo = new PaginationInfo("MyOwnCourse.discussion",
                currentPage, perPage, total,""+courseId);
    	
//    	vpaginator.setPageSize(2);
//    	vpaginator.setBoundaryControlsEnabled(false);
//    	vpaginator.setPagesDisplayed(0);
//    	System.out.println(vpaginator.getPagesDisplayed());
//    	renderArgs.put("aQuestions", vpaginator);
//    	System.out.println(vpaginator.getLastRowIndex());
//    	
//    	renderArgs.put("page", vpaginator.getPageNumber());
//    	
//    	vpaginator = new ValuePaginator(nQuestions);    	
//    	vpaginator.setPageSize(2);
//    	vpaginator.setBoundaryControlsEnabled(true);
//    	vpaginator.setPagesDisplayed(1);
//    	renderArgs.put("nQuestions", vpaginator);
//    	
//    	vpaginator = new ValuePaginator(vQuestions);    	
//    	vpaginator.setPageSize(2);
//    	vpaginator.setBoundaryControlsEnabled(true);
//    	vpaginator.setPagesDisplayed(1);    	
//    	renderArgs.put("vQuestions", vpaginator);
    	if(search==null){
    		search = "acitve";
    	}
    	render(course,search,aQuestions,paginationInfo);    	
    }
    public static void listNewQuestions() {
    	List<Question> questions = Question.findAll();
    	for(int i=0;i<questions.size();i++){
    		System.out.println(questions.get(i).title);
    	}
        renderJSON(questions);
    }
    public static void addQuestion(long courseId,String title,String text){
    	String tags = "";
    	Course course = Cache.get(session.getId() + "_user-course_"+courseId,Course.class);    	
    	if(course==null){
    		course = Course.getCourseById(courseId);    		
    		Cache.set(session.getId() + "_user-course_"+courseId, course,"30mn");
    	}
    	User user = (User)renderArgs.get("user");
    	Question question = new Question(course, user, title, text, tags);
    	question.save();
    	discussion(courseId,null,1);
    }
    public static void deleteQuestion(long courseId,long questionId){
    	Question question = Question.getQuestionById(questionId);
    	question.delete();    	
    	discussion(courseId,null,1);
    }
    public static void question(long courseId,long questionId){
    	Question question = Question.getQuestionById(questionId);
    	if(session.get("question_id-"+questionId)==null){
	    	session.put("question_id-"+questionId, questionId);
	    	question.views_number = question.views_number+1;
    	}
    	Course course = Cache.get(session.getId() + "_user-course_"+courseId,Course.class);    	
    	if(course==null){
    		course = Course.getCourseById(courseId);    		
    		Cache.set(session.getId() + "_user-course_"+courseId, course,"30mn");
    	}
    	question.save();
    	List<Answer> answers = Answer.getAnswers(question);
    	render(course,question,answers);
    }
    public static void addAnswer(long courseId,long questionId,String text){
    	User user = (User)renderArgs.get("user");
    	Question question = Question.getQuestionById(questionId);
    	question.answers_number = question.answers_number+1;
    	question.save();
    	Answer answer = new Answer(question, user, text);
    	answer.save();
    	question(courseId,questionId);    	
    }
    public static void deleteAnswer(long courseId,long questionId,long answerId){
    	Answer answer = Answer.findById(answerId);
    	answer.question.answers_number = answer.question.answers_number-1;    	
    	answer.delete();    	
    	question(courseId,questionId);
    }
    public static void progress(long courseId){
    	Course course = Cache.get(session.getId() + "_user-course_"+courseId,Course.class);    	
    	if(course==null){
    		course = Course.getCourseById(courseId);    		
    		Cache.set(session.getId() + "_user-course_"+courseId, course,"30mn");
    	}
    	render(course);
    }
    public static void courseSettings(long courseId){
    	Course course = Cache.get(session.getId() + "_user-course_"+courseId,Course.class);    	
    	if(course==null){
    		course = Course.getCourseById(courseId);    		
    		Cache.set(session.getId() + "_user-course_"+courseId, course,"30mn");
    	}
    	render(course);
    }
    public static void courseTabs(long courseId){
    	Course course = Cache.get(session.getId() + "_user-course_"+courseId,Course.class);    	
    	if(course==null){
    		course = Course.getCourseById(courseId);    		
    		Cache.set(session.getId() + "_user-course_"+courseId, course,"30mn");
    	}
    	render(course);
    }
    public static void courseTeachers(long courseId){
    	Course course = Cache.get(session.getId() + "_user-course_"+courseId,Course.class);    	
    	if(course==null){
    		course = Course.getCourseById(courseId);    		
    		Cache.set(session.getId() + "_user-course_"+courseId, course,"30mn");
    	}
    	render(course);
    }
    public static void courseStudents(long courseId){
    	Course course = Cache.get(session.getId() + "_user-course_"+courseId,Course.class);    	
    	if(course==null){
    		course = Course.getCourseById(courseId);    		
    		Cache.set(session.getId() + "_user-course_"+courseId, course,"30mn");
    	}
    	render(course);
    }
    public static void calendar(long courseId){
    	Course course = Cache.get(session.getId() + "_user-course_"+courseId,Course.class);    	
    	if(course==null){
    		course = Course.getCourseById(courseId);    		
    		Cache.set(session.getId() + "_user-course_"+courseId, course,"30mn");
    	}
    	List<Calendar> datas = Calendar.getCalendarByCourse(course);
    	render(course,datas);
    }
    public static void lesson(long courseId){
//    	Course course = Cache.get(session.getId() + "_user-course_"+courseId,Course.class);    	
//    	if(course==null){
//    		course = Course.getCourseById(courseId);    		
//    		Cache.set(session.getId() + "_user-course_"+courseId, course,"30mn");
//    	}
    	Course course = Course.getCourseById(courseId);
    	Cache.set(session.getId() + "_user-course_"+courseId, course,"30mn");
    	render(course);
    }
    public static void textLesson(long courseId,long lessonId){
//    	Course course = Cache.get(session.getId() + "_user-course_"+courseId,Course.class);    	
//    	if(course==null){
//    		course = Course.getCourseById(courseId);    		
//    		Cache.set(session.getId() + "_user-course_"+courseId, course,"30mn");
//    	}
    	Course course = Course.getCourseById(courseId);
    	Cache.set(session.getId() + "_user-course_"+courseId, course,"30mn");    	
    	Lesson lesson = Lesson.findById(lessonId);
    	render(course,lesson);
    }
    public static void editLesson(long lessonId, String topic, long courseId, String name,File file){
    	Date date = new Date();
    	int position = 1;
    	Course course = Course.getCourseById(courseId);
    	Cache.set(session.getId() + "_user-course_"+courseId, course,"30mn");
    	Lesson lesson = Lesson.findById(lessonId);
    	lesson.topic = topic;
    	lesson.name = name;
    	System.out.println(file);
    	if(file!=null){
    		Files files =  new Files(date, file, lesson);
        	files.save();
        }
    	lesson.save();
    	textLesson(courseId,lessonId);
    }
    public static void createLesson(long courseId,long lessonId){
    	
    	Course course = Course.getCourseById(courseId);
    	Cache.set(session.getId() + "_user-course_"+courseId, course,"30mn");
    	render(course);
    }
    public static void addLessonComment(long courseId,long lessonId, String text,long parent){
    	System.out.println(parent);
    	
    	Date date = new Date();
    	User user = (User)renderArgs.get("user");
    	Lesson lesson = Lesson.findById(lessonId);
    	Comment comment = null;
    	if(parent!=0){
    		Comment commentParent = Comment.findById(parent);
        	comment = new Comment(lesson, user, text, date, commentParent);
        }else{
        	comment = new Comment(lesson, user, text, date);
    	}
    	comment.save();
    	textLesson(courseId, lessonId);
    }
    public static void videoLesson(long courseId,long videoId){
//    	Course course = Cache.get(session.getId() + "_user-course_"+courseId,Course.class);    	
//    	if(course==null){
//    		course = Course.getCourseById(courseId);    		
//    		Cache.set(session.getId() + "_user-course_"+courseId, course,"30mn");
//    	}
    	Course course = Course.getCourseById(courseId);
    	Cache.set(session.getId() + "_user-course_"+courseId, course,"30mn");    	
    	Video video = Video.findById(videoId);    	
    	render(course,video);
    }
    public static void addVideo(long videoId, String topic, long courseId, String name, String url) {
//    	System.out.println(videoId+"       111111111111111111111");
//    	System.out.println(url.indexOf("watch"));?
    	if(url.contains("watch")){
    		url = url.substring(0,url.indexOf("watch"))+"embed/"+url.substring(url.indexOf("=")+1);
    	}
    	url+="?autoplay=0&amp;color=red&amp;html5=1&amp;rel=0&amp;showinfo=0&amp;theme=light&amp;wmode=opaque&amp;enablejsapi=1&amp;";
//    	System.out.println(url);
//    	System.out.println(courseId+" courseId");
    	Date date = new Date();
    	int position = 1;
    	Course course = Cache.get(session.getId() + "_user-course_"+courseId,Course.class);    	
    	if(course==null){
    		course = Course.getCourseById(courseId);    		
    		Cache.set(session.getId() + "_user-course_"+courseId, course,"30mn");
    	}
    	Video video = new Video(course, topic, position, name, date, url);
    	video.save();
    	if(videoId!=0){
    		videoLesson(courseId, videoId);
    	}else{
    		lesson(courseId);
    	}    	
    }   
    public static void removeVideo(long courseId, long videoId) {
    	Video video  = Video.findById(videoId);
		video.delete();
		lesson(courseId);
    }
    public static void addLesson(long lessonId, String topic, long courseId, String name, String url, 
    		File file) {
    	Date date = new Date();
    	int position = 1;
    	Course course = Cache.get(session.getId() + "_user-course_"+courseId,Course.class);    	
    	if(course==null){
    		course = Course.getCourseById(courseId);    		
    		Cache.set(session.getId() + "_user-course_"+courseId, course,"30mn");
    	}
    	Lesson lesson = new Lesson(course, topic, position, name, date);
    	lesson.save();
    	Files files =  new Files(date, file, lesson);
    	files.save();
    	lesson(courseId);
//    	if(videoId!=0){
//    		videoLesson(courseId, videoId);
//    	}else{
//    		lesson(courseId);
//    	}    	
    }   
    public static void downloadAdminAvatar(long id) {
  	   //final User user = User.findById(id);
  	   final User user = User.findById(id);
  	   notFoundIfNull(user);
  	   response.setContentTypeIfNotSet(user.avatar.type());
  	   renderBinary(user.avatar.get(),user.avatar.getUUID());
 	}
    public static void downloadAdminPhoto(long id) {
   	   //final User user = User.findById(id);
   	   final Course course = Course.findById(id);
   	   notFoundIfNull(course);
   	   response.setContentTypeIfNotSet(course.photo.type());
   	   renderBinary(course.photo.get(),course.photo.getUUID());
  	}
    public static void downloadAdminFile(long id) {
    	//final User user = User.findById(id);
  	   final Files file = Files.findById(id);
  	   notFoundIfNull(file);
  	   response.setContentTypeIfNotSet(file.file.type());
  	   renderBinary(file.file.get(), file.file.getUUID());
 	}
    public static void downloadFile(long id) {
 	   //final User user = User.findById(id);
 	   final Files file = Files.findById(id);
 	   notFoundIfNull(file);
 	   response.setContentTypeIfNotSet(file.file.type());
 	   renderBinary(file.file.get(), file.fileName);
	}
	public static void deleteLessonFile(long courseId, long lessonId,long id) {
		final Files file = Files.findById(id);
		file.delete();
		textLesson(courseId,lessonId);
	}
}
