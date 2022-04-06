package fi.messaging.pojos;

public class ReceivePojo {
	private String email;

	public ReceivePojo(String email) throws Exception {
		this.setEmail(email);

	}

	public ReceivePojo() {

	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

}
