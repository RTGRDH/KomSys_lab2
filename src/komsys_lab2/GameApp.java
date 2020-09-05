package komsys_lab2;
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
    
    static void udpServer(int port) throws SocketException, IOException
    {
	// Put your udp server code here
        int tal = gissaTalet();
        String answer;
        boolean flag = true;
        DatagramSocket UDPsocket = new DatagramSocket(port);
	System.out.println("UDP server running at port " + port);
        System.out.println(tal);//Temp
        while (flag) 
        {
            DatagramPacket request = new DatagramPacket(new byte[1], 1);
            UDPsocket.receive(request); //Recieves request from client
            
            //String quote = "Test";
            //byte[] buffer = quote.getBytes();
            byte[] buffer = new byte[512];
            String guess = new String(buffer, 0, request.getLength()); //Something wrong with making byte to string, makes square
            //System.out.println(guess);
            //String guess = String.valueOf(tal);
            if (Integer.parseInt(guess) == tal){ 
                System.out.println("Rätt");
                answer = "Rätt";
                buffer = answer.getBytes();
                break;
            }
            else if(Integer.parseInt(guess) > tal)
            {
                System.out.println("HI");
                answer = "HI";
                buffer = answer.getBytes();
            }else if(Integer.parseInt(guess) < tal){
                System.out.println("LO");
                answer = "LO";
                buffer = answer.getBytes();
            }
            else if (guess.equals("Bye.")){
                flag = false;
                break;
            }
            InetAddress clientAddress = request.getAddress();
            int clientPort = request.getPort();
            DatagramPacket response = new DatagramPacket(buffer, buffer.length, clientAddress, clientPort);
            UDPsocket.send(response); //Sends respond to client

        }
        UDPsocket.close();
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
        System.out.print("UDP client port: ");
        int port = _nextInt();
 
        try {
            InetAddress address = InetAddress.getLocalHost();
            DatagramSocket socket = new DatagramSocket();
 
            while (true) {
 
                DatagramPacket request = new DatagramPacket(new byte[1], 1, address, port);
                socket.send(request); //Request to server
 
                byte[] buffer = new byte[512];
                DatagramPacket response = new DatagramPacket(buffer, buffer.length);
                socket.receive(response); //Response from server
                
                String quote = new String(buffer, 0, response.getLength());
 
                System.out.println(quote);
                System.out.println();
                //Sends correctly
                String test = "7"; 
                buffer = test.getBytes();
                DatagramPacket response2 = new DatagramPacket(buffer, buffer.length);
                socket.send(response2);
                Thread.sleep(10000);
            }
 
        } catch (SocketTimeoutException ex) {
            System.out.println("Timeout error: " + ex.getMessage());
            ex.printStackTrace();
        } catch (IOException ex) {
            System.out.println("Client error: " + ex.getMessage());
            ex.printStackTrace();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }

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
