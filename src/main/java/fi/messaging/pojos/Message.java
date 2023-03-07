	package fi.messaging.pojos;


import java.util.Date;

public class Message {

	private int id;
	private String title;
	private String messagebody;
	private Date datetime;
	private int idUser; 
    private String sender;
    
	public Message(int id, String title, String messagebody, Date datetime, int idUser,String sender) {

		this.id = id;
		this.title = title;
		this.messagebody = messagebody;
		this.datetime = datetime;
		this.idUser = idUser;
		this.sender =sender;
	}

	public Message() {
	}

	public int getIdUser() {
		return idUser;
	}

	public void setIdUser(int idUser) {
		this.idUser = idUser;
	}

	public String getDatetime() {
		return datetime.toString();
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
	
	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}
	public String toString() {
		
	String s =(this.id+ " "+this.idUser+ " "+this.messagebody+ " "+this.title+ " ");
	return s;
	   
	}

	
}
