import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class MainServer extends Application{
	public static ArrayList<User> userList = new ArrayList<User>();
	Server server;
	boolean isStarted = false;
	int serverPort = 9000;
	UDPAcceptingServer UDPacceptingServer;
	int constantUDPport = 65507; //65507
	long timeOutInMilliSeconds = 20000; //20000
	Writer writer = null;

	//SERVER STARTS HERE
		public static void main(String[] args) {
			launch(args);		
		}
		
		public void start(Stage primaryStage) throws UnsupportedEncodingException, FileNotFoundException {	//GUI of server
			primaryStage.setTitle("Server");
	        
			GridPane grid = new GridPane();
			grid.setAlignment(Pos.CENTER);
			grid.setHgap(10);
			grid.setVgap(10);
			grid.setPadding(new Insets(25, 25, 25, 25));
			grid.setStyle("-fx-background-color: #00CCCC;-fx-padding: 10px;");
			
			Text scenetitle = new Text("Welcome Server!");
			scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
			grid.add(scenetitle, 0, 0, 2, 1);

			Button btnStartServer = new Button("Start Server");
			HBox hbBtnStartServer = new HBox(10);
			hbBtnStartServer.setAlignment(Pos.BOTTOM_LEFT);
			hbBtnStartServer.getChildren().add(btnStartServer);
			grid.add(hbBtnStartServer, 2, 3);
			
			Button btnStopServer = new Button("Stop Server");
			HBox hbBtnStopServer = new HBox(10);
			hbBtnStopServer.setAlignment(Pos.CENTER);
			hbBtnStopServer.getChildren().add(btnStopServer);
			grid.add(hbBtnStopServer, 3, 3);
			
			Button btnShowUserList = new Button("Show User List");
			HBox hbBtnShowUserList = new HBox(10);
			hbBtnShowUserList.setAlignment(Pos.BOTTOM_RIGHT);
			hbBtnShowUserList.getChildren().add(btnShowUserList);
			grid.add(hbBtnShowUserList, 0, 3);
			
			final Text actiontarget = new Text();
	        grid.add(actiontarget, 5, 6);
	        
	        final ListView<String> listView = new ListView<String>();
	        listView.setPrefSize(300, 300); 
	        grid.add(listView, 0, 0);
	        
	        writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("MainServer.txt"), "utf-8")); // log file of registry server
	        
	        btnStartServer.setOnAction(new EventHandler<ActionEvent>() {
	            @Override
	            public void handle(ActionEvent e) {
	                actiontarget.setFill(Color.FIREBRICK);
	                actiontarget.setText("Server is running");
	                if(!isStarted) { //if server is not started, create and start it
	                	server = new Server(serverPort, userList, writer);
	                	server.startServer(server);		
	                	UDPacceptingServer = new UDPAcceptingServer(constantUDPport, timeOutInMilliSeconds, userList, writer); //UDP accepting server is listening from a constant port, for finding and sending available udp ports for hello message
	                	UDPacceptingServer.startServer(UDPacceptingServer);	
	                	isStarted = true;
	                }
	            }
	        });
	        
	        btnStopServer.setOnAction(new EventHandler<ActionEvent>() {
	            @Override
	            public void handle(ActionEvent e) {
	                actiontarget.setFill(Color.FIREBRICK);
	                actiontarget.setText("Server is not running");
	                if(isStarted) { //if server is started, stop and close sockets and log file
	                	server.stop();
	                	UDPacceptingServer.stop();
	                	isStarted = false;
	                	
	                	if(writer != null) {
	                		try {
								writer.close();
							} catch (IOException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
	                	}
	                }
	            }
	        });
	        
	        btnShowUserList.setOnAction(new EventHandler<ActionEvent>() {
	            @Override
	            public void handle(ActionEvent e) {
	                if(server!=null) {
	                	ObservableList<String> observableList = FXCollections.observableArrayList();
	                	ArrayList<User> userList = new ArrayList<User>();
	                	userList = server.getUserList();
	                	for(int i=0; i<userList.size(); i++) {
	                		observableList.add(new String(userList.get(i).printUser())); //update view on the server GUI with userList
	                	}
	                	listView.setItems(observableList);
	                }
	            }
	        });
			
			Scene scene = new Scene(grid, 600, 600);
			primaryStage.setScene(scene);
			
	        primaryStage.show();
	    }
}
