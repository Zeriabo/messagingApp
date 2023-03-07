package fi.messaging.pojos;


public class SendPojo {
	private Message message;
	private Sender sender;
	private Receiver receiver;

	public SendPojo(Message message2, Sender sender, Receiver receiver) throws Exception {
		this.message = message2;
		this.sender = sender;
		this.receiver = receiver;

	}

	public Message getMessage() {
		return message;
	}

	public void setMessage(Message message) {
		this.message = message;
	}

	public Sender getSender() {
		return sender;
	}

	public void setSender(Sender sender) {
		this.sender = sender;
	}

	public Receiver getReceiver() {
		return this.receiver;
	}

	public void setReceiver(Receiver receiver) {

		this.receiver = receiver;
	}

}
