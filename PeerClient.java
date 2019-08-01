import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class PeerClient extends Thread {
	
	private int portTo;
	private String IP;
	boolean isAccepted = false;
	private Socket socket = null;
	ObjectOutputStream out = null;
	ObjectInputStream inp = null;
	private boolean isStopped=false;
	boolean isThereAMessageToSend = false;
	ArrayList<Message> msgQueue = new ArrayList<Message>();
	String [] colors = {"#FF82AB","#AB82FF","#5CACEE","#7CCD7C","#FFF68F","#FF9912","#CD3333","#C67171","#C5C1AA","#388E8E"};
	boolean isGetMessageActive;
	Stage chatStage;
	
	Writer writer = null;
	
	public PeerClient(String IP, int portTo, Writer writer) throws UnsupportedEncodingException, FileNotFoundException {
		this.writer = writer;
		this.portTo = portTo;
		this.IP = IP;
	}
	
	public void run() {
		try {
			socket = new Socket(IP, portTo); //peeer client is created with portTo and target IP
			System.out.println("Peer Client socket is opened with IP:" + IP + " port:" +portTo);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		while(!isStopped) {
			try{
				Thread.sleep(1000);
				if(msgQueue.size() >= 1) { //if queue has any messages, send one and delete one
					sendMessage(msgQueue.get(0));
					deleteFirstMessageFromQueue();
				}
			}
			catch(Exception e){
				break;
			}
			if(isGetMessageActive()) {
				getMessage();
				setGetMessage(false);
			}
		}
	}

	public synchronized void addNewMessageToQueue(Message msg){
		msgQueue.add(msg);
	}
	
	public synchronized void deleteFirstMessageFromQueue(){
		if(msgQueue.size() >= 1) { 
			msgQueue.remove(0);
		}
	}
	
	public synchronized boolean isGetMessageActive(){
		return isGetMessageActive;
	}
	
	public synchronized void setGetMessage(boolean status){
		isGetMessageActive = status;
	}
	
	
	public Message getMessage(){ //get message from peer
		Message msg = null;
		try {
			inp = new ObjectInputStream(socket.getInputStream());
			msg = (Message) inp.readObject();
			synchronized(writer) {
				writer.write(System.currentTimeMillis() + " PeerClient message come to client is " + msg.printMessage());
				writer.write("\n");
				writer.flush();
			}
			System.out.println("Message come to the client is "+ msg.printMessage());
			if(msg.getType() == MessageType.Type.ok) {
				//setPeerClientText("Ok message arrived\n");
				System.out.println("Ok message is arrived");
				showChatStage(); //if requested peer accepted the chat request show chat screen
				//showChatServerWindow();
			}
			else if(msg.getType() == MessageType.Type.reject) {
				//setPeerClientText("Reject message arrived\n");
				System.out.println("Reject message is arrived");
			}
			else if(msg.getType() == MessageType.Type.busy) {
				//setPeerClientText("Busy message arrived\n");
				System.out.println("Busy message is arrived");
			}
			else if(msg.getType() == MessageType.Type.chat) {
				//setPeerClientText("Busy message arrived\n");
				System.out.println("Chat message is arrived username:" + msg.getUsername());
				//writeMessageToServerWindow(msg);
			}
		} catch (IOException e) {
			return msg;
		} catch (ClassNotFoundException e) {
			return msg;
		}
		
		return msg;
	}
	
	public Socket getSocket(){
		return this.socket;
	}
	
	public void sendMessage(Message msg) { //send message to peer
		try {
			out = new ObjectOutputStream(socket.getOutputStream());
			out.writeObject(msg);
			out.flush();
			synchronized(writer) { //log
				writer.write(System.currentTimeMillis() + " PeerClient message sent to server is " + msg.printMessage());
				writer.write("\n");
				writer.flush();
			}
			System.out.println("Message sent to the server is "+ msg.printMessage());
		} catch (IOException e) {
			return;
		}
	}
	
	public synchronized void setChatStage(Stage chatStage){
		this.chatStage = chatStage;
	}
	
	public void close() {
		System.out.println("PeerClient is being closed now...");
		try {
			if(out!=null) {
				out.close();
			}
			if(inp != null) {
				inp.close();
			}
			if(socket != null) {
				this.socket.close();
				this.isStopped=true;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public synchronized void showChatStage(){
		Platform.runLater(new Runnable() {
			@Override 
		    public void run() {
				chatStage.show();
		    }
		});
	}
}
