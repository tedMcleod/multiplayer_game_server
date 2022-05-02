package mpserver;

import java.io.IOException;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class MultiThreadServer implements Runnable {
	private ConcurrentHashMap<String, Socket> activeClients;
	private ServerSocket ssock;

	public MultiThreadServer(ServerSocket sock) {
		activeClients = new ConcurrentHashMap<String, Socket>();
		ssock = sock;
	}
	public synchronized void addClient(String id, Socket sock) {
		activeClients.put(id, sock);
		sendMessage(getAssignIdCmdStr(id), id);
	}
	
	// The String sent to a client to indicate the client has been assigned that ID
	private String getAssignIdCmdStr(String id) {
		return id + " ID";
	}
	
	public synchronized void sendMessage(String message, String id) {
		Socket sock = activeClients.get(id);
		if (sock != null) {
			try {
				PrintStream pstream = new PrintStream(sock.getOutputStream(), true);
		    	pstream.println(message);
			} catch (IOException e) {
				activeClients.remove(id);
				System.out.println("active clients now: " + activeClients);
				e.printStackTrace();
			}
		}
	}
	
	public synchronized void broadcast(String message) {
		for (Map.Entry<String, Socket> entry : activeClients.entrySet()) {
			sendMessage(message, entry.getKey());
		}
	}
	
	public synchronized void broadcast(String message, String fromId) {
		for (Map.Entry<String, Socket> entry : activeClients.entrySet()) {
			if (!fromId.equals(entry.getKey())) sendMessage(message, entry.getKey());
		}
	}

	public ConcurrentHashMap<String, Socket> getActiveClients() {
		return activeClients;
	}
	
	public static String generateUUID() {
		return UUID.randomUUID().toString();
	}

	@Override
	public void run(){
		try {
			System.out.println("Listening at " + InetAddress.getLocalHost().getHostAddress() + ":" + ssock.getLocalPort());
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
		}
		while (true) {
			Socket sock;
			try {
				sock = ssock.accept();
				String id = generateUUID();
				addClient(id, sock);
				System.out.println("Client connected to " + sock.getInetAddress() + " and assigned UUID: " + id);
				new Thread(new ClientThread(sock, this, id)).start();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	
}
