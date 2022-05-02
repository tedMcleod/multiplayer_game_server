import java.io.IOException;
import java.net.ServerSocket;

import mpserver.MultiThreadServer;

/**
<ul>
    <li>Starts a server in port 1234 or on the port passed in the first argument when running the main method</li>
    <li>Clients can send messages to talk to other clients. Generally, the messages are made up tokens separated by spaces.</li>
    <li>When a client connects, a message is sent to just that client telling the client what client ID they have been assigned.
        The message is in the form: id + " ID"
<pre>
Example:
5e42484f-7f52-4be2-9ab0-c41796387504 ID
</pre>
    </li>
    <li> If a client sends a message that begins with the token "TO", it is sent to the client with the id following the TO token.
          Every message is passed on with the id of the sender preceding the message.
<pre>
Example:
If the client with id 7h91234b-7c33-4bd1-822c-a41796388099 sent this message:
TO 5e42484f-7f52-4be2-9ab0-c41796387504 AddOP 231 337
the following message would be sent to the client with id 5e42484f-7f52-4be2-9ab0-c41796387504
7h91234b-7c33-4bd1-822c-a41796388099 AddOP 231 337
</pre>
    </li>
    <li> If a client sends a message the does NOT begin with the token "TO", it is broadcast to all other clients.
<pre>
Example:
If the client with id 7h91234b-7c33-4bd1-822c-a41796388099 sent this message:
MOVE 231 337
the following message would be sent to each of the other clients:
7h91234b-7c33-4bd1-822c-a41796388099 MOVE 231 337
</pre>
    </li>
    <li> If a client wants to disconnect, they should sent a message that just says "DC". Other clients will then get a message saying
         the id of the client that disconnected and DC.
<pre>
Example:
If the client with id 7h91234b-7c33-4bd1-822c-a41796388099 sent this message:
DC
The client would be disconnected from the server and the other clients would recieve the message:
7h91234b-7c33-4bd1-822c-a41796388099 DC
Note that the same message would be sent to the other clients even if the client disconnected unexpectedly (i.e. the program was ended or internet disconnected...etc)
</pre>
    </li>

@author Ted_McLeod

*/
public class ServerDriver {

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        int port = 1234;
        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException err) {
                System.err.println("port must be an integer");
                err.printStackTrace();
            }
        }
        ServerSocket ssock = new ServerSocket(port);
        MultiThreadServer serve = new MultiThreadServer(ssock);
        new Thread(serve).start();
    }

}
