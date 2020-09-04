import java.io.*;
import java.net.*;

public class EchoClientSimplified {
    public static void main(String[] args) throws IOException {

        String serverHostname = args[0]; int port = Integer.parseInt(args[1]);

        Socket echoSocket = new Socket(serverHostname, port);
	PrintWriter out = new PrintWriter(echoSocket.getOutputStream(), true);
	BufferedReader in = new BufferedReader(new InputStreamReader(
                                        echoSocket.getInputStream()));

	BufferedReader stdIn = new BufferedReader(
                                   new InputStreamReader(System.in));
	String userInput;

        System.out.print ("input: ");
	while ((userInput = stdIn.readLine()) != null) {
	    out.println(userInput);
	    System.out.println("echo: " + in.readLine());
            System.out.print ("input: ");
	}

	out.close(); in.close(); stdIn.close(); echoSocket.close();
    }
}

