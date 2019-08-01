import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.DataInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import javafx.scene.control.Button;
import javafx.stage.Stage;

public class Client{
	private static Socket socket = null;
	private String username, password;
	int port;
	ObjectOutputStream out;
	ObjectInputStream inp = null;
	boolean isSocketConnected = false;
	Message msg = null;
	String serverIP;
	Writer writer = null;
	
	public Client(String serverIP, String username, String password, int port, Writer writer) throws UnsupportedEncodingException, FileNotFoundException {
		this.writer = writer;
		this.serverIP = serverIP;
		this.username = username;
		this.password = password;
		this.port = port;
		
			try { 
				socket = new Socket(serverIP,port); //client is created to connect to server in serverIP with port
				out = new ObjectOutputStream(socket.getOutputStream());
				inp = new ObjectInputStream(socket.getInputStream());
				isSocketConnected = socket.isConnected();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				//System.out.println("Client socket can not be opened");
			}
	}
	
	public boolean isSocketConnected(){
		return this.isSocketConnected;
	}
	
	public void sendMessage(MessageType.Type type, String username, String password) { //send Message from client to server
		try{
			Message msg = new Message(type,username,password);
			out.writeObject(msg);
			out.flush();
			synchronized(writer){ //log
				writer.write(System.currentTimeMillis() + " Client message sent to server is " + msg.printMessage() + "\n");
				writer.flush();
			}
			System.out.println("Message sent to the server is "+ msg.printMessage());
		}
		catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	public void sendMessage(MessageType.Type type, String username) { //send Message from client to server
		try{
			Message msg = new Message(type,username);
			out.writeObject(msg);
			out.flush();
			synchronized(writer){ //log
				writer.write(System.currentTimeMillis() + " Client message sent to server is " + msg.printMessage() + "\n");
				writer.flush();
			}
			System.out.println("Message sent to the server is "+ msg.printMessage());
		}
		catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	

	public Message getMessage() { //send Message to server
		Message msg = null;
		try {
			msg = (Message) inp.readObject();
			synchronized(writer){ //log
				writer.write(System.currentTimeMillis() + " Client message come to client is " + msg.printMessage() +"\n");
				writer.write("\n");
				writer.flush();
			}
			
			//System.out.println("type:" + msg.getType() + " username:" + msg.getUsername() + " password:"+msg.getPassword());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return msg;
	}
	
	
	public void closeSocket(){ //close input output streams and client socket
		try {
			if(out!=null) {
				out.close();
			}
			if(inp != null) {
				inp.close();
			}
			if(socket != null) {
				socket.close();
				socket = null;
				isSocketConnected = false;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
}
