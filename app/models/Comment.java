package models;

import java.util.*;

import javax.persistence.*;

import play.data.validation.Email;
import play.data.validation.MaxSize;
import play.data.validation.MinSize;
import play.data.validation.Required;
import play.db.jpa.*;

@Entity
@Table(name = "Comments")
public class Comment extends Model{
	
	@ManyToOne
	public Answer answer;
	@ManyToOne
	public Lesson lesson;
	@ManyToOne
	public Homework homework;
	@ManyToOne
	public User user;
	public String text;
	public Date date;
	@OneToOne
	public Comment parent;
	
	public Comment(Answer answer, User user, String text, Date date) {
		super();
		this.answer = answer;
		this.user = user;
		this.text = text;
		this.date = date;
	}
	public Comment(Lesson lesson, User user, String text, Date date) {
		super();
		this.lesson = lesson;
		this.user = user;
		this.text = text;
		this.date = date;
	}
	public Comment(Lesson lesson, User user, String text, Date date,Comment parent) {
		super();
		this.lesson = lesson;
		this.user = user;
		this.text = text;
		this.date = date;
		this.parent = parent;
	}
	public Comment(Homework homework, User user, String text, Date date) {
		super();
		this.homework = homework;
		this.user = user;
		this.text = text;
		this.date = date;
	}
	public Comment(Homework homework, User user, String text, Date date,Comment parent) {
		super();
		this.homework = homework;
		this.user = user;
		this.text = text;
		this.date = date;
		this.parent = parent;
	}
	public static List<Comment> getHomeworkComments(Homework homework){
		 List<Comment> comments = Course.find("select c from Comment c where c.homework = :homework").setParameter("homework", homework).fetch();
		 return comments;
	}
}
