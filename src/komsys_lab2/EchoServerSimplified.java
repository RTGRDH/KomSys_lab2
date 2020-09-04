import java.net.*; 
import java.io.*; 

public class EchoServerSimplified
{ 
    public static void main(String[] args) throws IOException 
    { 
	ServerSocket serverSocket = new ServerSocket(Integer.parseInt(args[0])); 
	
	System.out.println ("Waiting for connection.....");
	Socket clientSocket = serverSocket.accept(); 
	System.out.println ("Connection successful");
	System.out.println ("Waiting for input.....");
	
	PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true); 
	BufferedReader in = new BufferedReader( 
					       new InputStreamReader( clientSocket.getInputStream())); 
	
	String inputLine; 
	
	while ((inputLine = in.readLine()) != null) 
	    { 
		System.out.println ("Server: " + inputLine); 
		out.println(inputLine); 
		
		if (inputLine.equals("Bye.")) 
		    break; 
	    } 
	
	out.close(); in.close(); clientSocket.close(); serverSocket.close(); 
    } 
} 
