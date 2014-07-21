/**
 *  Constructor Client()
 *  Constructor Client(String host, int port)
 *  Method communication() is used to send "request" to Server
 */
package com.example.peekaroundcorner.socket;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

import android.util.Log;

public class Client {
    
    private String host;
    private int port; 
    private Socket clientSocket;
    PrintWriter out;
    BufferedReader in;
    
    //constructor, initialize host, port and request 
    public Client(String host, int port){
        this.host=host;
        this.port=port;
        
    }
    public void BuildUpConnection() throws IOException{
    	clientSocket = new Socket(host, port);
    	 out= new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream()), true);
    	 in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    }

        
    // send "request" to Server
    public void messageSend(String request){
            out.println(request);
			out.flush();
			Log.v("Socket_messageSend", request);
        
        
    }
    public String messageReceive() throws IOException{
    	
        String s = in.readLine();
        System.out.println(s);  
        return s;
    }
    public void closeConnection() throws IOException{
    	clientSocket.close();
    }
    
}
