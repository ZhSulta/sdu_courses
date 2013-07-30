package models;

import java.util.*;

import javax.persistence.*;

import play.data.validation.Email;
import play.data.validation.MaxSize;
import play.data.validation.MinSize;
import play.data.validation.Required;
import play.db.jpa.*;

@Entity
@Table(name = "Homeworks")
public class Homework extends Model{
	@ManyToOne
	public Course course;
	public String topic;	
	public String title;
	public Date publish_date;
	public Date modified_date;
	public Date due_date;
	public Date hard_deadline;
	public int position;
	public int attempts;
	@OneToMany(mappedBy="homework", cascade=CascadeType.ALL, fetch = FetchType.EAGER)
	public Set<Files> files;
	@OneToMany(mappedBy="homework", cascade=CascadeType.ALL, fetch = FetchType.EAGER)
	public Set<Comment> comments;
	@OneToMany(mappedBy="homework", cascade=CascadeType.ALL, fetch = FetchType.EAGER)
	public Set<UserHomework> userHomeworks;
	@Lob
	public String text;
	
	public Homework(Course course, String topic, String title,
			Date due_date,Date hard_deadline, int position, int attempts,String text) {		
		this.course = course;
		this.topic = topic;
		this.title = title;
		this.attempts = attempts;
		this.publish_date = new Date();
		this.modified_date = new Date();
		this.due_date = due_date;
		this.hard_deadline = hard_deadline;
		this.position = position;
		this.text = text;
		this.files = new HashSet<Files>();
		this.comments = new HashSet<Comment>();
	}
	
	public static List<Homework> getCourseHomeworks(Course course){
		return Homework.find("select h from Homework h where h.course = :course order by position").setParameter("course", course).fetch();		 
	}
}
