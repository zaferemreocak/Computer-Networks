import java.io.Serializable;

public class Message implements Serializable{
	MessageType.Type type;
	String username = null, password = null;
	boolean isSuccessful = false;
	
	public Message(MessageType.Type type, String username, String password){ //sending sign in/sign up username password
		this.type = type;
		this.username = username;
		this.password = password;
		
	}
	public Message(MessageType.Type type) {
		this.type = type;
	}
	public Message(MessageType.Type type, String username){ //used for chatting
		this.type = type;
		this.username = username;
	}	
	
	public Message(MessageType.Type type, boolean isSuccessful){ //used for sign in/sign out successful/unsuccessful response
		this.type = type;
		this.isSuccessful = isSuccessful;
	}
	
	public MessageType.Type getType(){
		return this.type;
	}
	
	public String getUsername(){
		return this.username;
	}
	
	public String getPassword(){
		return this.password;
	}
	
	public String printMessage(){
		return "Type: " + type.name() + ((username == null) ? "" :" Username:"+username) + ((password == null) ? "" :" Password:"+password) ;
	}
	
	public boolean isSuccessful(){
		return this.isSuccessful;
	}
}
