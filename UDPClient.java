import java.io.*;
import java.net.*;

public class UDPClient implements Runnable { 
	int port;
	InetAddress IP;
	DatagramSocket clientSocket;
	boolean isClosed = false;
	long helloMessageIntervalInMilliSeconds;
	String username;
	Writer writer = null;
	
	public UDPClient(InetAddress IP, int port, long helloMessageIntervalInMilliSeconds, String username,Writer writer) throws UnsupportedEncodingException, FileNotFoundException { //UDP Client sends hello messages to UDPServer
		this.writer = writer;
		try {
			clientSocket = new DatagramSocket();
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.port = port;
		this.IP = IP;
		this.helloMessageIntervalInMilliSeconds = helloMessageIntervalInMilliSeconds;
		this.username = username;
		System.out.println("UDP client port number local:"+ clientSocket.getLocalPort() + " to:" + clientSocket.getPort());
	}

	@Override
	public void run() {
		final String message = "HELLO from " + username;
		
		while(true) {     
			if(isClosed) {
				break;
			}
			DatagramPacket sendPacket = new DatagramPacket(message.getBytes(), message.length(), IP, port); //send hello message
			try {
				clientSocket.send(sendPacket);
				synchronized(writer) {
					writer.write(System.currentTimeMillis() + " UDPClient message sent to server is " + new String(sendPacket.getData()));
					writer.write("\n");
					writer.flush();
				}
			} catch (IOException e) {
				if(isClosed) {
					break;
				}
			}
			System.out.println("Message to server: " + message);
			try {
				Thread.sleep(helloMessageIntervalInMilliSeconds); // wait in the amount of helloMessageIntervalInMilliSeconds
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void close(){
		isClosed = true;
		clientSocket.close();
	}
}