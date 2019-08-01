import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

public class UDPAcceptingServer implements Runnable{
	protected int serverPort;
	protected ServerSocket serverSocket = null;
	protected boolean isStopped = false;
	protected Thread runningThread = null;
	int threadCounter = 0;
	Thread serverThread;
	long timeOutInMilliSeconds;
	ArrayList<User> userList;
	Writer writer;
	
	public UDPAcceptingServer(int serverPort, long timeOutInMilliSeconds,  ArrayList<User> userList, Writer writer){
		this.writer = writer;
		this.serverPort = serverPort;
		this.timeOutInMilliSeconds = timeOutInMilliSeconds;
		this.userList = userList;
	}
	
	public void run(){		
		while(!getIsStopped()){
			Socket clientSocket=null;
			
			try{
				clientSocket=this.serverSocket.accept(); //accept multiple clients, when a client wants to learn available port on the registry server for udp hello messaging
				System.out.println("New UDP client for port is accepted!");
			}
			catch(IOException e){
				if(getIsStopped()){
					System.out.println("Server stopped");
					return;
				}
				throw new RuntimeException("Error accepting client connection",e);
			}
			Thread thread = null;
			try {
				thread = new Thread(new UDPPortFinder(clientSocket, timeOutInMilliSeconds, userList, writer)); // each clietn is served in different UDPPortFinder threads
			} catch (UnsupportedEncodingException | FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			thread.start();
		}
	}

	public void startServer(UDPAcceptingServer server){ //create and start UDPAcceptingServer, which happens when the start button in the GUI server clicked
		try{
			this.serverSocket = new ServerSocket(this.serverPort);
			serverThread = new Thread(server);
			serverThread.start();
			System.out.println("Server has started!");
		}
		catch(IOException e){
			throw new RuntimeException("Cannot open port " + serverPort, e);
		}
	}
	
	private boolean getIsStopped() {
		// TODO Auto-generated method stub
		return this.isStopped;
	}
	
	private void setIsStopped(boolean status) {
		// TODO Auto-generated method stub
		this.isStopped = status;
	}
	
	public void stop(){
		setIsStopped(true);
	}

}
