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
 if(startServer){
        Thread t=new Thread((Runnable) new GreetinServer());
           t.start();
 }


    }
}
    
    
