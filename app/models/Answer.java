package models;

import java.util.*;

import javax.persistence.*;

import play.data.validation.Email;
import play.data.validation.MaxSize;
import play.data.validation.MinSize;
import play.data.validation.Required;
import play.db.jpa.*;

@Entity
@Table(name = "Answers")
public class Answer extends Model{
	@ManyToOne
	public Question question;
	@ManyToOne
	public User user;
	public String text;
	public Date publish_date;
	public int votes;
	@OneToMany(mappedBy="answer", cascade=CascadeType.ALL)
	public Set<Comment> comments;
	
	public Answer(Question question, User user, String text) {		
		this.question = question;
		this.user = user;
		this.text = text;
		this.publish_date = new Date();
		this.votes = 0;
		this.comments = new HashSet<Comment>();
	}
	public static List<Answer> getAnswers(Question question){
		 return Answer.find("byQuestion", question).fetch();		 
	}
}
