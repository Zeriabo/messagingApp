package fi.messaging.pojos;


public class SignInfo {


	private String email;
	private String password;
	private String token;

	
   public SignInfo()
   {
	   
   }
	public SignInfo(String email, String token, String password)
	{
		this.email=email;
		this.token=token;
		this.password=password;
		
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
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
}
