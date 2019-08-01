
public class User {
	private String IP, username, password;
	private boolean isOnline;
	
	public User(String IP, String username, String password, boolean isOnline) { //used for saving each client's data
		this.IP = IP;
		this.username = username;
		this.password = password;
		this.isOnline = isOnline;
	}
	
	public String getIP(){
		return IP;
	}
	
	public String getUsername(){
		return username;
	}
	
	public String getPassword(){
		return password;
	}
	
	public boolean getIsOnline(){
		return isOnline;
	}
	
	public void setIP(String IP){
		this.IP = IP;
	}
	
	public void setIsOnline(boolean isOnline){
		this.isOnline = isOnline;
	}
	public String printUser(){
		return this.IP + ", " + this.username + ", " + this.password + ", " + ((this.isOnline) ? "online" : "offline");
	}
}
