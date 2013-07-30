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
@Table(name = "Files")
public class Files extends Model{
	@ManyToOne
	public Lesson lesson;
	@ManyToOne
	public Homework homework;
	public Date date;
	public Blob file;
	public String fileName;
	
	public Files(Date date, File file, Lesson lesson) {
		this.fileName = file.getName();
    	this.date = date;
    	this.lesson = lesson;
		this.file = new Blob();
		try {
			this.file.set(new FileInputStream(file), MimeTypes.getContentType(file.getName()));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}	
	public Files(Date date, File file, Homework homework) {
		this.fileName = file.getName();
    	this.date = date;
    	this.homework = homework;
		this.file = new Blob();
		try {
			this.file.set(new FileInputStream(file), MimeTypes.getContentType(file.getName()));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static List<Files> getLessonFires(Lesson lesson){
		 return Files.find("byLesson", lesson).fetch();		 
	}
}
