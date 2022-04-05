package fi.invian.codingassignment.pojos;


import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Response {

	private boolean status;
	private MessagesPojo message;
	private int code;
	private String errorMessage="";
	
	public Response() {
		
	}
    public Response(int code,boolean status,MessagesPojo messages) {
		this.status=status;
		this.message=messages;
		this.code=code;
	}
    
	public boolean isStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

	public MessagesPojo getMessages() {
		return message;
	}

	public void setMessages(MessagesPojo message) {
		this.message = message;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
}
