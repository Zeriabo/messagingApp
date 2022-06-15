package fi.messaging.pojos;

import org.json.JSONObject;

public class UserPojo {
	private String email;
	private String password;

	public UserPojo( String email, String password) throws Exception {
		this.setEmail(email);
		this.setPassword(password);

	}

	public UserPojo() {

	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
   public JSONObject getUserJSON() {
	   
	   JSONObject userJSON = new JSONObject(this.email);
	   
	return userJSON;
	   
   }
}
