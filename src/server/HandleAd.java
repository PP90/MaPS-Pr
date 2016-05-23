/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import EntityClasses.FormatMessage;
import com.sun.org.apache.xml.internal.security.exceptions.Base64DecodingException;
import com.sun.org.apache.xml.internal.security.utils.Base64;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 *
 * @author p3
 */


public class HandleAd {
    static InputStream input=null;
    static File imageToDB=null;
    static File descriptionToDb=null;
    static final String pathFiles="C:\\ProximityMarket\\";
    
    static boolean sendImage(ObjectOutputStream outObject){
       try {
    
            FileInputStream fis=new FileInputStream("C:\\sample\\sample.jpg");//TODO: CREATE A DIRECTORY IN WHICH THERE ARE SUBDIRECTORY BASED ON USERNAME
            int fisSize=fis.available();
            byte[] buffer=new byte[fisSize];
            fis.read(buffer);
            outObject.writeObject(buffer);
            return true;
        } 
       catch (FileNotFoundException ex) {
            System.out.println("Error in retriving file");
            Logger.getLogger(GreetinServer.class.getName()).log(Level.SEVERE, null, ex);
            return false;
       }
       catch (IOException ex) {
           Logger.getLogger(GreetinServer.class.getName()).log(Level.SEVERE, null, ex);
           return false;
       }
   }
   
   static String getCurrentTs(){
    SimpleDateFormat s = new SimpleDateFormat("yyyyMMddhhmmss");
    return s.format(new Date());

   }
   
   static void receiveImage(String imgStr, String username) throws Base64DecodingException, IOException{
       byte[] decodedImage=Base64.decode(imgStr);
       System.out.println("The decoded size is "+decodedImage.length);
       String filename=pathFiles+"JPEG_"+username+"_"+getCurrentTs()+".jpg";
       if(writeImageOnFile(decodedImage, filename)) System.out.println("Image written correctly");
       else System.out.println("Error during writing image");
   }
   
   
   static boolean writeDescriptionOnFile(String description, String username) throws FileNotFoundException{
         try {
             String filename=pathFiles+"DESC_"+username+"_"+getCurrentTs()+".txt";
             descriptionToDb=new File(filename);
             FileOutputStream fos=new FileOutputStream(descriptionToDb);
             fos.write(description.getBytes());
             fos.close();
             return true;
         } catch (IOException ex) {
             Logger.getLogger(HandleAd.class.getName()).log(Level.SEVERE, null, ex);
             return false;
         }
   }
   
   static boolean writeImageOnFile(byte[] byteToSaveOnFile, String filename){
   
   try {
     ByteArrayInputStream bais = new ByteArrayInputStream(byteToSaveOnFile);
    imageToDB = new File(filename);
    BufferedImage bi=ImageIO.read(bais);
    ImageIO.write(bi, "png", imageToDB);
} catch (IOException e) {
    return false;
}
   return true;
   }
   
   
   
   public static boolean seeNearAds(String uname, String latString, String longitString, String distString){
       double lat=Double.valueOf(latString);
       double longit=Double.valueOf(longitString);
       int dist=Integer.valueOf(distString);
       /*
       HERE:
       double MinLat=computeMinLat(lat,dist);
       double MaxLat=computeMaxLat(lat,dist);
       double MinLon=computeMinLon(lon,dist);
       double MaxLon=computeMaxLon(lon,dist);
        
       
       */
   return false;
   }
     public static void  handleAD(Socket server, DBMS_interface dbms_if, ArrayList<String> receivedFromTheClient,  DataOutputStream out) throws IOException, Base64DecodingException{
                         ///IMPLEMENTES THE POSSIBLE SUBCASES OF AD:
           ObjectOutputStream outObject=null;
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
                   
                   
                   
               case FormatMessage.INSERT_AD:
                   String titleAd=receivedFromTheClient.get(3);
                   writeDescriptionOnFile(receivedFromTheClient.get(4),receivedFromTheClient.get(2));
                   int findOfferValue;
                   if(receivedFromTheClient.get(5).equalsIgnoreCase("true")) findOfferValue=1;
                   else findOfferValue=0;
                   
                   double priceDouble;
                   try{
                       priceDouble=Double.valueOf(receivedFromTheClient.get(6));
                   }catch(Exception e){//TODO: IF THE STRING IS EMPTY, THEN PRICE IS EQUAL TOZERO
                       priceDouble=0;
                   }
                   double latitude=Double.valueOf(receivedFromTheClient.get(7));
                   double longitude=Double.valueOf(receivedFromTheClient.get(8));
                   String from=receivedFromTheClient.get(9);
                   String until=receivedFromTheClient.get(10);
                
                    FileInputStream fisImg=new FileInputStream(imageToDB);
                    FileInputStream fisDesc=new FileInputStream(descriptionToDb);
                    fisImg.read();
                    fisImg.read();
                 
                  if(dbms_if.insertAd(titleAd,
                          fisDesc, fisImg,
                          findOfferValue,
                          priceDouble,
                          from, until,
                          "Pisa",//Hard coeded. maybe to delete
                          latitude,longitude))  out.writeUTF(FormatMessage.INSERT_AD_OK);  
                    else out.writeUTF(FormatMessage.INSERT_USER_NO);
                  
                    fisImg.close();
                    fisDesc.close();
                    break;
                   
                   
                   
               case "DEL":
                   
                   break;
                   
               case "IMG":
                   System.out.println("IMG");
                   receiveImage(receivedFromTheClient.get(3),receivedFromTheClient.get(2));  
                   break;
                   
               case "SEE_NEAR":
                   System.out.println("SEE_NEAR");
                   
                   break;
                   
                   
               default:
                   System.out.println("Error");
                   break;
           }
      
   }
}
