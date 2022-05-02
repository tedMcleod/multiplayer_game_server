package mpserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Scanner;

/**
 * Each client that connects to the server is given its own thread that runs the code in run().
 * @author Ted_McLeod
 *
 */
public class ClientThread implements Runnable {
    private Socket csocket;
    private MultiThreadServer server;
    private String id;

    public ClientThread(Socket csocket, MultiThreadServer server, String id) {
        this.csocket = csocket;
        this.server = server;
        this.id = id;
    }

    /**
     * The code run by each client thread. It listens for messages and sends the messages on to other clients as described
     * in the ServerDriver API.
     */
    public void run() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(csocket.getInputStream()));
            String inputLine = in.readLine();
            while (inputLine != null) {
                final String cmd = inputLine;
                try (Scanner reader = new Scanner(cmd)) {
                    String first = reader.next();
                    if (first.equals("TO")) {
                        String toId = reader.next();
                        String restOfCmd = reader.nextLine();
                        server.sendMessage(id + restOfCmd, toId);
                    } else if (first.equals("DC")) {
                        break;
                    } else {
                        server.broadcast(id + " " + cmd, id);
                    }
                } catch (Exception err) {
                    err.printStackTrace();
                }
                inputLine = in.readLine();
            }
            csocket.close();
            server.getActiveClients().remove(id);
            server.broadcast(id + " DC");
        } catch (IOException e) {
            server.getActiveClients().remove(id);
            server.broadcast(id + " DC");
            System.out.println(e);
        }
    }
}