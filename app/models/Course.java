package models;

import java.util.*;

import javax.persistence.*;

import play.data.validation.Email;
import play.data.validation.MaxSize;
import play.data.validation.MinSize;
import play.data.validation.Required;
import play.db.jpa.*;


@Entity
@Table(name="COURSES")
public class Course extends Model{	

	public String name;
	public String university;
	public int duration;
	public String smallDescription;
	public String description;
	public Blob photo;
	@Lob
	public String syllabus;
	@Lob
    public String resources;
	@Lob
	public String videoUrl;
	public Date date;
	public Date startDate;
	public boolean isActive;
	
	public boolean isTimeTable = true;
	public boolean isLessons = true;
	public boolean isAssignments = true;
	public boolean isDiscussions = true;
	
	@ManyToOne
	public User user;
	@OneToMany(mappedBy="course", cascade=CascadeType.ALL, fetch = FetchType.EAGER)
	public Set<Announcement> announcements;
	@OneToMany(mappedBy="course", cascade=CascadeType.ALL, fetch = FetchType.EAGER)
	public Set<Video> videos;
	@OneToMany(mappedBy="course", cascade=CascadeType.ALL, fetch = FetchType.EAGER)
	public Set<Lesson> lessons;
	@OneToMany(mappedBy="course", cascade=CascadeType.ALL, fetch = FetchType.EAGER)
	public Set<Homework> homeworks;
	@OneToMany(mappedBy="course", cascade=CascadeType.ALL, fetch = FetchType.EAGER)
	public Set<Question> questions;
	@OneToMany(mappedBy="course", cascade=CascadeType.ALL, fetch = FetchType.EAGER)
	public Set<Assignment> assignments;
	@OneToMany(mappedBy="course", cascade=CascadeType.ALL, fetch = FetchType.EAGER)
	public Set<MyCourse> myCourses;
	
	public Course(String name, String university, int duration,
			String smallDescription, String description,
			Date startDate, User user,String resourses, Blob photo,String videoUrl) {
		this.name = name;
		isActive = false;
		this.university = university;
		this.duration = duration;
		this.resources = resourses;
		this.smallDescription = smallDescription;
		this.description = description;
		this.photo = photo;
		this.videoUrl = videoUrl;
		this.date = new Date();
		this.startDate = startDate;
		this.user = user;
		this.announcements = new HashSet<Announcement>();
		this.videos = new HashSet<Video>();
		this.questions = new HashSet<Question>();
		this.assignments = new HashSet<Assignment>();
		this.myCourses = new HashSet<MyCourse>();
		this.homeworks = new HashSet<Homework>();
		this.lessons = new HashSet<Lesson>();
	}
	public static List<Course> getMyOwnCourses(User user){
		 List<Course> courses = Course.find("byUser", user).fetch();
		 return courses;
	}
	public static List<Course> getUniversityCourses(){
		 List<Course> courses = Course.find("select c from Course c where university!=null and isActive = true").fetch();
		 return courses;
	}
	public static List<Course> getUserCourses(){
		 List<Course> courses = Course.find("select c from Course c where university=null and isActive = true").fetch();
		 return courses;
	}
	public static List<Course> getActiveCourses(){
		 List<Course> courses = Course.find("byIsActive",true).fetch();
		 return courses;
	}
	public static List<Course> getNotActiveCourses(){
		 List<Course> courses = Course.find("byIsActive",false).fetch();
		 return courses;
	}	
	public static Course getCourseById(long courseId){
		 return Course.findById(courseId);	 
	}
	public String toString() {
	    return name;
	}
}
