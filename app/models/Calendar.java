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
@Table(name = "Calendar")
public class Calendar extends Model{
	@ManyToOne
	public Course course;
	public Date date;
	public String name;
	
	public Calendar(Course course, Date date, String name) {
		this.course = course;
		this.date = date;
		this.name = name;
	}

	public static List<Calendar> getCalendarByCourse(Course course){
		 return Calendar.find("byCourse", course).fetch();		 
	}
}
