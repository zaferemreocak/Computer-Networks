import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class UDPServer implements Runnable {
	DatagramSocket serverSocket;
	byte[] receiveData = new byte[1024];
	boolean isClosed = false;
	long timeOutInMilliSeconds;
	ArrayList<User> userList;
	String username;
	Writer writer = null;
	
	public UDPServer(int port, long timeOutInMilliSeconds, ArrayList<User> userList, Writer writer) throws UnsupportedEncodingException, FileNotFoundException{
		this.writer = writer; 
		try {
			serverSocket  = new DatagramSocket(port);
			this.timeOutInMilliSeconds = timeOutInMilliSeconds;
			this.userList = userList;
			
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("UDP server port number local:"+ serverSocket.getLocalPort() + " to:" + serverSocket.getPort());
	}
	
	@Override
	public void run(){
		long start, elapsedTime; 
		while(true){
			if(isClosed) {
				break;
			}
			
			DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length); //receives hello messages
			try {
				start = System.nanoTime();
				serverSocket.receive(receivePacket);
				String receivedPacketString = new String(
						receivePacket.getData(),
						receivePacket.getOffset(),
						receivePacket.getLength(),
					    StandardCharsets.UTF_8 // or some other charset
					);
				System.out.println("Packet received " +receivedPacketString);
				synchronized(writer) {
					writer.write(System.currentTimeMillis() + " UDPPServer message come to server is " + receivedPacketString);
					writer.write("\n");
					writer.flush();
				}
				
				String[] tokens = receivedPacketString.split(" "); // message "hello from username" splitted
				String username = tokens[2]; 
				System.out.println("username " + username);
				
				elapsedTime = System.nanoTime() - start;
				if(elapsedTime > (timeOutInMilliSeconds * 1000000)) { //if elapsed time is higher than some threshold, timeout occurs and registry server makes this user offline
					System.out.println("Time out !");
					updateUserListWithOffline();
					serverSocket.close();
					break;
				}
				
			} catch (IOException e) {
				if(isClosed) {
					break;
				}
			}
			String receivedString = new String(receivePacket.getData());
			System.out.println("Message from client: " + receivedString);
		}
	}
	
	public void close(){
		isClosed = true;
		serverSocket.close();
	}
	
	public synchronized void updateUserListWithOffline(){ //find username and change its isOnline property to false
		for(int i = 0; i < userList.size(); i++) {
			System.out.println("size " + userList.size());
			if((userList.get(i).getUsername()).equals(username)) {
				userList.get(i).setIsOnline(false); //offline
				break;
			}
		}
	}
}