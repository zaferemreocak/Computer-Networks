import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.InetAddress;
import java.net.Socket;
import java.io.ObjectInputStream;

import java.util.ArrayList;

public class WorkerRunnable implements Runnable{
	protected Socket clientSocket = null;
	//protected String serverText = null;
	protected String username = null;
	protected String password = null;
	ArrayList<User> userList;
	Message msg = null;
	boolean online = true, offline = false;
	long timeOutInMilliSeconds = 2000; //20000
	Writer writer = null;
	
	public WorkerRunnable(Socket clientSocket, ArrayList<User> userList, Writer writer) throws UnsupportedEncodingException, FileNotFoundException{ //serves to each client
		this.writer = writer;
		this.clientSocket = clientSocket;
		this.userList = userList;
	}
	
	public void run() {
		ObjectInputStream inp = null;
		ObjectOutputStream out = null;
		
		try {
			inp = new ObjectInputStream(clientSocket.getInputStream());
			out = new ObjectOutputStream(clientSocket.getOutputStream());
		
			while(true) {
				try {
					msg = (Message) inp.readObject(); //get message from client
					synchronized(writer) {
						writer.write(System.currentTimeMillis() + " WorkerRunnable message come to server is " + msg.printMessage());
						writer.write("\n");
						writer.flush();
					}
					System.out.println("From client to server :" + msg.printMessage());
				} catch (ClassNotFoundException | IOException e2) {
					break;
				}
				
				if(msg.getType() == MessageType.Type.signIn){ //if received message is sign In
					if(isUserExistInUserList(msg.getUsername(), msg.getPassword())) { //check both username and password and if exists, successful
						//isOnline = 1
						//IP = address.get
						//show search and logout buttons, search textbox screen
						try {
							updateIsOnline(msg.getUsername(), online); //make user online
							InetAddress address = clientSocket.getInetAddress();
							String IP = address.getHostAddress();
							updateIP(msg.getUsername(), IP); //update userList with new IP
							out.writeObject(new Message(MessageType.Type.signIn,true)); //send the message which says sign is successful
							out.flush();
							synchronized(writer) {
								writer.write(System.currentTimeMillis() + " WorkerRunnable message sent to client is " + msg.printMessage());
								writer.write("\n");
								writer.flush();
							}
						} catch (IOException e) {
							break;
						}
					}
					else { //check both username and password and if not exist, unsuccessful
						//you haven't sign up before, please sign up first
						//show signIn and signUp screen
						try {
							out.writeObject(new Message(MessageType.Type.signIn,false));
							out.flush();
							synchronized(writer) {
								writer.write(System.currentTimeMillis() + " WorkerRunnable message sent to client is " + msg.printMessage());
								writer.write("\n");
								writer.flush();
							}
						} catch (IOException e) {
							break;
						}
					}
				}
				else if(msg.getType() == MessageType.Type.signUp){
					if(isUsernameExistInUserList(msg.getUsername())) { //check only username
						//please select another username
						//show signIn and signUp screen
						try {
							out.writeObject(new Message(MessageType.Type.signUp,false));
							out.flush();
							synchronized(writer) {
								writer.write(System.currentTimeMillis() + " WorkerRunnable message sent to client is " + msg.printMessage());
								writer.write("\n");
								writer.flush();
							}
						} catch (IOException e) {
							break;
						}
					}
					else { //sign up successful
						//add it to the userList
						//show signIn and signUp screen
						InetAddress address = clientSocket.getInetAddress();
						String IP = address.getHostAddress();
						addNewUserToTheUserList(IP, msg.getUsername(), msg.getPassword());
						try {
							out.writeObject(new Message(MessageType.Type.signUp,true));
							out.flush();
							synchronized(writer) {
								writer.write(System.currentTimeMillis() + " WorkerRunnable message sent to client is " + msg.printMessage());
								writer.write("\n");
								writer.flush();
							}
						} catch (IOException e) {
							break;
						}
					}
				}		
				else if(msg.getType() == MessageType.Type.logOut){ //logout message received
					updateIsOnline(msg.getUsername(), offline);			
				}
				else if(msg.getType() == MessageType.Type.search){ //search message received
					String IP = isPeerExistAndOnline(msg.getUsername()); //search the username
					if(IP == null) { //not found
						System.out.println("NOT FOUND");
						try {
							out.writeObject(new Message(MessageType.Type.search,"NOT FOUND"));
							out.flush();
							synchronized(writer) {
								writer.write(System.currentTimeMillis() + " WorkerRunnable message sent to client is " + msg.printMessage());
								writer.write("\n");
								writer.flush();
							}
						} catch (IOException e) {
							break;
						}
					}
					else { //searched username found and online
						System.out.println("FOUND, IP is "+IP);
						try {
							out.writeObject(new Message(MessageType.Type.search,IP));
							out.flush();
							synchronized(writer) {
								writer.write(System.currentTimeMillis() + " WorkerRunnable message sent to client is " + msg.printMessage());
								writer.write("\n");
								writer.flush();
							}
						} catch (IOException e) {
							break;
						}
					}
				}
			}
		}
		catch(Exception e){
			System.out.println("Error while opening input and output stream");
		}
		finally{
			if(inp != null) {
				try {
					inp.close();
				} catch (IOException e) {
					System.out.println("Input Stream can't be closed");
				}
			}
			if(out != null) {
				try {
					out.close();
				} catch (IOException e) {
					System.out.println("Output Stream can't be closed");
				}
			}
		}	
	}
	
	public synchronized boolean isUserExistInUserList(String username, String password){ //userList'i kullanmadan önce lockla
		boolean isExist = false;
		for(int i = 0; i < userList.size(); i++) {
			if((userList.get(i).getUsername()).equals(username) && (userList.get(i).getPassword()).equals(password)) {
				isExist = true;
				break;
			}
		}
		
		return isExist;
		
	} 
	
	public synchronized boolean isUsernameExistInUserList(String username){ //userList'i kullanmadan önce lockla
		boolean isExist = false;
		for(int i = 0; i < userList.size(); i++) {
			if((userList.get(i).getUsername()).equals(username)) {
				isExist = true;
				break;
			}
		}
		
		return isExist;
	}
	
	public synchronized void updateIsOnline(String username, boolean isOnline){
		for(int i = 0; i < userList.size(); i++) {
			if((userList.get(i).getUsername()).equals(username)) {
				userList.get(i).setIsOnline(isOnline);
				break;
			}
		}
	}
	
	public synchronized void updateIP(String username, String IP){
		for(int i = 0; i < userList.size(); i++) {
			if((userList.get(i).getUsername()).equals(username)) {
				userList.get(i).setIP(IP);
				break;
			}
		}
	}
	
	public synchronized void addNewUserToTheUserList(String IP, String username, String password){
		userList.add(new User(IP, username, password, offline));
	}
	
	public synchronized String isPeerExistAndOnline(String username){
		String IP = null;
		for(int i = 0; i < userList.size(); i++) {
			if((userList.get(i).getUsername()).equals(username) && userList.get(i).getIsOnline() == online) {
				IP = userList.get(i).getIP();
				break;
			}
		}
		return IP;
	}
}