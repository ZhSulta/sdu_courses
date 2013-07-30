package models;

import java.util.*;

import javax.persistence.*;

import play.data.validation.Email;
import play.data.validation.MaxSize;
import play.data.validation.MinSize;
import play.data.validation.Required;
import play.db.jpa.*;

@Entity
@Table(name = "Videos")
public class Video extends Model{
	@ManyToOne
	public Course course;
	public String topic;
	public int position;	//position in this topic
	public String name;
	public Date date;
	public String url;
	
	public Video(Course course, String topic, int position, String name,
			Date date, String url) {
		this.course = course;
		this.topic = topic;
		this.position = position;
		this.name = name;
		this.date = date;
		this.url = url;
	}	
	public static List<Video> getMyOwnVideos(Course course){
		 return Video.find("byCourse", course).fetch();		 
	}
	public String getUrl(){
		return this.url;
	}
}
