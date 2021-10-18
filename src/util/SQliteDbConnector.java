/*
 * Decompiled with CFR 0_110.
 */
package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *This class is use to create connection to JDBC:MySQL database
 * @author Balogun Taiwo
 */


public class SQliteDbConnector {
    private final static String dbURL = "jdbc:mysql://localhost:3306/dbmedical";
    private final static String username = "root";
    private final static String password = "";
    private static Connection conn;
    
    public static void connectToSQLDb(){
        try{
            conn = DriverManager.getConnection(dbURL,username, password);
            if(conn != null){
                System.out.println("DB Connected Successfully!!!");
            }
        }catch(SQLException ex){
            ex.printStackTrace();
        }
        
    }
    
    public static void saveKey(String imagePath, String key, String dateCreated){
        try{
            String sql = "INSERT INTO tblMedicalKey(ImagePath, EncryptKey, DateCreated)VALUES(?,?,?)";
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setString(1, imagePath);
            statement.setString(2, key);
            statement.setString(3, dateCreated);
            int rowsInserted = statement.executeUpdate();
            if(rowsInserted > 0){
                System.out.println("Encryption Key successfully saved!");
            }            
        }
        catch(SQLException ex){
            ex.printStackTrace();            
        }
        
    }
    
    public static boolean isValidKey(String imagePath, String key){
        try{
            String sql = "SELECT * from tblMedicalKey WHERE ImagePath = ? AND EncryptKey = ?";
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setString(1, imagePath);
            statement.setString(2, key);
            ResultSet result = statement.executeQuery();
            if(result.next()){ 
                System.out.println("Encryption Key Validated!!!");
                return true;                
            }   
        }
        catch(SQLException ex){
            ex.printStackTrace();            
        }
        System.out.println("Encryption Key not Valid!!!");
        return false;
    }
    
}

