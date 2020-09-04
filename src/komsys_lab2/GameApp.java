import java.util.*;
import java.net.*; 
import java.io.*;

public class GameApp
{
    static public final int SERVER = 1;
    static public final int CLIENT = 2;
    static public final int UDP    = 1;
    static public final int TCP    = 2;
    static int type = 0;
    static int protocol = 0;
    static int wellKnownPort = 2000;
    static Scanner scan = new Scanner (System.in);

    static int _nextInt()
    {
	String line = scan.nextLine();
	return Integer.parseInt(line);
    }
    
    public static void main(String[] args) throws IOException 
    {
	do {
	    System.out.print("(1) Server or (2) client? > ");
	    type = _nextInt();
	} while(type != SERVER && type != CLIENT);

	if(type==SERVER) {
	    System.out.print("Server port: ");
	    wellKnownPort = _nextInt();
	}
	
	do{
	    System.out.print("(1) UDP or (2) TCP? > ");
	    protocol = _nextInt();
	} while(protocol != UDP && protocol != TCP);

	if(type==SERVER) {
	    if(protocol==UDP)
		udpServer(wellKnownPort);
	    else
		tcpServer(wellKnownPort);
	}
	else
	{
	    if(protocol==UDP)
		udpClient();
	    else
                System.out.println("Enter port");
		tcpClient(_nextInt());
	}
    }

    private static boolean isNum(String str){
	int dummy;
	try {
	    Integer.parseInt(str);
	    return true;
	}
	catch(Exception e) {
	    return false;
	}
    }
    
    static void udpServer(int port) throws SocketException
    {
	// Put your udp server code here
        
        DatagramSocket UDPsocket = new DatagramSocket(port);
	System.out.println("UDP server running at port " + port);

    }

    static void tcpServer(int port) throws IOException
    {
	// Put your tcp server code here
	ServerSocket serverSocket = new ServerSocket(port);
	System.out.println ("Waiting for connection.....");
	Socket clientSocket = serverSocket.accept(); 
	System.out.println ("Connection successful");
	System.out.println ("Waiting for input.....");
	PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true); 
	BufferedReader in = new BufferedReader( 
					       new InputStreamReader( clientSocket.getInputStream())); 
	
	String inputLine; 
	
        int tal = gissaTalet();
        
	while ((inputLine = in.readLine()) != null) 
        {
            /*System.out.println ("Server: " + inputLine); 
            out.println(inputLine); */
            System.out.println(tal);
            if (Integer.parseInt(inputLine) == tal){
                System.out.println("Rätt");
                out.println("Rätt");
                break;
            }
            else if(Integer.parseInt(inputLine) > tal)
            {
                System.out.println("HI");
                out.println("HI");
            }else if(Integer.parseInt(inputLine) < tal){
                System.out.println("LO");
                out.println("LO");
            }
            else if (inputLine.equals("Bye.")){
                break;
            }
        }
	
	out.close(); in.close(); clientSocket.close(); serverSocket.close(); 
        
        
        
        
	System.out.println("TCP server running at port " + port);

    }
    
    static int gissaTalet()
    {
        return (int)(100.0 * Math.random());
    }

    static void udpClient()
    {	
	// Put your udp client code here
	System.out.println("UDP client");

    }

    static void tcpClient(int port) throws IOException
    {
	// Put your tcp client code here
        
        Socket echoSocket = new Socket(InetAddress.getLocalHost(), port);
	PrintWriter out = new PrintWriter(echoSocket.getOutputStream(), true);
	BufferedReader in = new BufferedReader(new InputStreamReader(
                                        echoSocket.getInputStream()));

	BufferedReader stdIn = new BufferedReader(
                                   new InputStreamReader(System.in));
	String userInput;
        String serverInput;
        System.out.print ("input: ");
	while ((userInput = stdIn.readLine()) != null) {
	    out.println(userInput);
            serverInput = in.readLine();
	    System.out.println("Server: " + serverInput);
            System.out.print ("input: ");
            if(serverInput.equals("Rätt")){
                break;
            }
	}

	out.close(); in.close(); stdIn.close(); echoSocket.close();
        
        
	System.out.print("TCP client.");

    }
    
} 
