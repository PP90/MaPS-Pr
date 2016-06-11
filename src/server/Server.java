package server;

import java.io.IOException;
import java.sql.SQLException;


/**
 *
 * @author p3
 */
public class Server {

    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     * @throws java.sql.SQLException
     */
    public static void main(String[] args) throws IOException, SQLException {
 boolean startServer=true;
 
 //GPSCoordinates gps=new GPSCoordinates(0.0, 0.0);
 //System.out.println("The distance is: "+gps.computeDistance(0.0, 0.0525));
 
 if(startServer){
        Thread t=new Thread((Runnable) new GreetinServer());
           t.start();
 }
    }
}
    
    
