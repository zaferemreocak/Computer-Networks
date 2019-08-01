
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

public class Server implements Runnable{
	public static ArrayList<User> userList;
	public static ArrayList<Thread> threadList = new ArrayList<Thread>();
	public static ArrayList<WorkerRunnable> workerRunnableList = new ArrayList<WorkerRunnable>();
	public static ArrayList<Socket> clientSocketList = new ArrayList<Socket>();
	
	protected int serverPort;
	protected ServerSocket serverSocket = null;
	protected boolean isStopped = false;
	protected Thread runningThread = null;
	int threadCounter = 0;
	Thread serverThread;
	Writer writer;
	
	public Server(int serverPort, ArrayList<User> userList, Writer writer){
		this.writer = writer;
		this.serverPort = serverPort;
		this.userList = userList;
	}
	
	public void run(){		
		while(!getIsStopped()){
			Socket clientSocket=null;
			
			try{
				clientSocket=this.serverSocket.accept(); //server for handling multiple requests
			}
			catch(IOException e){
				if(getIsStopped()){
					System.out.println("Server stopped");
					return;
				}
				throw new RuntimeException("Error accepting client connection",e);
			}
			clientSocketList.add(clientSocket);
			try {
				workerRunnableList.add(new WorkerRunnable(clientSocket, userList, writer)); //each client is served in different WorkerRunnable threads
			} catch (UnsupportedEncodingException | FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			threadList.add(new Thread(workerRunnableList.get(threadCounter)));
			threadList.get(threadCounter).start();
			threadCounter++;
		}
	}

	public void startServer(Server server){
		try{
			this.serverSocket = new ServerSocket(this.serverPort); //create serverSocket with the constant registry port
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
		
		for(int i = 0; i < threadList.size(); i++) {
			try {
				threadList.get(i).join(); // wait for all worker runnables created
			} catch (InterruptedException e) {
				throw new RuntimeException("Error while waiting for client threads",e);
			}
		}
		
		try{
			System.out.println("Server socket will be closed");
			this.serverSocket.close();
		}
		catch(IOException e){
			throw new RuntimeException("Error closing server", e);
		}
	}
	
	public ArrayList<User> getUserList(){
		return this.userList;
	}
}
