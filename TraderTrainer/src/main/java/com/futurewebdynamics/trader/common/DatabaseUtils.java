package com.futurewebdynamics.trader.common;

import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by 52con on 15/04/2016.
 */
public class DatabaseUtils {

    final static Logger logger = Logger.getLogger(DatabaseUtils.class);

    public static Connection refreshConnection(Connection connection, String connectionString) {
        try {
            while (connection == null || connection.isClosed() || !connection.isValid(2)) {
                logger.info("Database connection lost - retrying");
                logger.debug("Connection string " + connectionString);
                try {
                    connection = DriverManager.getConnection(connectionString);
                    logger.info("Database connection established");
                } catch (SQLException e) {
                    logger.debug(e.getMessage());
                }
            }
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }

        return connection;
    }
}
