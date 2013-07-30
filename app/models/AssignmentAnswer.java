package models;

import java.util.*;

import javax.persistence.*;

import play.data.validation.Email;
import play.data.validation.MaxSize;
import play.data.validation.MinSize;
import play.data.validation.Required;
import play.db.jpa.*;

@Entity
@Table(name = "AssignmentAnswers")
public class AssignmentAnswer extends Model{
	@ManyToOne
	public AssignmentQuestion question;
	public String text;
	public boolean correctness;
	
	@OneToMany(mappedBy="answer", cascade=CascadeType.ALL)
	public Set<UserAssignmentAnswer> userAssignmentAnswers;
	
	public AssignmentAnswer(AssignmentQuestion question, String text,boolean correctness) {		
		this.question = question;
		this.text = text;
		this.correctness = correctness;
	}
//	public static List<AssignmentAnswer> getAssignmentQuestionAnswers(List<AssignmentQuestion> questions){		
//		return AssignmentAnswer.find("select a from AssignmentAnswer a where a.question in (:questions)").setParameter("questions", questions).fetch()").fetch();
//	}
	public static List<AssignmentAnswer> getAssignmentQuestionAnswers(AssignmentQuestion question){		
		return AssignmentAnswer.find("select a from AssignmentAnswer a where a.question = :question").setParameter("question", question).fetch();
	}
}
