package models;

import java.util.*;

import javax.persistence.*;

import play.data.validation.Email;
import play.data.validation.MaxSize;
import play.data.validation.MinSize;
import play.data.validation.Required;
import play.db.jpa.*;

@Entity
@Table(name = "AssignmentQuestions")
public class AssignmentQuestion extends Model{
	@ManyToOne
	public Assignment assignment;
	public String title;
	@Lob
	public String text;
	public int position;
	public int score;
	public String explanation;
	public int oneAnswer;
	
	@OneToMany(mappedBy="question", cascade=CascadeType.ALL,fetch = FetchType.EAGER)
	public Set<AssignmentAnswer> answers;
	
	public AssignmentQuestion(Assignment assignment, String title, String text,
			int position, int score,String explanation,int oneAnswer) {		
		this.assignment = assignment;
		this.oneAnswer = 0;
		this.title = title;
		this.text = text;
		this.position = position;
		this.score = score;
		this.answers = new HashSet<AssignmentAnswer>();
		this.explanation = explanation;
	}
	
	public static List<AssignmentQuestion> getAssignmentQuestions(Assignment assignment){
		return AssignmentQuestion.find("byAssignment",assignment).fetch();		 
	}
	
}
