package org.cnyex.service;

import org.postgresql.Driver;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class JDBCPostgresService implements JDBCService {
    private final String url;
    private final String username;
    private final String password;

    public JDBCPostgresService(String url, String username, String password){
        this.url = url;
        this.username = username;
        this.password = password;
    }

    public Connection getConnection(){
        Connection connection = null;
        try{
            DriverManager.registerDriver(new Driver());
            connection = DriverManager.getConnection(url, username, password);
        }catch(SQLException ex){
            showSQLException(ex);
        }

        return connection;
    }

    public void showSQLException(SQLException ex){
        ex.printStackTrace();
    }
}
