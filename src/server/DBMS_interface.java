package server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;

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
     //It works
    public ArrayList<String> getUserList() throws SQLException{
         ArrayList<String> usersList = null;
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
 //It works
    public ResultSet queryLogin(String username, String password){

        ResultSet result;    
        try{
             String query="SELECT username FROM account WHERE (username=? OR email=?) AND password=?";
             PreparedStatement ps= connection.prepareStatement(query);
             ps.setString(1, username);
             ps.setString(2, username);
             ps.setString(3, password);
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
    
    
  public boolean insertUser(String username, String email, String password){
       
        try{
             String insert="insert INTO account (`username`,`email`,`password`)" + " VALUES (?,?,?)";
             PreparedStatement ps= connection.prepareStatement(insert);
             ps.setString(1, username);
             ps.setString(2, email);
             ps.setString(3, password);
             ps.execute();
            return true;
        }
        catch(SQLException sqlException){
            sqlException.printStackTrace();            
            return false;    
        }
   }
  
  //It works
  public boolean deleteUser(String username, String password){
       
        try{
             String insert="delete FROM account WHERE (username=? OR email=?) AND password=?";
             PreparedStatement ps= connection.prepareStatement(insert);
             ps.setString(1, username);
             ps.setString(2, username);
             ps.setString(3, password);
             ps.execute();
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

 