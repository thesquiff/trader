package com.futurewebdynamics.trader.trainer;

import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by 52con on 14/04/2016.
 */
public class DatabaseCache {

    private String connectionString;

    private Connection connection = null;

    private ArrayList<PriceInformation> cache = null;

    final static Logger logger = Logger.getLogger(DatabaseCache.class);

    public DatabaseCache(String connectionString) {
        this.connectionString = connectionString;

    }

    public void loadData() {
        refreshConnection();

        try {
            ResultSet resultSet = connection.createStatement().executeQuery("SELECT COIUNT(*) FROM price");
            resultSet.next();
            int count = resultSet.getInt(0);
            cache = new ArrayList<PriceInformation>(count);

            resultSet = connection.createStatement().executeQuery("SELECT * FROM price ORDER BY 'index'");

            int index = 0;
            while (resultSet.next()) {
                index++;
                cache.add(new PriceInformation (resultSet.getInt("timestamp"),resultSet.getInt("price")));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public ArrayList<PriceInformation> getCache() {
        return cache;
    }

    private void refreshConnection() {
        try {
            while (connection == null || connection.isClosed() || !connection.isValid(2)) {
                logger.info("Database connection lost - retrying");
                try {
                    connection = DriverManager.getConnection(connectionString);
                } catch (SQLException e) {
                    logger.debug(e.getMessage());
                }
            }
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
    }

}