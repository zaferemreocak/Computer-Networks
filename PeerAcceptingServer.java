import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Writer;

public class PeerAcceptingServer implements Runnable{
	protected int serverPort;
	protected ServerSocket serverSocket = null;
	protected boolean isStopped = false;
	protected Thread runningThread = null;
	int threadCounter = 0;
	Thread serverThread;
	long timeOutInMilliSeconds;
	ArrayList<User> userList;
	int peerPort;
	int peerServerLocalPort, peerServerTargetPort;
	Writer writer = null;
	Message peerMessage;
	String peerTargetIP;
	PeerClient peerClient;
	PeerServer peerServer;
	Stage peerServerStage;
	List<Label> messages;
	int messageIndex = 0;
	VBox chatBox;
	
	public PeerAcceptingServer(int serverPort, Writer writer, PeerClient peerClient, PeerServer peerServer, Stage peerServerStage,List<Label> messages,VBox chatBox, int messageIndex){
		this.peerClient = peerClient;
		this.peerServer = peerServer;
		this.writer = writer;
		this.serverPort = serverPort;
		this.peerServerStage = peerServerStage;
		this.messages = messages;
		this.chatBox = chatBox;
		this.messageIndex = messageIndex;
	}
	
	public void run(){		
		while(!getIsStopped()){
			Socket clientSocket=null;
			
			try{
				clientSocket=this.serverSocket.accept(); //if new peer wants chat, serverSocket accepts it
				System.out.println("New Peer client for port is accepted!");
				ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
				ObjectInputStream inp = new ObjectInputStream(clientSocket.getInputStream());
				
				
				peerMessage = (Message)inp.readObject(); //peerMessage has available port of the requested peer
				synchronized(writer) {
        			writer.write(System.currentTimeMillis() + " PeerAcceptingServer message come to server is " + peerMessage.printMessage());
        			writer.write("\n");
        			writer.flush();
        		}
				peerServerTargetPort = Integer.parseInt(peerMessage.getUsername());
				InetAddress address = clientSocket.getInetAddress();
				peerTargetIP = address.getHostAddress();
				peerClient = new PeerClient(peerTargetIP, peerServerTargetPort ,writer); //create peerClient with the available port of the requested peer
				peerClient.start();
				System.out.println("PeerAcceptingServer peerClient started to communicate with port " + peerServerTargetPort); 	
				
				ServerSocket serverSocket = new ServerSocket(0); //find available port
				peerServerLocalPort = serverSocket.getLocalPort();
				serverSocket.close();
				
				peerServer = new PeerServer(peerServerLocalPort, writer,peerServerStage, messages,chatBox,messageIndex); //create peerServer with available local port
				peerServer.start();
				System.out.println("PeerAcceptingServer peerServer started in port " + peerServerLocalPort);
				
				peerMessage = new Message(MessageType.Type.freePeerPort,peerServerLocalPort+""); //send available local port to the requested peer
				out.writeObject(peerMessage);
				out.flush();    					
        		synchronized(writer) {
        			writer.write(System.currentTimeMillis() + " PeerAcceptingServer message sent to client is " + peerMessage.printMessage());
        			writer.write("\n");
        			writer.flush();
        		}
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(getIsStopped()){
				System.out.println("Server stopped");
				return;
			}
		}
	}
	
	public synchronized int getServerLocalPort(){
		return peerPort;
		
	}
	
	public synchronized PeerServer getPeerServer(){
		return this.peerServer;
	}

	public synchronized PeerClient getPeerClient(){
		return this.peerClient;
	}
	
	public void startServer(PeerAcceptingServer server){ //start server
		try{
			this.serverSocket = new ServerSocket(this.serverPort);
			serverThread = new Thread(server);
			serverThread.start();
			System.out.println("Peer accepting Server has started!");
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
