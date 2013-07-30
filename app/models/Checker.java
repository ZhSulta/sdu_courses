package models;

import java.util.*;

import javax.persistence.*;


import play.data.validation.Email;
import play.data.validation.MaxSize;
import play.data.validation.MinSize;
import play.data.validation.Required;
import play.db.jpa.*;

@Entity
@Table(name = "Checker")
public class Checker extends Model{
	
	@ManyToOne
	public Homework homework;
	public String input;
	public String output;
	public String lang;
	
	
	public Checker(Homework homework, String input, String output, String lang) {
		super();
		this.homework = homework;
		this.input = input;
		this.output = output;
		this.lang = lang;
	}

	public static List<Checker> getHomeworkChecker(Homework homework){
		 return Checker.find("byHomework", homework).fetch();		
	}
}
