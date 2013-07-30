package models;

import java.io.Serializable;
import java.util.*;

import javax.persistence.*;
import play.data.validation.Email;
import play.data.validation.MaxSize;
import play.data.validation.MinSize;
import play.data.validation.Required;
import play.data.validation.Unique;
import play.db.jpa.*;

@Entity
@Table(name = "Users")
public class User extends Model implements Serializable{
	@MinSize(5)
	@MaxSize(30)
	public String username;
	@Required
	@MinSize(5)
	@MaxSize(20)
	public String pwd;
	@Email
	@Required
	@Unique
	public String email;
	
	public String firstName;
	public String lastName;
	public String gender;
	public String location;
	public Blob avatar;
	public String photoFileName;
	public Date birthday;
	public String aboutMe;
    public boolean isAdmin;
    public boolean isTeacher;
    public boolean isActive;
    
	@OneToMany(mappedBy="user", cascade=CascadeType.ALL)
	public Set<MyCourse> myCourses;
	@OneToMany(mappedBy="user", cascade=CascadeType.ALL)
	public Set<Course> courses;
	@OneToMany(mappedBy="user", cascade=CascadeType.ALL)
	public Set<Question> questions;
	@OneToMany(mappedBy="user", cascade=CascadeType.ALL)
	public Set<Answer> answers;
	@OneToMany(mappedBy="user", cascade=CascadeType.ALL)
	public Set<Comment> comments;
	@OneToMany(mappedBy="user", cascade=CascadeType.ALL)
	public Set<UserAssignment> userAssignments;
	@OneToMany(mappedBy="user", cascade=CascadeType.ALL)
	public Set<UserHomework> userHomeworks;
	
	public User(String username, String pwd, String email, String firstName,
			String lastName, String gender, String location, Blob avatar,
			Date birthday, String aboutMe) {		
		this.username = username;
		this.pwd = pwd;
		this.email = email;
		this.firstName = firstName;
		this.lastName = lastName;
		this.gender = gender;
		this.location = location;
		this.avatar = avatar;
		this.birthday = birthday;
		this.aboutMe = aboutMe;
		this.isActive = true;
	}

	public User(String email, String pwd,Blob avatar) {
		this.avatar = avatar;
		this.email = email;
		this.pwd = pwd; 
	}
	public User(String email, String pwd) {
		this.email = email;
		this.pwd = pwd; 
	}
	public String toString() {
	    return email;
	}
	public static User connect(String email, String pwd) {
		System.out.println("111111111111111111"+email+" "+pwd);
        return find("byEmailAndPwd", email, pwd).first();
    }
	public static User getUserByEmail(String email) {
//		System.out.println(email+" 111111111111111email");
		return find("byEmail", email ).first();
	}
	public static List<User> getnotActiveUsersl() {
//		System.out.println(email+" 111111111111111email");
		return User.find("byIsActive",false).fetch();
	}
}
