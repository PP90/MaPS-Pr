package server;

import EntityClasses.FormatMessage;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
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
    static final String PATH_FILE="C:\\ProximityMarket\\";
    static final String DATE_FORMAT="yyyymmdd";
    

   
    
   static ArrayList<String> getKeyword(String keywordList){
        ArrayList<String> keywordsArrayList=new ArrayList<>();
       if(keywordList!=null){
           
            String[] myData=keywordList.split(" ");
            keywordsArrayList.addAll(Arrays.asList(myData));
            return keywordsArrayList;
            
       }else{
           return null;
       }
   
   }
   
   static String getCurrentTs(){
    SimpleDateFormat s = new SimpleDateFormat(DATE_FORMAT);
    return s.format(new Date());
   }
   
   static String appendString(String toAppend){
   return null;
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
   
   
   static ArrayList<String> getKeywordsList(String keyword){
   ArrayList<String> keywordsList=new ArrayList<>();
   if (keyword != null) {
            String[] myData = keyword.split(" ");
            keywordsList.addAll(Arrays.asList(myData));
        }
   return keywordsList;
   }
   

   public static void seeNearAds(String uname,
      String typology, String keywords, String latString, String longitString,
      String distString, DBMS_interface dbIf,  DataOutputStream out) throws SQLException, IOException{
       
      double lat=Double.valueOf(latString);
      double lon=Double.valueOf(longitString);
      int dist=Integer.valueOf(distString);
      double distKm=dist/1000;
      ArrayList<String> keywordList=getKeywordsList(keywords); 
      
      GPSCoordinates gpsCoordinates=new GPSCoordinates(lat, lon);
      gpsCoordinates.setDistance(distKm);
      ArrayList<String> nearAds=dbIf.seeNearAdsQuery(typology,null,gpsCoordinates); 
     if(nearAds!=null)  out.writeUTF(nearAds.toString());
     else out.writeUTF("Error");
       }
     
   
     public static void  handleAD(Socket server, DBMS_interface dbms_if, ArrayList<String> receivedFromTheClient,  DataInputStream in,DataOutputStream out) throws IOException, SQLException{
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
                   String typology=receivedFromTheClient.get(3);

                   double priceDouble;
                   try{
                       priceDouble=Double.valueOf(receivedFromTheClient.get(5));
                   }catch(Exception e){//TODO: IF THE STRING IS EMPTY, THEN PRICE IS EQUAL TOZERO
                       priceDouble=0;
                   }
                   double latitude=Double.valueOf(receivedFromTheClient.get(6));
                   double longitude=Double.valueOf(receivedFromTheClient.get(7));
                   String from=receivedFromTheClient.get(8);
                   String until=receivedFromTheClient.get(9);
                   FileInputStream fisImg=null;
                   
                   if(imageToDB!=null){
                    fisImg=new FileInputStream(imageToDB);
                    fisImg.read();
                   }
                     FileInputStream fisDesc=new FileInputStream(descriptionToDb);
                  if(dbms_if.insertAd(typology, fisDesc, fisImg,  priceDouble,
                          from, until,
                          latitude,longitude))  out.writeUTF(FormatMessage.INSERT_AD_OK);  
                    else out.writeUTF(FormatMessage.INSERT_USER_NO);
                  
                     if(imageToDB!=null) fisImg.close();
                    fisDesc.close();
                    break;
                   
                   
                   
               case "DEL":
                   
                   break;
                   
                
               case "SEE_NEAR":
                   System.out.println("SEE_NEAR");
                   seeNearAds(receivedFromTheClient.get(2),receivedFromTheClient.get(3),
                           receivedFromTheClient.get(4), receivedFromTheClient.get(5),
                           receivedFromTheClient.get(6),receivedFromTheClient.get(7),
                           dbms_if,  out);
                   break;
                   
                   
               default:
                   System.out.println("Error, format message not expected");
                   break;
           }
      
   }
}
