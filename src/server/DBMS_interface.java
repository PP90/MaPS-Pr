package server;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DBMS_interface {
    static  final String DRIVER = "com.mysql.jdbc.Driver";             
    static  final String DATABASE_URL = "jdbc:mysql://localhost/proximitymarketdb";   
    private final String USERNAME="admin";
    private final String PASSWORD="password420$";
    private Connection connection;
    
    //It works
    public DBMS_interface(){
        connection=null;
        System.out.println(this.getClass().getName());
        try{
            Class.forName(DRIVER);
            connection=DriverManager.getConnection(DATABASE_URL, USERNAME,PASSWORD);
        }
        catch(SQLException | ClassNotFoundException sqlException){  
            System.out.println(sqlException.toString());
        }
    }
   
    //It works
    private ResultSet queryShowAllUser(){
        try{
            String query="SELECT username FROM account";
            PreparedStatement ps= connection.prepareStatement(query);
            return ps.executeQuery();
        }
        catch(SQLException sqlException){
            sqlException.printStackTrace();
         return null;
        }
    }
    
    public ArrayList<String> seeNearAdsQuery(String typology, ArrayList<String> keywords,  GPSCoordinates gpsCoordinates){
       
        String query="SELECT * FROM Ad WHERE Typology=? AND Latitude BETWEEN ? AND ? AND Longitude BETWEEN ? AND ?";
        
         String regularExpression="AND Description REGEXP '";
        if(keywords!=null){
            for(int i=0; i<keywords.size()-1; i++) regularExpression+=keywords.get(i)+"+|";
            regularExpression+=keywords.get(keywords.size()-1)+"+'";
            query+=regularExpression;
        }
        
        System.out.println("query: "+query);
       
        try {
            PreparedStatement ps= connection.prepareStatement(query);
            ps.setString(1, typology);
            ps.setDouble(2, gpsCoordinates.getLatMin());
            ps.setDouble(3, gpsCoordinates.getLatMax());
            ps.setDouble(4, gpsCoordinates.getLonMin());
            ps.setDouble(5, gpsCoordinates.getLonMax());
            ResultSet rs=ps.executeQuery();
            return this.getNearAdsArrayList(rs,gpsCoordinates);
            
        } catch (SQLException ex) {
          ex.printStackTrace();
           return null;
        }
               
    }
    
    public boolean changePassword(String username, String newPwd){
    String updatePass="UPDATE account SET `password`=? WHERE `email`=?";

        try {
             PreparedStatement ps = connection.prepareStatement(updatePass);
              ps.setString(1, newPwd);
              ps.setString(2, username);
              return true;
        } catch (SQLException ex) {
            Logger.getLogger(DBMS_interface.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
   
    }
    
    //It works
    public boolean insertAd(String typology,FileInputStream fisDescription,
            FileInputStream fisPhoto, double price, 
            String validFrom, String validUntil,
            double latitude, double longitude){
        String insertAdQuery;
       
        insertAdQuery = "INSERT INTO ad (`Typology`, `Description`, `Photo`,`Price`, `ValidFrom`, `ValidUntil`, `Latitude`, `Longitude`)"
                + " values (?, ?, ? ,?, ?, ?, ?, ?)";
         PreparedStatement ps;
        try {
            ps = connection.prepareStatement(insertAdQuery);
            ps.setString(1, typology);
            if(fisDescription!=null)  ps.setBlob(2, fisDescription);
            else ps.setNull(2, java.sql.Types.BLOB);
         
            if(fisPhoto!=null) ps.setBlob(3, fisPhoto);
            else ps.setNull(3, java.sql.Types.BLOB);
            ps.setDouble(4, price);
            ps.setString(5, validFrom);
            ps.setString(6, validUntil);
            ps.setDouble(7, latitude);
            ps.setDouble(8, longitude);
            ps.executeUpdate();
            
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(DBMS_interface.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
            return false;
        }
             

        
 
    }
     //It works
    
    public ArrayList<String> getUserList() throws SQLException{
         ArrayList<String> usersList;
     ResultSet rs=this.queryShowAllUser();
    ResultSetMetaData md = rs.getMetaData();
    int columns = md.getColumnCount();
    usersList=new ArrayList<>();

    while (rs.next()) {
        for (int i = 1; i <= columns; ++i) {
            usersList.add((String) rs.getObject(i));
            }
    }
        
        return usersList;
    }
    
    private ArrayList<String> getNearAdsArrayList(ResultSet rs, GPSCoordinates gpsCoordinates){
         ArrayList<String> arrayList = null;
        try {
              arrayList=new ArrayList<>();
              int coutner=0;
                while (rs.next()) {
                    coutner++;
                    
                   int id=(int) rs.getObject(1);
                   String typology= (String) rs.getObject(2);
                   String description= (String)rs.getObject(3);
                   byte[] photo= (byte[]) rs.getObject(4);
                   double price= (double) rs.getObject(5);
                   Timestamp from= (Timestamp) rs.getObject(6);
                   Timestamp until = (Timestamp)rs.getObject(7);
                   int distance =gpsCoordinates.computeDistance((double) rs.getObject(8), (double) rs.getObject(9));
                   
                arrayList.add(String.valueOf(id)+","+typology+","+description+","+
                Base64.encode(photo)+","+String.valueOf(price)+","+
                 from.toString()+","+until.toString()+","+
                 distance+";") ; 
         
    }
        } catch (SQLException ex) {
            Logger.getLogger(DBMS_interface.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        System.out.println(arrayList.toString());
        return arrayList;
    }

    
    private ResultSet queryLogin(String username, String password){

        ResultSet result;    
        try{
             String query="SELECT email FROM account WHERE email=? AND password=?";
             PreparedStatement ps= connection.prepareStatement(query);
             ps.setString(1, username);
             ps.setString(2, password);
             result=ps.executeQuery();
            return result;
        }  
        catch(SQLException sqlException){
           sqlException.printStackTrace();
            return null;
        }
    }  
    
    private int getRowsNumber(ResultSet rs) throws SQLException{
        int nRows=0;
    while(rs.next()){
        nRows++;
    }
    return nRows;
    }
    
    
    public boolean checkLogin(String username, String password) throws SQLException{
    ResultSet rs=queryLogin(username, password);
    return ( getRowsNumber(rs)==1);
    }
    
    //test the functionality
  public int insertUser(String email, String password, String name, String surname, String sex){
       
      
      int sexInt=Integer.parseInt(sex);
        try{
             String insert="insert INTO account (`email`,`password`,`name`,`surname`,`sex`)" + " VALUES (?,?,?,?,?)";
             PreparedStatement ps= connection.prepareStatement(insert);
             ps.setString(1, email);
             ps.setString(2, password);
             ps.setString(3, name);
             ps.setString(4, surname);
             ps.setInt(5, sexInt);
             ps.executeUpdate();
            return 1;
        }
        
        catch(com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException error){
            System.out.println("Duplicate");
            return -2;
        }
        catch(SQLException sqlException){
            return -1;    
        }
        
   }
  
  //It works
  public boolean deleteUser(String username, String password){
       
        try{
             String insert="delete FROM account WHERE email=?  AND password=?";
             PreparedStatement ps= connection.prepareStatement(insert);
             ps.setString(1, username);
             ps.setString(2, password);
            ps.executeUpdate();
            return true;
        }
        catch(SQLException sqlException){
            sqlException.printStackTrace();            
            return false;    
        }
   }
  
      public void closeConnection(){
        
        try{
            connection.close();
        }
        catch(SQLException sqlException){
                 sqlException.printStackTrace();
        }
    }

    }

 