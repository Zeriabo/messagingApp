package fi.invian.codingassignment.pojos;

import java.util.Date;

public class Message {

	private int id;
	private String title;
	private String messagebody;
	private Date datetime;
	private int idUser;

	public Message(int id, String title, String messagebody, Date datetime, int idUser) {

		this.id = id;
		this.title = title;
		this.messagebody = messagebody;
		this.datetime = datetime;
		this.idUser = idUser;
	}

	public Message() {
		// TODO Auto-generated constructor stub
	}

	public int getIdUser() {
		return idUser;
	}

	public void setIdUser(int idUser) {
		this.idUser = idUser;
	}

	public Date getDatetime() {
		return datetime;
	}

	public void setDatetime(Date datetime) {
		this.datetime = datetime;
	}

	public String getMessagebody() {
		return messagebody;
	}

	public void setMessagebody(String messagebody) {
		this.messagebody = messagebody;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	public String toString() {
		
	String s =(this.id+ " "+this.idUser+ " "+this.messagebody+ " "+this.title+ " ");
	return s;
	   
	}
}
