/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import EntityClasses.FormatMessage;
import com.sun.org.apache.xml.internal.security.exceptions.Base64DecodingException;
import com.sun.org.apache.xml.internal.security.utils.Base64;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author p3
 */


public class HandleAd {
    
    
     static boolean sendImage(ObjectOutputStream outObject){
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
   
   
   static byte[] receiveImage(String imgStr) throws Base64DecodingException{
        byte[] decoded=Base64.decode(imgStr);
       byte[] b = imgStr.getBytes();
       System.out.println("The string size is "+imgStr.length());
       System.out.println("The size is "+b.length);
       System.out.println("The decoded size is "+decoded.length);
               return b;
   }
   
     public static void  handleAD(Socket server, DBMS_interface dbms_if, ArrayList<String> receivedFromTheClient) throws IOException, Base64DecodingException{
                         ObjectOutputStream outObject=null;
                         ///IMPLEMENTES THE POSSIBLE SUBCASES OF AD:
           
           //DELETE AD. MANDATOTY
           //MODIFY AD. optional
             System.out.println("HandleAD");
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
                   String price=receivedFromTheClient.get(5);
                   String from=receivedFromTheClient.get(6);
                   String until=receivedFromTheClient.get(7);
                  // dbms_if.insertAd(titleAd, description, fisDescription, photo, fisPhoto, TIMEOUT, PORT, from, until, until, PORT, PORT)insertAd()
                   break;
                   
                   
                   
               case "DEL":
                   
                   break;
                   
               case "IMG":
                   System.out.println("IMG");
                   System.out.println();
                   receiveImage(receivedFromTheClient.get(2));  
                   break;
               default:
                   System.out.println("Error");
                   break;
           }
      
   }
}
