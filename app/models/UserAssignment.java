package models;

import java.util.*;

import javax.persistence.*;

import play.data.validation.Email;
import play.data.validation.MaxSize;
import play.data.validation.MinSize;
import play.data.validation.Required;
import play.db.jpa.*;

@Entity
@Table(name = "UserAssignments")
public class UserAssignment extends Model{
	@ManyToOne
	public Assignment assignment;
	@ManyToOne
	public User user;
	public Date dateOfAttempt;
	public int score;
	
	@OneToMany(mappedBy="userAssignment", cascade=CascadeType.ALL,fetch = FetchType.EAGER)
	public Set<UserAssignmentAnswer> userAssignmentAnswers;

	public UserAssignment(Assignment assignment, User user, Date dateOfAttempt,
			int score) {
		this.assignment = assignment;
		this.user = user;
		this.dateOfAttempt = dateOfAttempt;
		this.score = score;
		this.userAssignmentAnswers = new HashSet<UserAssignmentAnswer>();
	}
}
