package models;

import java.util.*;

import javax.persistence.*;

import play.data.validation.Email;
import play.data.validation.MaxSize;
import play.data.validation.MinSize;
import play.data.validation.Required;
import play.db.jpa.*;

@Entity
@Table(name="MY_COURSES")
public class MyCourse extends Model{
	@ManyToOne
	public User user;
	@ManyToOne
	public Course course;
	public int progress;
	
	public MyCourse(User user, Course course) {
		this.user = user;
		this.course = course;
		this.progress = 0;
	}
	public static MyCourse getMyCourseByCourseAndUser(Course course,User user){
//		 return MyCourse.findById(courseId);	 
		 return MyCourse.find("course = ? and user = ?", course, user ).first();
	}
	
	public static List<MyCourse> getMyCourses(Course course){
		 return MyCourse.find("byCourse", course).fetch();		 
	}
	public static List<MyCourse> getMyCoursesByUser(User user){
		 return MyCourse.find("byUser", user).fetch();		 
	}
}
