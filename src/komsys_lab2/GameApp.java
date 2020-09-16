/**
 * @author Carl-Bernhard Hallberg och Ernst Reutergårdh, TIDAA3
 */

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
            else if(protocol==TCP)
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
        long timer2 = 0;
        String answer;
        int currentClient;
        int clientPort = 0;
        DatagramSocket UDPsocket = new DatagramSocket(port);
        DatagramPacket response, request;
        InetAddress clientAddress = null;
        System.out.println("UDP server running at port " + port);
        byte[] bufferRecieve = new byte[512];
        byte[] bufferSend = new byte[512];
        while (true) 
        {
            try
            {
                System.out.println("Waiting for client");
                request = new DatagramPacket(bufferRecieve, bufferRecieve.length);
                UDPsocket.receive(request); //Recieves request from client
                clientPort = request.getPort();
                currentClient = request.getPort();
                clientAddress = request.getAddress();
                String guess = new String(request.getData()).trim();
                System.out.println(guess);
                if(guess.equals("Hello"))
                {
                    answer = "WELCOME";
                    bufferSend = answer.getBytes();
                    System.out.println(tal);//Temp
                    response = new DatagramPacket(bufferSend, bufferSend.length, clientAddress, clientPort);
                    UDPsocket.send(response);
                    while(true)
                    {
                        Arrays.fill(bufferSend,(byte)0); //Flush send buffer
                        Arrays.fill(bufferRecieve,(byte)0); //Flush receive buffer
                        timeBefore = System.currentTimeMillis();
                        UDPsocket.receive(request); //Packets for game
                        timeAfter = System.currentTimeMillis();
                        long totalTime = timeAfter - timeBefore;
                        if(request.getPort() != currentClient)
                        {
                            timer2 += totalTime;
                        }
                        if(request.getPort() == currentClient)
                        {
                            if(totalTime + timer2 < 15000)
                            {
                                timer2 = 0;
                                guess = new String(request.getData()).trim();
                                System.out.println(guess);
                                if(guess.equals("Bye."))
                                {
                                    break; 
                                }
                                if (Integer.parseInt(guess) == tal) 
                                {
                                    System.out.println("Correct");
                                    answer = "Correct";
                                    bufferSend = answer.getBytes();
                                    response = new DatagramPacket(bufferSend, bufferSend.length, clientAddress, clientPort);
                                    UDPsocket.send(response);
                                    break;
                                } 
                                else if (Integer.parseInt(guess) > tal) 
                                {
                                    System.out.println("HI");
                                    answer = "HI";
                                    bufferSend = answer.getBytes();
                                    response = new DatagramPacket(bufferSend, bufferSend.length, clientAddress, clientPort);
                                    UDPsocket.send(response);
                                } 
                                else if (Integer.parseInt(guess) < tal) 
                                {
                                    System.out.println("LO");
                                    answer = "LO";
                                    bufferSend = answer.getBytes();
                                    response = new DatagramPacket(bufferSend, bufferSend.length, clientAddress, clientPort);
                                    UDPsocket.send(response);
                                }
                            }
                            else
                            {
                                System.out.println("Timeout");
                                answer = "Timeout";
                                bufferSend = answer.getBytes();
                                response = new DatagramPacket(bufferSend, bufferSend.length, clientAddress, clientPort);
                                UDPsocket.send(response);
                                clientAddress = null; clientPort = 0;
                                break;
                            }   
                        }
                        else
                        {
                            answer = "BUSY";
                            bufferSend = answer.getBytes();
                            response = new DatagramPacket(bufferSend, bufferSend.length, request.getAddress(), request.getPort());
                            UDPsocket.send(response);
                        }
                    }
                }
            }
            catch(NumberFormatException ex)
            {
                System.out.println(ex);
                answer = "Annoying Client. Disconnecting.";
                if(clientAddress != null && clientPort != 0){
                    bufferSend = answer.getBytes();
                    response = new DatagramPacket(bufferSend, bufferSend.length, clientAddress, clientPort);
                    UDPsocket.send(response);
                }
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

    static void udpClient() throws SocketException, IOException 
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
            socket.setSoTimeout(5000);
            while (true) 
            {
                System.out.print("Input: ");
                userInput = scan.nextLine();
                Arrays.fill(bufferSend,(byte)0); //Flush send buffer
                bufferSend = userInput.getBytes();
                DatagramPacket sendPacket = new DatagramPacket(bufferSend, bufferSend.length, address, port);
                socket.send(sendPacket); //Request to server
                if(userInput.equals("Bye."))
                {
                    break; //Doesn't work
                }
                Arrays.fill(bufferRecieve,(byte)0); //Flush recieve buffer
                DatagramPacket recievePacket = new DatagramPacket(bufferRecieve, bufferRecieve.length);
                socket.receive(recievePacket); //Response from server
                String serverAnswer = new String(recievePacket.getData()).trim();
                System.out.println("FROM SERVER:" + serverAnswer);
                if(serverAnswer.equals("Correct"))
                { 
                    break;
                }
                if(serverAnswer.equals("Timeout")) {
                    break;
                }
            }
            System.out.println("Socket closed");
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
