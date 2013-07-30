package models;

import java.util.*;

import javax.persistence.*;

import play.data.validation.Email;
import play.data.validation.MaxSize;
import play.data.validation.MinSize;
import play.data.validation.Required;
import play.db.jpa.*;

@Entity
@Table(name = "UserAssignmentAnswers")
public class UserAssignmentAnswer extends Model{
	@ManyToOne
	public UserAssignment userAssignment;
	@ManyToOne
	public AssignmentAnswer answer;
	
	public UserAssignmentAnswer(UserAssignment userAssignment,AssignmentAnswer answer) {
		this.userAssignment = userAssignment;
		this.answer = answer;
	}
	public static List<UserAssignmentAnswer> getUserAssignmentAnswers(UserAssignment userAssignment){
		 return UserAssignmentAnswer.find("byUserAssignment", userAssignment).fetch();		 
	}
}
