/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author p3
 */


public class GreetinServer implements Runnable
{
   private final ServerSocket SERVER_SOCKET;
   private final int PORT=8080;
   private final int TIMEOUT=10000000;
   private final String DELIMITS="[,]";
   private final String LOGIN="LOGIN";
   private final String INSERT_USER="INSERT_USER";
   
   public GreetinServer() throws IOException
   {
      SERVER_SOCKET = new ServerSocket(PORT);
      SERVER_SOCKET.setSoTimeout(TIMEOUT);
   }

   
   private ArrayList<String> getParsedDataFromBuffer(String dataFromBuffer){
       ArrayList<String> parsedInput=new ArrayList<>();
       if(dataFromBuffer!=null){
           
            System.out.println("From the client:"+dataFromBuffer);
            String[] myData=dataFromBuffer.split(DELIMITS);
            parsedInput.addAll(Arrays.asList(myData));
            return parsedInput;
            
       }else{
           return null;
       }
   }

   
   @Override
   public void run()
   {
      while(true)
      {
           DBMS_interface dbms_if2=new DBMS_interface();
            System.out.println("result delete: "+dbms_if2.deleteUser("aaa", "ccc"));
           
         try
         {
            System.out.println("Waiting for client on port " +   SERVER_SOCKET.getLocalPort() + "...");
            Socket server = SERVER_SOCKET.accept();
            System.out.println("Just connected to "+ server.getRemoteSocketAddress());
            DataInputStream in = new DataInputStream(server.getInputStream());
         
            String readFromBuffer= in.readUTF();
            ArrayList<String> dataParsed=getParsedDataFromBuffer(readFromBuffer);
            DBMS_interface dbms_if=new DBMS_interface();
             DataOutputStream out = new DataOutputStream(server.getOutputStream());
             
            switch(dataParsed.get(0)){
                case LOGIN:
                    
                    if(dbms_if.checkLogin(dataParsed.get(1), dataParsed.get(2))==true){
                      System.out.println("Result is OK");
                    out.writeUTF(LOGIN+",OK");
                    }else{
                         System.out.println("Result is KO");
                    out.writeUTF(LOGIN+",NO");
                    }   
                break;
                case INSERT_USER:
                   if(dbms_if.insertUser(dataParsed.get(1), dataParsed.get(2),dataParsed.get(3))) 
                   out.writeUTF(INSERT_USER+",OK");
                   else out.writeUTF(INSERT_USER+",NO");
                   break;
                   
 }
            
            server.close();
         }catch(SocketTimeoutException s)
         {
            System.out.println("Socket timed out!");
            break;
         }catch(IOException e)
         {
            break;
         } catch (SQLException ex) {
              Logger.getLogger(GreetinServer.class.getName()).log(Level.SEVERE, null, ex);
          }
      }
   }

}

