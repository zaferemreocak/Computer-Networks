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

public class UDPPortFinder implements Runnable{
	protected Socket clientSocket = null;
	String UDPport;
	UDPServer udpServer;
	ServerSocket serverSocket;
	long timeOutInMilliSeconds;
	ArrayList<User> userList;
	String username;
	Writer writer = null;
	
	public UDPPortFinder(Socket clientSocket, long timeOutInMilliSeconds, ArrayList<User> userList, Writer writer) throws UnsupportedEncodingException, FileNotFoundException{
		this.writer = writer;
		this.clientSocket = clientSocket;
		this.timeOutInMilliSeconds = timeOutInMilliSeconds;
		this.userList = userList;
	}
	
	public void run() {
		ObjectInputStream inp = null;
		ObjectOutputStream out = null;
		
		try {
			inp = new ObjectInputStream(clientSocket.getInputStream());
			out = new ObjectOutputStream(clientSocket.getOutputStream());
			
				try {
					serverSocket = new ServerSocket(0); //find available port on the server
					UDPport = serverSocket.getLocalPort() + "";
					openUDPServerForSignedInClient(); // open new UDP server on the registry for listening each client's UDPclients. this connection used for hello messaging
					System.out.println("Free port for UDP is " + UDPport);
					Message msg = new Message(MessageType.Type.freeUDPPort,UDPport); //send available port to the client
					out.writeObject(msg);
					out.flush();
					synchronized(writer) {
						writer.write(System.currentTimeMillis() + " UDPPortFinder message sent to client is " + msg.printMessage());
						writer.write("\n");
						writer.flush();
					}
				} catch (IOException e) {
					//break;
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
			if(clientSocket != null) {
				try {
					clientSocket.close();
				} catch (IOException e) {
					System.out.println("ClientSocket in UDPport finder can't be closed");
				}
			}
			if(serverSocket != null) {
				try {
					serverSocket.close();
				} catch (IOException e) {
					System.out.println("serverSocket in UDPport finder can't be closed");
				}
			}
		}
	}
	
	public void openUDPServerForSignedInClient() throws NumberFormatException, UnsupportedEncodingException, FileNotFoundException{ //synchronized
		udpServer = new UDPServer(Integer.parseInt(UDPport), timeOutInMilliSeconds, userList, writer);
		Thread udpServerThread = new Thread(udpServer);
		udpServerThread.start();
	}
	
	public void closeUDPSocket(){
		if(udpServer != null && !udpServer.serverSocket.isClosed()) {
			udpServer.close();
			System.out.println("UDP socket closed in UDPportFinder");
		}
	}
}