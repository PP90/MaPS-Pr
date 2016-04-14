/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;


/**
 *
 * @author p3
 */
public class Server {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException, SQLException {
 boolean startServer=true;
 if(startServer){
        Thread t=new Thread((Runnable) new GreetinServer());
           t.start();
 }


    }
}
    
    
