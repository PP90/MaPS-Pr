/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import EntityClasses.FormatMessage;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 *
 * @author p3
 */
public class HandleUser {
    
     static void insertUserCase(DBMS_interface dbms_if,  ArrayList<String> dataParsed,  DataOutputStream out) throws IOException{
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
   
   static void deleteUser(DBMS_interface dbms_if,   ArrayList<String> dataParsed, DataOutputStream out) throws IOException{
        if(dbms_if.deleteUser(dataParsed.get(1), dataParsed.get(2))==true)  out.writeUTF("OK"); 
       else  out.writeUTF("NO");    

    }
   
   static void checkLogin(DBMS_interface dbms_if,  ArrayList<String> dataParsed,  DataOutputStream out) throws SQLException, IOException{
        if(dbms_if.checkLogin(dataParsed.get(1), dataParsed.get(2))==true){
                      System.out.println("User "+dataParsed.get(1)+"logged correctly");
                    out.writeUTF(FormatMessage.LOGIN_OK);
                    }else{
                         System.out.println("Result is KO");
                    out.writeUTF(FormatMessage.LOGIN_NO);
                    }   
   }
   
   //TOO: TO BE TESTED
   static void modifyPwd(DBMS_interface dbms_if,  ArrayList<String> dataParsed,  DataOutputStream out) throws IOException{
       if(dbms_if.changePassword(dataParsed.get(1),dataParsed.get(2)))  out.writeUTF("OK");
       else  out.writeUTF("NO");
   }
   
 

}
