

package org.magnezis.inversiones.magnezis.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionDB {

    private static Connection connection;

    private ConnectionDB() {
    }

    public static Connection getConnectionDataBase() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(CredentialsDB.URL_DB, CredentialsDB.USER_DB, CredentialsDB.PASS_DB);
        }
        return connection;
    }
}