package server;

import EntityClasses.FormatMessage;
import com.sun.org.apache.xml.internal.security.exceptions.Base64DecodingException;
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
   
   public GreetinServer() throws IOException
   {
      SERVER_SOCKET = new ServerSocket(PORT);
      SERVER_SOCKET.setSoTimeout(TIMEOUT);
   }

   private ArrayList<String> getParsedDataFromBuffer(String dataFromBuffer){
       ArrayList<String> parsedInput=new ArrayList<>();
       if(dataFromBuffer!=null){
           
            System.out.println("From the client: "+dataFromBuffer);
            String[] myData=dataFromBuffer.split(FormatMessage.DELIMITS);
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
         try
         {
          
            System.out.println("Waiting for client on port " +   SERVER_SOCKET.getLocalPort() + "...");
             try (Socket server = SERVER_SOCKET.accept()) {
                 System.out.println("Just connected to: "+ server.getRemoteSocketAddress());
                 DataInputStream in = new DataInputStream(server.getInputStream());
                 String readFromBuffer= in.readUTF();
                 ArrayList<String> dataParsed=getParsedDataFromBuffer(readFromBuffer);
                 DBMS_interface dbms_if=new DBMS_interface();
                 DataOutputStream out = new DataOutputStream(server.getOutputStream());
                 
                 switch(dataParsed.get(0)){
                     case FormatMessage.LOGIN:
                         HandleUser.checkLogin(dbms_if, dataParsed, out);
                         break;
                         
                         //TODO: THESE BELOW THREE CASES MUST BE AGGREGATED IN A SINGLE FUNCTION IN ORDER TO HAVE A BETTER CODE READABILITY
                     case FormatMessage.INSERT_USER:
                         HandleUser.insertUserCase(dbms_if, dataParsed, out);
                         break;
                         
                     case FormatMessage.DELETE_USER:
                         HandleUser.deleteUser(dbms_if, dataParsed,  out);
                         break;
                         
                     case FormatMessage.MODIFY_PWD:
                         HandleUser.modifyPwd(dbms_if, dataParsed,  out);
                         break;                   
                        
                     case FormatMessage.AD:
                         HandleAd.handleAD(server, dbms_if, dataParsed);
                         break;
                 }
                   
                 
             
             } catch (Base64DecodingException ex) {
                 Logger.getLogger(GreetinServer.class.getName()).log(Level.SEVERE, null, ex);
             }         }
         catch(SocketTimeoutException s){
            System.out.println("Socket timed out!");
            break;
         }
         
         catch(IOException e){
             System.out.println("Io Exception occured!");
            break;
          }
         
         catch (SQLException ex) { 
             System.out.println("SQL Exception occured!");
          }
         
      }
   }

}

