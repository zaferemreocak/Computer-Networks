import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class MainClient extends Application{
	Client client = null;
	int port = 9000;
	String username = null, password = null, searchUsername = null;
	String [] colors = {"#FF82AB","#AB82FF","#5CACEE","#7CCD7C","#FFF68F","#FF9912","#CD3333","#C67171","#C5C1AA","#388E8E"};
	int constantUDPport = 65507; //65507
	InetAddress IPAddress; //IP address of server
	long helloMessageIntervalInMilliSeconds = 1000; //1000
	UDPClient udpClient;
	String serverIP = "127.0.1.1";
	int randomNum;
	int constantPeerport = 50000; 
	List<Label> messagesClient;
	VBox chatBoxClient;
	int messageIndexClient = 0;
	Stage chatStageClient;
	
	Stage peerServerStage;
	Button btnOK, btnReject, btnBusy;
	Stage peerClientStage;
	Text peerClientText = null;
	Text peerServerText = null;
	PeerClient peerClient;
	PeerServer peerServer;
	int peerServerLocalPort, peerServerTargetPort;	//for first client enter 10000, 40000. for the second enter 40000, 10000
	String peerTargetIP = "localhost";
	String peerLocalIP = "localhost";
	Message msg;
	int messageIndex = 0;
	Stage chatStage;
	//Label x;
	PeerAcceptingServer peerAcceptingServer;
	
	
	List<Label> messages;
	VBox chatBox;
	Writer writer = null;
	
	public static void main(String[] args) {
		launch(args); //show GUI
	}
	
	@Override
    public void start(Stage primaryStage) throws UnsupportedEncodingException, FileNotFoundException {		//GUI of client
		writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("MainClient.txt"), "utf-8")); //log file of client

		Random r = new Random();
		randomNum = r.nextInt(colors.length); //use random colored backgrounds for clients
		
		GridPane grid = new GridPane();
		grid.setAlignment(Pos.CENTER);
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(25, 25, 25, 25));
		grid.setStyle("-fx-background-color: " + colors[randomNum]); //use random colored backgrounds for clients
		
		Text scenetitle = new Text("Welcome Client!");
		scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
		grid.add(scenetitle, 0, 0, 2, 1);

		Label userName = new Label("Username:");
		grid.add(userName, 0, 1);

		TextField userTextField = new TextField();
		grid.add(userTextField, 1, 1);

		Label pw = new Label("Password:");
		grid.add(pw, 0, 2);

		PasswordField pwBox = new PasswordField();
		grid.add(pwBox, 1, 2);

		Button btnSignIn = new Button("Sign In");
		HBox hbBtnSignIn = new HBox(10);
		hbBtnSignIn.setAlignment(Pos.BOTTOM_LEFT);
		hbBtnSignIn.getChildren().add(btnSignIn);
		grid.add(hbBtnSignIn, 1, 3);
		
		Button btnSignUp = new Button("Sign Up");
		HBox hbBtnSignUp = new HBox(10);
		hbBtnSignUp.setAlignment(Pos.CENTER);
		hbBtnSignUp.getChildren().add(btnSignUp);
		grid.add(hbBtnSignUp, 0, 3);
		
		final Text actiontarget = new Text();
        grid.add(actiontarget, 1, 6);
        
        Scene scene1 = new Scene(grid, 600, 600);
        
        
        GridPane grid2 = new GridPane();
		grid2.setAlignment(Pos.CENTER);
		grid2.setHgap(10);
		grid2.setVgap(10);
		grid2.setPadding(new Insets(25, 25, 25, 25));
		grid2.setStyle("-fx-background-color: " + colors[randomNum]);
		
		Text scenetitle2 = new Text("Welcome Client!");
		scenetitle2.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
		grid2.add(scenetitle2, 0, 0, 2, 1);

		Label userName2 = new Label("Username To Be Searched:");
		grid2.add(userName2, 0, 1);

		TextField userTextField2 = new TextField();
		grid2.add(userTextField2, 1, 1);

		Button btnSearch = new Button("Search");
		HBox hbBtnSearch = new HBox(10);
		hbBtnSearch.setAlignment(Pos.BOTTOM_LEFT);
		hbBtnSearch.getChildren().add(btnSearch);
		grid2.add(hbBtnSearch, 1, 3);
		
		Button btnLogOut = new Button("Log out");
		HBox hbBtnLogOut = new HBox(10);
		hbBtnLogOut.setAlignment(Pos.BOTTOM_RIGHT);
		hbBtnLogOut.getChildren().add(btnLogOut);
		grid2.add(hbBtnLogOut, 0, 3);
        
		final Text actiontarget2 = new Text();
        grid2.add(actiontarget2, 1, 6);
		
        Scene scene2 = new Scene(grid2, 600, 600);

        
        primaryStage.setScene(scene1);
        primaryStage.show();
        
        
        try {
			IPAddress = InetAddress.getByName("localhost");  //IP address of server
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        
        btnSignIn.setOnAction(new EventHandler<ActionEvent>() { //when sign in buttton clicked
            @Override
            public void handle(ActionEvent e) {
                actiontarget.setFill(Color.FIREBRICK);
                if((userTextField.getText()).equals("") || (pwBox.getText()).equals("")){ //if username and password is empty
                	actiontarget.setText("You haven't enter username or password, please enter!");
                }
                else {  //if username and password are not empty
                	actiontarget.setText("Sign In button pressed\nUsername:" + userTextField.getText() + "\nPassword:"+ pwBox.getText());
                	username = userTextField.getText();
                	password = pwBox.getText();
                	if(client == null) {
                		actiontarget.setText("New Client socket opened");
                    	try {
							client = new Client(serverIP, username,password, port, writer); //create new client if it is not created before
						} catch (UnsupportedEncodingException | FileNotFoundException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
                	}
                    if(client != null && client.isSocketConnected()){ //if client is created and connected to serversocket
                    	client.sendMessage(MessageType.Type.signIn, username, password); //send username and password to the server
                    	Message msg = client.getMessage(); //get message from server
                    	if(msg.isSuccessful()){ //if sign in is successful
                    		primaryStage.setScene(scene2); //show logout and search screen
                    		primaryStage.show();
                    		scenetitle2.setText("Welcome " + username);
                    		actiontarget2.setText("Sign in successful");
                    		Socket socket;
                    		Message udpMessage = null;
							try {
								socket = new Socket(serverIP,constantUDPport); //open TCP client targeting a constant UDP port
								ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
	            				ObjectInputStream inp = new ObjectInputStream(socket.getInputStream());
	                    		udpMessage =(Message) inp.readObject(); //get message from server
	                    		synchronized(writer) { //log
	                    			writer.write(System.currentTimeMillis() + " MainClient message come to client is " + udpMessage.printMessage());
	                    			writer.write("\n");
	                    			writer.flush();
	                    		}
							} catch (IOException | ClassNotFoundException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
                    		
                    		int UDPport = Integer.parseInt(udpMessage.getUsername()); //udp port is came from server
                    		System.out.println("UDP message received " + UDPport);
                    		try {
								udpClient = new UDPClient(IPAddress, UDPport, helloMessageIntervalInMilliSeconds, username, writer); //create udpclient for sending hello message
							} catch (UnsupportedEncodingException | FileNotFoundException e2) {
								// TODO Auto-generated catch block
								e2.printStackTrace();
							}
                    		Thread udpClientThread = new Thread(udpClient); //hello message will be sent in thread
                    		udpClientThread.start();
                    		
                    		createPeerServerWindow(); //create screen with ok, reject and busy buttons
                    		peerAcceptingServer = new PeerAcceptingServer(constantPeerport, writer, peerClient, peerServer, peerServerStage, messages,chatBox,messageIndex);
                    		peerAcceptingServer.startServer(peerAcceptingServer);   
                    		addButtonsToPeerServerWindow();
                    		
                    	}
                    	else {
                    		actiontarget.setText("You haven't sign up before, please sign up first");
                    	}
                    }
                    if(client != null && !client.isSocketConnected()) {
                    	actiontarget.setText("Client socket can not be opened because server is off");
                    }
                }
            }
        });
        
        btnSignUp.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                actiontarget.setFill(Color.FIREBRICK);
                if((userTextField.getText()).equals("") || (pwBox.getText()).equals("")){
                	actiontarget.setText("You haven't enter username or password, please enter!");
                }
                else {
                	actiontarget.setText("Sign Up button pressed\nUsername:" + userTextField.getText() + "\nPassword:"+ pwBox.getText()); 
                	username = userTextField.getText();
                	password = pwBox.getText();
                	if(client == null) {
                		actiontarget.setText("New Client socket opened");
                    	try {
							client = new Client(serverIP, username,password, port, writer);
						} catch (UnsupportedEncodingException | FileNotFoundException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
                	}
                	if(client != null && client.isSocketConnected()){ //send signup message and get response message
                    	client.sendMessage(MessageType.Type.signUp, username, password);
                    	Message msg = client.getMessage();
                    	actiontarget.setText((msg.isSuccessful() == true ? "Your username and password added to the userList" : "This username already exist! Please select another username"));         
                    }
                	if(client != null && !client.isSocketConnected()) {
                    	actiontarget.setText("Client socket can not be opened because server is off");
                    }                
                }
            }
        });
		
        btnLogOut.setOnAction(new EventHandler<ActionEvent>() { //logout button action
            @Override
            public void handle(ActionEvent e) {
            	if(client != null && client.isSocketConnected()) {
            		client.sendMessage(MessageType.Type.logOut, username); //say server that i'm logging out
            		primaryStage.setScene(scene1); //go back to sign in/sign up screen
            		primaryStage.show();
            		scenetitle.setText("Welcome Client!");
            		actiontarget.setText("Log out button pressed\nClient is closed");
            		client.closeSocket(); //close client
            		client = null;
                }
            	if(udpClient != null && !udpClient.clientSocket.isClosed()) {
        			udpClient.close();
        		}
            }
        });
        
        btnSearch.setOnAction(new EventHandler<ActionEvent>() { 
            @Override
            public void handle(ActionEvent e) {
        		searchUsername = userTextField2.getText();
        		client.sendMessage(MessageType.Type.search, searchUsername);
        		Message msg = client.getMessage();
        		if(!(msg.getUsername()).equals("NOT FOUND")) { //searched username is found
        			peerTargetIP = msg.getUsername(); //get IP of searched username
        			Socket socket;
					try {
						socket = new Socket(peerTargetIP,constantPeerport); //open TCP client socket for a constant serversocket
						ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
        				ObjectInputStream inp = new ObjectInputStream(socket.getInputStream());
        				
        				ServerSocket serverSocket;
						try {
							serverSocket = new ServerSocket(0);
							peerServerLocalPort = serverSocket.getLocalPort(); //find available port for peer communication
            				serverSocket.close();
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
        				
        				Message peerMessage = new Message(MessageType.Type.freePeerPort,peerServerLocalPort+"");
        				out.writeObject(peerMessage);
    					out.flush();    					
                		synchronized(writer) {
                			writer.write(System.currentTimeMillis() + " MainClient message sent to client is " + peerMessage.printMessage());
                			writer.write("\n");
                			writer.flush();
                		}
                		peerServer = new PeerServer(peerServerLocalPort, writer, peerServerStage, messages,chatBox,messageIndex); //create peerServer for sending chat messages to peer
    					peerServer.start();
    					System.out.print("peerServer started in port " + peerServerLocalPort);
        				
    					try {
							peerMessage = (Message)inp.readObject();
							synchronized(writer) {
	                			writer.write(System.currentTimeMillis() + " MainClient message come to client is " + peerMessage.printMessage());
	                			writer.write("\n");
	                			writer.flush();
	                		}
							peerServerTargetPort = Integer.parseInt(peerMessage.getUsername()); //received message has available port address of peer
							peerClient = new PeerClient(peerTargetIP, peerServerTargetPort ,writer);
							peerClient.start();
							System.out.print("peerClient started to communicate with port " + peerServerTargetPort);
						} catch (ClassNotFoundException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} 					
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					
        			actiontarget2.setText("Searched user is found with IP: " + peerTargetIP + " and chat request sent");
        			System.out.println("Searched user is found with IP: " + peerTargetIP + " and chat request sent");
        			msg = new Message(MessageType.Type.chatRequest, username);
        			sendMessage(peerClient, msg); //send chatRequest message
        			getMessageFromPeerClient(); //get OK/Reject/Busy message
        			openChatWindow(true);
        			peerClient.setChatStage(chatStage);
        			peerServer.setChatInstances(messages,chatBox);
        			peerServer.updateMessageIndex(messageIndex);
        		}
        		else {
        			actiontarget2.setText("Searched user is not found!");
        		}
            }
        });
        
        
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() { //when screen is closed, close sockets and log file
            @Override
            public void handle(WindowEvent t) {
            	if(client != null && client.isSocketConnected()) {
            		client.closeSocket();
            		client = null;
                } 
            	if(udpClient != null && !udpClient.clientSocket.isClosed()) {
        			udpClient.close();
        		}
                Platform.exit();
                System.exit(0);
                
                if(writer != null) {
                	try {
                		writer.close();
                	} catch (IOException e) {
                		// TODO Auto-generated catch block
                		e.printStackTrace();
                	}
                }
            }
        });
    }
	
	public void createPeerServerWindow(){ //peer ok,reject and busy window
		GridPane grid = new GridPane();
		grid.setAlignment(Pos.CENTER);
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(25, 25, 25, 25));
		grid.setStyle("-fx-background-color: " + colors[randomNum]);
		
		peerServerText = new Text();
        grid.add(peerServerText, 0, 0);
        
        btnOK = new Button("OK");
		HBox hbBtnOK = new HBox(10);
		hbBtnOK.setAlignment(Pos.BOTTOM_LEFT);
		hbBtnOK.getChildren().add(btnOK);
		grid.add(hbBtnOK, 0, 3);
		
		btnReject = new Button("REJECT");
		HBox hbBtnReject = new HBox(10);
		hbBtnReject.setAlignment(Pos.CENTER);
		hbBtnReject.getChildren().add(btnReject);
		grid.add(hbBtnReject, 1, 3);
		
		btnBusy = new Button("BUSY");
		HBox hbBtnBusy = new HBox(10);
		hbBtnBusy.setAlignment(Pos.CENTER);
		hbBtnBusy.getChildren().add(btnBusy);
		grid.add(hbBtnBusy, 2, 3);
		
		Scene scene1 = new Scene(grid, 600, 600);
		peerServerStage = new Stage();
		peerServerStage.setTitle("Chat Request Information Server");
		peerServerStage.setScene(scene1);  
		
	}
	
	public void addButtonsToPeerServerWindow(){ 
		btnOK.setOnAction(new EventHandler<ActionEvent>() { //server  ok
            @Override
            public void handle(ActionEvent e) {
            	System.out.println("OK button clicked");
            	
            	peerAcceptingServer.getPeerServer().sendMessage(new Message(MessageType.Type.ok)); //send ok message from peer to peer
            	peerAcceptingServer.getPeerServer().peerServerStage.close(); //close the screen with ok,reject and busy buttons
            	openChatWindow(false); //openChatWindow for requested peer
            	peerAcceptingServer.getPeerServer().setChatStage(chatStage);
            	peerAcceptingServer.getPeerServer().setChatInstances(messages,chatBox);
            	peerAcceptingServer.getPeerServer().updateMessageIndex(messageIndex);
    			chatStage.show();
            }
        });
        
        btnReject.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
            	System.out.println("Reject button clicked");
            	peerServer.sendMessage(new Message(MessageType.Type.reject));
            }
        });
        
        btnBusy.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
            	System.out.println("Busy button clicked");
            	peerServer.sendMessage(new Message(MessageType.Type.busy));
            }
        });
	}
	
	public synchronized void sendMessage(PeerClient peerClient, Message msg) {		//add one chat message to queue of peerClient
		peerClient.addNewMessageToQueue(msg);
	}
	
	public synchronized void getMessageFromPeerClient(){
		peerClient.setGetMessage(true);
	}
	
	public void openChatWindow(boolean isClient){ //chat window updated from peerClient when message has sent, from peerServer when message is received
		chatStage = new Stage();
		chatStage.setTitle("Chat");
		Pane root = new Pane();
		Scene scene;
		chatBox = new VBox(5);
		messages = new ArrayList<>();
		ScrollPane container = new ScrollPane();
		container.setPrefSize(260, 400);
	    container.setContent(chatBox); 
	    root.setStyle("-fx-background-color: " + colors[randomNum]);
	    
	    TextField messageTextField = new TextField();
	    messageTextField.relocate(20, 440);
	    final Button send = new Button("Send");
	    send.relocate(220, 440);
	    chatBox.getStyleClass().add("chatbox");
	    container.relocate(20, 20);
	    
	    root.getChildren().addAll(messageTextField, send, container);
	  
	    scene = new Scene(root,300,500);
	    chatStage.setScene(scene);
	    
	    send.setOnAction(evt->{
	    	if(!(messageTextField.getText().equals(""))) {
	    		if(isClient) { //if this is the peer who is requested chatting
		    		messages = peerServer.getMessages();
		    		chatBox = peerServer.getChatBox();
		    		messageIndex = peerServer.getMessageIndex();
	    		}
	    		else { ////if this is the peer who is accepted chatting
	    			messages = peerAcceptingServer.getPeerServer().getMessages();
		    		chatBox = peerAcceptingServer.getPeerServer().getChatBox();
		    		messageIndex = peerAcceptingServer.getPeerServer().getMessageIndex();
	    		}
	    		
		        messages.add(new Label(username + ": " + messageTextField.getText()));
		        chatBox.getChildren().add(messages.get(messageIndex)); //update chat screen
		        messageIndex = messageIndex + 1;
		        if(isClient) { //if this is the peer who is requested chatting
		        	sendMessage(peerClient, new Message(MessageType.Type.chat, username, messageTextField.getText())); //send chat message
		        	peerServer.updateMessageIndex(messageIndex);
		        	peerServer.updateMessages(messages);
		        	peerServer.updateChatBox(chatBox);
		        }
		        else {  ////if this is the peer who is accepted chatting
		        	sendMessage(peerAcceptingServer.getPeerClient(), new Message(MessageType.Type.chat, username, messageTextField.getText())); //send chat message
		        	peerAcceptingServer.getPeerServer().updateMessageIndex(messageIndex);
				    peerAcceptingServer.getPeerServer().updateMessages(messages);
				    peerAcceptingServer.getPeerServer().updateChatBox(chatBox);
		        }
	    	}
	    });
	    
  		System.out.println("Chat window showed!");
	}
}
