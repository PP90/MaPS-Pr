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
 boolean startServer=true;
 

 if(startServer){
        Thread t=new Thread((Runnable) new GreetinServer());
           t.start();
 }
    }
    public void test(){
        GPSCoordinates gps=new GPSCoordinates(42.90, 10.99);
        gps.setDistance(1);
        ArrayList<String> keywords=new ArrayList<>();
        keywords.add("vino");
        keywords.add("scadente");
        keywords.add("economico");
 DBMS_interface dbif=new DBMS_interface();
 ArrayList<String> adList=dbif.seeNearAdsQuery("buy", null, gps);
System.out.println("Element n0 is "+adList.get(0));
    
    }
}
    
    
