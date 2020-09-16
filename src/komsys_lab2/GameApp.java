package komsys_lab2;
import java.util.*;
import java.net.*; 
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

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
        long timeBefore;
        long timeAfter;
        String answer;
        DatagramSocket UDPsocket = new DatagramSocket(port);
        System.out.println("UDP server running at port " + port);
        System.out.println(tal);//Temp
        byte[] bufferRecieve = new byte[512];
        byte[] bufferSend = new byte[512];
        while (true) 
        {
            DatagramPacket request = new DatagramPacket(bufferRecieve, bufferRecieve.length);
            timeBefore = System.currentTimeMillis();
            UDPsocket.receive(request); //Recieves request from client
            timeAfter = System.currentTimeMillis();
            String guess = new String(request.getData()).trim();
            if((timeAfter - timeBefore) > 15)
            {
                System.out.println(guess);
                if(guess.equals("Hello"))
                {
                    answer = "WELCOME";
                    bufferSend = answer.getBytes();
                }
                else if (guess.equals("Bye.")) 
                {
                    break;
                }
                else if (Integer.parseInt(guess) == tal) 
                {
                    System.out.println("Correct");
                    answer = "Correct";
                    bufferSend = answer.getBytes();
                } 
                else if (Integer.parseInt(guess) > tal) 
                {
                    System.out.println("HI");
                    answer = "HI";
                    bufferSend = answer.getBytes();
                } 
                else if (Integer.parseInt(guess) < tal) 
                {
                    System.out.println("LO");
                    answer = "LO";
                    bufferSend = answer.getBytes();
                }
                InetAddress clientAddress = request.getAddress();
                int clientPort = request.getPort();
                DatagramPacket response = new DatagramPacket(bufferSend, bufferSend.length, clientAddress, clientPort);
                UDPsocket.send(response); //Sends response to client
            }
            else
            {
                answer = "BUSY";
                bufferSend = answer.getBytes();
            }
        }
        UDPsocket.close();
    }


    static void tcpServer(int port) throws IOException
    {
        ServerSocket serverSocket = new ServerSocket(port);
        Socket clientSocket = null;
        PrintWriter out = null;
        BufferedReader in = null;
        while(true)
        {
            try
            {
                String inputLine; int tal; boolean game; int annoying = 0;
                System.out.println ("Waiting for connection.....");
                clientSocket = serverSocket.accept();
                clientSocket.setSoTimeout(15000);
                System.out.println ("Connection successful\nWaiting for input.....");
                out = new PrintWriter(clientSocket.getOutputStream(), true);
                in = new BufferedReader(
                        new InputStreamReader( clientSocket.getInputStream()));
                while ((inputLine = in.readLine()) != null)
                {
                    if(annoying == 2){
                        System.out.println("Client annoying. Terminating connection");
                        out.close(); in.close(); clientSocket.close(); break; 
                    }
                    if(inputLine.equals("HELLO"))
                    {
                        out.println("WELCOME");
                        tal = gissaTalet();
                        game = true;
                        while(game == true){
                            inputLine = in.readLine();
                            if (inputLine.equals("Bye.")){
                                game = false;
                            }
                            else if (Integer.parseInt(inputLine) == tal){
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
                        }
                    }else if(inputLine.equals("BYE")) break;
                    else{
                        out.println("HELLO for game.");
                        annoying++;
                    }
                }
                System.out.println("Connection Terminated");
                out.close(); in.close(); clientSocket.close();
            }catch(NumberFormatException sk){
                System.out.println("Annoying client.\n" + sk);
                if(clientSocket != null && in != null && out != null)
                {
                    clientSocket.close(); in.close(); out.close();
                }
            }
            catch(SocketTimeoutException x){
                if(out != null){
                    System.out.println("Timeout\n" + x);
                    out.println("TIMEOUT");
                    if(clientSocket != null && in != null)
                    {
                        clientSocket.close(); in.close(); out.close();
                    }
                }
            }catch(NullPointerException np){
                System.out.println("Nullpointer\n" + np);
            }
            catch (IOException ex) {
                Logger.getLogger(GameApp.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    static int gissaTalet()
    {
        return (int)(100.0 * Math.random());
    }

    static void udpClient() 
    {
        System.out.print("UDP client port: ");
        int port = _nextInt();
        String userInput;
        try 
        {
            InetAddress address = InetAddress.getLocalHost();
            DatagramSocket socket = new DatagramSocket();
            byte[] bufferSend = new byte[512];
            byte[] bufferRecieve = new byte[512];
            System.out.print("Input: ");
            while (true) 
            {
                userInput = scan.nextLine();
                bufferSend = userInput.getBytes();
                DatagramPacket sendPacket = new DatagramPacket(bufferSend, bufferSend.length, address, port);
                socket.send(sendPacket); //Request to server
                if(userInput.equals("Bye."))
                {
                    break; //Doesn't work
                }
                DatagramPacket recievePacket = new DatagramPacket(bufferRecieve, bufferRecieve.length);
                socket.receive(recievePacket); //Response from server
                String serverAnswer = new String(recievePacket.getData()).trim();
                System.out.println("FROM SERVER:" + serverAnswer);
                System.out.print("Input: ");
            }
            socket.close();
        } 
        catch (SocketTimeoutException ex) 
        {
            System.out.println("Timeout error: " + ex.getMessage());
            ex.printStackTrace();
        } 
        catch (IOException ex) 
        {
            System.out.println("Client error: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    static void tcpClient(int port) throws IOException
    {
	// Put your tcp client code here
        Socket echoSocket = null;
        PrintWriter out = null;
        BufferedReader in = null;
        BufferedReader stdIn = null;
        try{
            echoSocket = new Socket();
            int tOut = 5000;
            echoSocket.connect(new InetSocketAddress(InetAddress.getLocalHost(), port), tOut);
            echoSocket.setSoTimeout(tOut);
            out = new PrintWriter(echoSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(
                                            echoSocket.getInputStream()));
            stdIn = new BufferedReader(
                                       new InputStreamReader(System.in));
            String userInput;
            String serverInput;
            System.out.print ("input: ");
            while ((userInput = stdIn.readLine()) != null) {
                if(userInput.equals("Bye.")){
                    out.println(userInput);
                    break;
                }
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
        }catch(ConnectException c){
            System.out.println("No Server Available");
        }
        catch(SocketTimeoutException se){
            System.out.println("TIMEOUT");
        }
        catch(NullPointerException nulp){
            System.out.println("Annoying Client");
        }
        catch(SocketException se){
            System.out.println(se);
        }catch(IOException ex){
            System.out.println(ex);
        }finally{
            if(out != null && in != null && stdIn != null && echoSocket != null)
            {
                System.out.println("Closing all connections.");
                out.close(); in.close(); stdIn.close(); echoSocket.close();
            }
        }
    }
    
} 
