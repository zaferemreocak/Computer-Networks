import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class PeerServer extends Thread{
	private int portFrom;
	private ServerSocket serverSocket = null;
	boolean isAccepted = false;
	private Socket socket;
	private boolean isStopped=false;
	ObjectOutputStream out = null;
	ObjectInputStream inp = null;
	Text peerServerText, peerClientText;
	List<Label> messages;
	int index = 0;
	VBox chatBox;
	String peerTargetIP;
	PeerClient peerClient;
	int colorIndex;
	Stage peerServerStage;
	Stage chatStage;
	Label x;
	int i = 0;
	int messageIndex;
	String username;
	Writer writer = null;
	
	String [] colors = {"#FF82AB","#AB82FF","#5CACEE","#7CCD7C","#FFF68F","#FF9912","#CD3333","#C67171","#C5C1AA","#388E8E"};
	
	public PeerServer(int portFrom, Writer writer, Stage peerServerStage, List<Label> messages,VBox chatBox, int messageIndex) throws UnsupportedEncodingException, FileNotFoundException{
		this.messages = messages;
		this.chatBox = chatBox;
		this.messageIndex = messageIndex;
		this.writer = writer;
		this.portFrom = portFrom;
		this.peerServerStage = peerServerStage;
	}
	
	public void run() {
		try {
			serverSocket = new ServerSocket(portFrom); //create a server socket for listening peer's chat messages
			System.out.println("Peer Server socket is opened");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			socket = this.serverSocket.accept();
			System.out.println("Peer Client socket is accepted");
			
		} catch (IOException e) {
			
		}
		
		while(!isStopped) {
			try {
				Message msg = getMessage();
				if(msg.getType() == MessageType.Type.chatRequest) { //if peer sends chatRequest
					//setPeerServerText(msg.username + " wants to  chat with you.What do you want to do?\n");
					System.out.println("Chat request is received. What do you want to do?\n");	
					showPeerServerStage(); //show the screen with ok, reject, busy
				}
				else if(msg.getType() == MessageType.Type.chat) { //if chat message arrived
					//setPeerClientText("Busy message arrived\n");
					username = msg.getUsername();
					String messageText = msg.getPassword();
					System.out.println("Chat message is arrived Message:" + messageText);
					System.out.println("Server will update the chat");
					updateChatWithNewMessage(messageText); //peerServer updates the chat when chat message arrives
					
				}
			}
			catch(Exception e) {
				break;
			}
		}		
	}
	
	public synchronized void setPeerServerStage(Stage peerServerStage, Text peerServerText){
		this.peerServerStage = peerServerStage;
		this.peerServerText = peerServerText;
	}
	
	public synchronized void setChatStage(Stage chatStage){
		this.chatStage = chatStage;
	}
	
	public synchronized void setX(Label x){
		this.x = x;
	}
	
	public synchronized void showPeerServerStage(){
		Platform.runLater(new Runnable() {
			@Override 
		    public void run() {
				peerServerStage.show();
		    }
		});
	}
	
	public synchronized void writeX(String text){
		Platform.runLater(new Runnable() {
			@Override 
		    public void run() {
				x.setText(text);
		    }
		});
	}
	
	public synchronized void updateChatWithNewMessage(String messageText){
		Platform.runLater(new Runnable() {
			@Override 
		    public void run() {
				messages.add(new Label(username + ": " + messageText)); // new message to the chat screen
		        chatBox.getChildren().add(messages.get(messageIndex));
		        messageIndex = messageIndex + 1;
		    }
		});
	}
	
	public synchronized void setChatInstances(List<Label> messages,VBox chatBox){
		this.messages = messages;
		this.chatBox = chatBox;
	}
	
	public synchronized void updateMessageIndex(int messageIndex){
		this.messageIndex = messageIndex;
	}
	
	public synchronized void updateMessages(List<Label> messages){
		this.messages = messages;
	}
	
	public synchronized int getMessageIndex(){
		return this.messageIndex;
	}
	
	public synchronized List<Label> getMessages(){
		return this.messages;
	}
	
	public synchronized VBox getChatBox(){
		return this.chatBox;
	}
	
	public synchronized void updateChatBox(VBox chatBox){
		this.chatBox = chatBox;
	}
	
	/*public synchronized void showChatServerWindow(){
		Platform.runLater(new Runnable() {
			@Override 
		    public void run() {
				Pane root = new Pane();
				Scene scene;
				
				final Button send = new Button("Send");
				chatBox = new VBox(5);
				messages = new ArrayList<>();
				ScrollPane container = new ScrollPane();
				container.setPrefSize(216, 400);
			    container.setContent(chatBox); 

			    chatBox.getStyleClass().add("chatbox");
				
				
			    root.getStylesheets().add(getClass().getResource("Style.css").toExternalForm());
			    root.getChildren().addAll(container,send);
			    scene = new Scene(root,400,600);
			    peerServerStage.setScene(scene);
			    peerServerStage.show();
		  		System.out.println("chat Server window showed!");
		    }
		});
	}
	
	public synchronized void writeMessageToServerWindow(Message msg){
		messages.add(new Label(msg.getUsername()));

        if(index%2==0){

            messages.get(index).setAlignment(Pos.CENTER_LEFT);
            System.out.println("1");

        }else{

            messages.get(index).setAlignment(Pos.CENTER_RIGHT);
            System.out.println("2");

        }


        chatBox.getChildren().add(messages.get(index));
        index = index +1;
	}
	*/
	public synchronized void setPeerServerText(String text){
		Platform.runLater(new Runnable() {
			@Override 
		    public void run() {
				peerServerText.setText(text);
		    }
		});
	}
	
	/*public synchronized void setPeerClientText(String text){
		Platform.runLater(new Runnable() {
			@Override 
		    public void run() {
				String previousText = peerClientText.getText();
				peerClientText.setText(previousText + text);
		    }
		});
	}*/
	
	public Socket getSocket(){
		return this.socket;
	}
	
	public Message getMessage(){
		Message msg = null;
		try {
			inp = new ObjectInputStream(socket.getInputStream());
			msg = (Message) inp.readObject();
			synchronized(writer) {
				writer.write(System.currentTimeMillis() + " PeerServer message come to server is " + msg.printMessage());
				writer.write("\n");
				writer.flush();
			}
			System.out.println("Message come to the server is "+ msg.printMessage());
		} catch (IOException e) {
			return msg;
		} catch (ClassNotFoundException e) {
			return msg;
		}
		
		return msg;
	}
	
	public void sendMessage(Message msg){
		try {
			out = new ObjectOutputStream(socket.getOutputStream());
			out.writeObject(msg);
			out.flush();
			synchronized(writer) {
				writer.write(System.currentTimeMillis() + " PeerServer message sent to client is " + msg.printMessage());
				writer.write("\n");
				writer.flush();
			}
			System.out.println("Message sent to the client is "+ msg.printMessage());
		} catch (IOException e) {
			return;
		}
	}
	
	public void close() {
		System.out.println("PeerServer is being closed now...");
		try {
			if(out!=null) {
				out.close();
			}
			if(inp != null) {
				inp.close();
			}
			if(serverSocket != null && socket != null) {
				this.serverSocket.close();
				this.socket.close();
				this.isStopped=true;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

	
	public synchronized void sendMessage(PeerClient peerClient, Message msg) {		
		peerClient.addNewMessageToQueue(msg);
	}
	
	/*public void showChatWindow(){
		Pane root = new Pane();
		Scene scene;

		final Button send = new Button("Send");
		final VBox chatBox = new VBox(5);
		List<Label> messages = new ArrayList<>();
		ScrollPane container = new ScrollPane();
		container.setPrefSize(216, 400);
	    container.setContent(chatBox); 

	    chatBox.getStyleClass().add("chatbox");
		
	    TextField msgTextField = new TextField();
		
	    root.getStylesheets().add(getClass().getResource("Style.css").toExternalForm());
	    root.getChildren().addAll(container,send,msgTextField);
	    scene = new Scene(root,400,600);
	    peerClientStage.setScene(scene);
	    peerClientStage.show();
	    
	    send.setOnAction(evt->{
	        messages.add(new Label(msgTextField.getText()));
	        peerClient.sendMessage(new Message(MessageType.Type.chat, msgTextField.getText()));

	        if(index%2==0){
	            messages.get(index).setAlignment(Pos.CENTER_LEFT);
	            System.out.println("1");

	        }else{

	            messages.get(index).setAlignment(Pos.CENTER_RIGHT);
	            System.out.println("2");

	        }

	        chatBox.getChildren().add(messages.get(index));
	        index = index +1;

	    });
	    
  		System.out.println("chat Client window showed!");
  		
  		peerServerStage.close();
	}*/
}
