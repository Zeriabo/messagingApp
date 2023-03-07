package fi.messaging.pojos;

import java.util.Date;

public class SignedUser {


	private int idUser;
	private String name;
	private String email;
	private Date dateofbirth;
	private boolean active;
	private String token;
	
   public SignedUser()
   {
	   
   }
	public SignedUser(int idUser,String name, String email,Date dateofbirth,boolean active, String token)
	{
	    this.idUser=idUser;
		this.name=name;
		this.email=email;
		this.dateofbirth=dateofbirth;
		this.active=active;
		this.token=token;
		
	}
	public int getIdUser() {
		return idUser;
	}
	public void setIdUser(int idUser) {
		this.idUser = idUser;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public Date getDateofbirth() {
		return dateofbirth;
	}
	public void setDateofbirth(Date dateofbirth) {
		this.dateofbirth = dateofbirth;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}
	/**
	 * @return the token
	 */
	public String getToken() {
		return token;
	}
	/**
	 * @param token the token to set
	 */
	public void setToken(String token) {
		this.token = token;
	}
	
}
