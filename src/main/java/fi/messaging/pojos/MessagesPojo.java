package fi.messaging.pojos;



import java.util.ArrayList;

public class MessagesPojo {

	private int userid;
	private ArrayList<Message> messages= new ArrayList<Message>();


	public MessagesPojo(int userid, ArrayList<Message>  messages) {

		this.setUserid(userid);
		this.setMessages(messages);
	}


	public MessagesPojo() {
	
	}


	public int getUserid() {
		return userid;
	}


	public void setUserid(int userid) {
		this.userid = userid;
	}


	public ArrayList<Message> getMessages() {
		return messages;
	}


	public void setMessages(ArrayList<Message> messages) {
		this.messages = messages;
	}

	public void addMessage(Message m) {
		
		this.messages.add(m);
	}
	public void printMessages()
	{
		this.messages.forEach((message)->{
			System.out.println(message);
		});
	}

}
