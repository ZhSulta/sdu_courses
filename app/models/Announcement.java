package models;

import java.util.*;

import javax.persistence.*;

import play.data.validation.Email;
import play.data.validation.MaxSize;
import play.data.validation.MinSize;
import play.data.validation.Required;
import play.db.jpa.*;

@Entity
@Table(name = "Announcements")
public class Announcement extends Model{	
	@ManyToOne
    @Required		
	public Course course;
	public String topic;
	@Lob
	public String text;
	public Date date;
	
	public Announcement(Course course, String topic, String text) {		
		this.course = course;
		this.topic = topic;
		this.text = text;
		this.date = new Date();
	}
	public static List<Announcement> getCourseAnnouncements(Course course){
		 return Announcement.find("byCourse", course).fetch();		 
	}
	public static void deleteAnnouncement(long id){
		 Announcement announcement = Announcement.findById(id);
		 announcement.delete();
	}
}
