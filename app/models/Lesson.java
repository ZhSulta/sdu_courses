package models;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.*;

import javax.persistence.*;

import play.data.validation.Email;
import play.data.validation.MaxSize;
import play.data.validation.MinSize;
import play.data.validation.Required;
import play.db.jpa.*;
import play.libs.MimeTypes;

@Entity
@Table(name = "Lessons")
public class Lesson extends Model{
	@ManyToOne
	public Course course;
	public String topic;
	public int position;	//position in this topic
	public String name;
	public Date date;
	@OneToMany(mappedBy="lesson", cascade=CascadeType.ALL, fetch = FetchType.EAGER)
	public List<Files> files;
	@OneToMany(mappedBy="lesson", cascade=CascadeType.ALL, fetch = FetchType.EAGER)
	public Set<Comment> comments;
	
	public Lesson(Course course, String topic, int position, String name,
			Date date) {
		this.course = course;
		this.topic = topic;
		this.position = position;
		this.name = name;
		this.date = date;
		this.files = new LinkedList<Files>();
		this.comments = new HashSet<Comment>();
	}	
	public static List<Lesson> getCourseLesson(Course course){
		 return Lesson.find("byCourse", course).fetch();		 
	}
}
