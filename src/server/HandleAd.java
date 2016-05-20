/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import EntityClasses.FormatMessage;
import com.sun.org.apache.xml.internal.security.exceptions.Base64DecodingException;
import com.sun.org.apache.xml.internal.security.utils.Base64;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author p3
 */


public class HandleAd {
     static InputStream input=null;
    static File imageToDB=null;
    
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
   
   
   static byte[] receiveImage(String imgStr) throws Base64DecodingException, IOException{
        byte[] decoded=Base64.decode(imgStr);
       byte[] byteArrayImage = imgStr.getBytes();
       System.out.println("The decoded size is "+decoded.length);
       input = new ByteArrayInputStream(byteArrayImage);
        writeFile(byteArrayImage);
               return byteArrayImage;
   }
   
   
   static boolean writeFile(byte[] imageFromClient) throws IOException{

       /*  try {
   
	  File file=new File("C:\\pippo2.jpg");
          file.setWritable(true);
          file.createNewFile();
	    FileOutputStream fos = new FileOutputStream(file); 
	    fos.write(imageFromClient);
	    fos.close();
	       
	    System.out.println("Done");
        }catch(Exception e){
            e.printStackTrace();
 
        }
       
       */
       
       
       return false;
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
                   
                   String titleAd=receivedFromTheClient.get(3);
                   String description=receivedFromTheClient.get(4);
                   int findOfferValue;
                   if(receivedFromTheClient.get(5).equalsIgnoreCase("true")) findOfferValue=1;
                   else findOfferValue=0;
                   double priceDouble=0;
                   try{
                       priceDouble=Double.valueOf(receivedFromTheClient.get(6));
                   }catch(Exception e){
                   e.printStackTrace();
                   }
                   double latitude=Double.valueOf(receivedFromTheClient.get(7));
                   double longitude=Double.valueOf(receivedFromTheClient.get(8));
                   String from=receivedFromTheClient.get(9);
                   String until=receivedFromTheClient.get(10);
                   //TODO: create two file:
                   //the first one for the description. Name file format: uname_TS_JPEG
                   //the second one for the image. Name file format: uname_TS_DESC
                  
                   
                  dbms_if.insertAd(titleAd,
                          null, null,
                          null,null,
                          findOfferValue,
                          priceDouble,
                          from, until,
                          null,
                          latitude,longitude);
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
