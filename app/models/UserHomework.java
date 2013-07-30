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
@Table(name = "UserHomeworks")
public class UserHomework extends Model{
	@ManyToOne
	public Homework homework;
	@ManyToOne
	public User user;
	public Date dateOfAttempt;
	public int score;
	public boolean pass;
	public Blob file;
	public String fileName;
	

	public UserHomework(Homework homework, User user, Date dateOfAttempt,
			int score,boolean pass,File file) {
		this.homework = homework;
		this.user = user;
		this.dateOfAttempt = dateOfAttempt;
		this.score = score;
		this.pass = pass;
		this.fileName = file.getName();
    	this.file = new Blob();
		try {
			this.file.set(new FileInputStream(file), MimeTypes.getContentType(file.getName()));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static List<UserHomework> getUserHomeworks(Homework homework){
		return Homework.find("select h from UserHomework h where h.homework = :homework order by dateOfAttempt").setParameter("homework", homework).fetch();		 
	}
}
