package fi.messaging.pojos;


import java.util.Date;
import java.util.List;


public class Email {

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getMessagebody() {
		return messagebody;
	}

	public void setMessagebody(String messagebody) {
		this.messagebody = messagebody;
	}

	public Date getDatetime() {
		return datetime;
	}

	public void setDatetime(Date datetime)  {
	this.datetime=datetime;
	}

	public int getReceiptsnbr() {
		return receiptsnbr;
	}

	public void setReceiptsnbr(int receiptsnbr) {
		this.receiptsnbr = receiptsnbr;
	}

	public List<String> getReceivers() {
		return receivers;
	}

	public void setReceivers(List<String> receivers) {
		this.receivers = receivers;
	}

	public Email(int id, String token, String title, String messagebody, Date datetime, int receiptnbr,
			List<String> receivers) {
		this.id = id;
		this.token = token;
		this.title = title;
		this.messagebody = messagebody;
		this.datetime = datetime;
		this.receiptsnbr = receiptnbr;
		this.receivers = receivers;

	}

	public Email() {

	}

	private int id;
	private String token;
	private String title;
	private String messagebody;
	private Date datetime;
	private int receiptsnbr;
	private List<String> receivers;

}
