package fi.messaging.pojos;

public class User {

	private int idUser;
	private String name;
	private String email;
	
	public User(int idUser,String name, String email)
	{
		this.idUser=idUser;
		this.name=name;
		this.email=email;
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
	
}
