package models;

import java.util.*;

import javax.persistence.*;

import play.data.validation.Email;
import play.data.validation.MaxSize;
import play.data.validation.MinSize;
import play.data.validation.Required;
import play.db.jpa.*;

@Entity
@Table(name = "Questions")
public class Question extends Model{
	@ManyToOne
	public Course course;
	@ManyToOne
	public User user;
	public String title;
	@Lob
	public String text;
	public String tags;
	public int votes_number;
	public int answers_number;
	public boolean active;
	public int views_number;
	public Date publish_date;
	
	@OneToMany(mappedBy="question", cascade=CascadeType.ALL,fetch = FetchType.EAGER)
	public Set<Answer> answers;
	
	public Question(Course course, User user, String title, String text,
			String tags) {
		this.active = true;
		this.course = course;
		this.user = user;
		this.title = title;
		this.text = text;
		this.tags = tags;
		this.votes_number = 0;
		this.answers_number = 0;
		this.views_number = 0;
		this.answers = new HashSet<Answer>();
		this.publish_date = new Date();
	}	
	public static List<Question> getCourseQuestions(Course course,String sortBy,int currentPage, int perPage){
		if(sortBy.equals("new")){
			return Question.find("select q from Question q where q.course = :course order by publish_date").setParameter("course", course).fetch(currentPage - 1, perPage);
		}else if(sortBy.equals("active")){
			return Question.find("select q from Question q where q.course = :course and active = true order by publish_date").setParameter("course", course).fetch(currentPage, perPage);
		}else if(sortBy.equals("vote")){
			return Question.find("select q from Question q where q.course = :course order by votes_number desc").setParameter("course", course).fetch();
		}
		return Question.find("select q from Question q where q.course = :course order by :sortBy").setParameter("course", course).setParameter("sortBy", sortBy).fetch();	
//		 Course.find("select c from Course  where c.user != :user").setParameter("user", user).fetch();
	}
	public static List<Question> getUserQuestions(User user){
		 return Question.find("byUser", user).fetch();		 
	}
	public static Question getQuestionById(long id){
		 return Question.findById(id);		 
	}
}
