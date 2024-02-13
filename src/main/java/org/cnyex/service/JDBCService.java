package org.cnyex.service;

import java.sql.Connection;
import java.sql.SQLException;

public interface JDBCService {

    Connection getConnection();

    void showSQLException(SQLException ex);
}
