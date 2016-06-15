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
     * @throws java.io.IOException
     * @throws java.sql.SQLException
     */
    public static void main(String[] args) throws IOException, SQLException {
 
 //test();

        Thread t=new Thread((Runnable) new GreetinServer());
           t.start();
 
    }
    static void test(){
        GPSCoordinates gps=new GPSCoordinates(42.90, 10.99);
        gps.setDistance(1);
 DBMS_interface dbif=new DBMS_interface();

    
    }
}