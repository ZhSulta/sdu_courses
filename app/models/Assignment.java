package models;

import java.util.*;

import javax.persistence.*;

import play.data.validation.Email;
import play.data.validation.MaxSize;
import play.data.validation.MinSize;
import play.data.validation.Required;
import play.db.jpa.*;

@Entity
@Table(name = "Assignments")
public class Assignment extends Model{
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
	@OneToMany(mappedBy="assignment", cascade=CascadeType.ALL,fetch = FetchType.EAGER)
	public Set<AssignmentQuestion> questions;
	@OneToMany(mappedBy="assignment", cascade=CascadeType.ALL,fetch = FetchType.EAGER)
	public Set<UserAssignment> userAssignments;
	
	public Assignment(Course course, String topic, String title,
			Date due_date,Date hard_deadline, int position, int attempts) {		
		this.course = course;
		this.topic = topic;
		this.title = title;
		this.attempts = attempts;
		this.publish_date = new Date();
		this.modified_date = new Date();
		this.due_date = due_date;
		this.hard_deadline = hard_deadline;
		this.position = position;
		this.questions = new HashSet<AssignmentQuestion>();
		this.userAssignments = new HashSet<UserAssignment>();
	}
	
	public static List<Assignment> getCourseAssignments(Course course){
		return Assignment.find("select a from Assignment a where a.course = :course order by position").setParameter("course", course).fetch();		 
	}
}
