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
		tcpClient();
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

    static void tcpServer(int port)
    {
	// Put your tcp server code here
	System.out.println("TCP server running at port " + port);

    }

    static void udpClient()
    {	
	// Put your udp client code here
	System.out.println("UDP client");

    }

    static void tcpClient()
    {
	// Put your tcp client code here
	System.out.print("TCP client.");

    }
    
} 
