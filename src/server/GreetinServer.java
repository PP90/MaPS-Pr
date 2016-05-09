package server;

import EntityClasses.Ad;
import EntityClasses.FormatMessage;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectOutputStream;
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

   
   private void insertUserCase(DBMS_interface dbms_if,  ArrayList<String> dataParsed,  DataOutputStream out) throws IOException{
     int insertUserCase=dbms_if.insertUser(dataParsed.get(1), dataParsed.get(2),
             dataParsed.get(3),dataParsed.get(4),dataParsed.get(5));
     
            switch (insertUserCase) {
                case 1:
                    out.writeUTF(FormatMessage.INSERT_USER_OK);        
                    break;
                    
                case -1:
                    out.writeUTF(FormatMessage.INSERT_USER_NO);
                    break;
                    
                case -2:
                    out.writeUTF(FormatMessage.INSERT_USER_DUPLICATE);
                    System.out.println("Duplicate");
                    break;
                    
                default:
                    System.out.println("Message format ERROR in the registration user.");
                    break;
            }
   }
   
   private void deleteUser(DBMS_interface dbms_if,   ArrayList<String> dataParsed, DataOutputStream out) throws IOException{
        if(dbms_if.deleteUser(dataParsed.get(1), dataParsed.get(2))==true)  out.writeUTF("OK"); 
       else  out.writeUTF("NO");    

    }
   
   private void checkLogin(DBMS_interface dbms_if,  ArrayList<String> dataParsed,  DataOutputStream out) throws SQLException, IOException{
        if(dbms_if.checkLogin(dataParsed.get(1), dataParsed.get(2))==true){
                      System.out.println("User "+dataParsed.get(1)+"logged correctly");
                    out.writeUTF(FormatMessage.LOGIN_OK);
                    }else{
                         System.out.println("Result is KO");
                    out.writeUTF(FormatMessage.LOGIN_NO);
                    }   
   }
   
   //TOO: TO BE TESTED
   private void modifyPwd(DBMS_interface dbms_if,  ArrayList<String> dataParsed,  DataOutputStream out) throws IOException{
       if(dbms_if.changePassword(dataParsed.get(1),dataParsed.get(2)))  out.writeUTF("OK");
       else  out.writeUTF("NO");
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

   
   public boolean sendImage(ObjectOutputStream outObject){
       try {
    
               FileInputStream fis=new FileInputStream("C:\\sample\\sample.jpg");//TODO: CREATE A DIRECTORY IN WHICH THERE ARE SUBDIRECTORY BASED ON USERNAME
               int fisSize=fis.available();
               byte[] buffer=new byte[fisSize];
               fis.read(buffer);
               outObject.writeObject(buffer);
               return true;
           
       } catch (FileNotFoundException ex) {
           System.out.println("Error in retriving file");
           Logger.getLogger(GreetinServer.class.getName()).log(Level.SEVERE, null, ex);
           return false;
       } catch (IOException ex) {
           Logger.getLogger(GreetinServer.class.getName()).log(Level.SEVERE, null, ex);
           return false;
       }
   }
   
   
   public void  handleAD(Socket server, DBMS_interface dbms_if, ArrayList<String> receivedFromTheClient){
                         ObjectOutputStream outObject=null;
                         ///IMPLEMENTES THE POSSIBLE SUBCASES OF AD:
           
           //DELETE AD. MANDATOTY
           //MODIFY AD. optional
           
           switch(receivedFromTheClient.get(1)){
               case FormatMessage.USER_AD://SEE MY AD case. TODO: IMPLEMENT THE QUERY CASE INTO DB
                    try {
         
           System.out.println("The client wants its ads");
           outObject = new ObjectOutputStream(server.getOutputStream());
           sendImage(outObject);
       } catch (IOException ex) {
           Logger.getLogger(GreetinServer.class.getName()).log(Level.SEVERE, null, ex);
       } finally {
           try {
               outObject.close();
           } catch (IOException ex) {
               Logger.getLogger(GreetinServer.class.getName()).log(Level.SEVERE, null, ex);
           }
       }
                   break;
                   
                   
                   
               case FormatMessage.INSERT_AD://INSERT AD. TODO: IMPLEMENT THE INSERT CASE INTO DB
                   
                   System.out.println("Ad field to be insert: "+receivedFromTheClient.toString());
                   
                   String titleAd=receivedFromTheClient.get(2);
                   String description=receivedFromTheClient.get(3);
                   String findOffer=receivedFromTheClient.get(4);
                   String from=receivedFromTheClient.get(5);
                   String until=receivedFromTheClient.get(6);
                  // dbms_if.insertAd(titleAd, description, fisDescription, photo, fisPhoto, TIMEOUT, PORT, from, until, until, PORT, PORT)insertAd()
                   break;
                   
                   
                   
               case "DEL":
                   
                   break;
                   
               default:
                   System.out.println("Error");
                   break;
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
                         checkLogin(dbms_if, dataParsed, out);
                         break;
                         
                         //TODO: THESE BELOW THREE CASES MUST BE AGGREGATED IN A SINGLE FUNCTION IN ORDER TO HAVE A BETTER CODE READABILITY
                     case FormatMessage.INSERT_USER:
                         insertUserCase(dbms_if, dataParsed, out);
                         break;
                         
                     case FormatMessage.DELETE_USER:
                         deleteUser(dbms_if, dataParsed,  out);
                         break;
                         
                     case FormatMessage.MODIFY_PWD:
                         modifyPwd(dbms_if, dataParsed,  out);
                         break;
                         
                     case FormatMessage.INSERT_AD:
                        break;
                        
                     case FormatMessage.AD:
                         handleAD(server, dbms_if, dataParsed);
                         break;
                 }
                   
                 
                 
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

