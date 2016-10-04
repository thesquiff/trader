package com.futurewebdynamics.trader.common;

import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
        this.connection = DatabaseUtils.refreshConnection(this.connection, connectionString);

        try {

            Statement statement = this.connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT COUNT(*) FROM price");
            resultSet.next();

            int count = resultSet.getInt(1);
            cache = new ArrayList<PriceInformation>(count);

            logger.info(count + " price records identified");

            resultSet = connection.createStatement().executeQuery("SELECT UNIX_TIMESTAMP(price.timestamp), price FROM price ORDER BY 'index'");

            int index = 0;
            while (resultSet.next()) {

                cache.add(new PriceInformation (resultSet.getInt(1),resultSet.getInt(2), 0));
                index++;
            }
            logger.info(index + " price records cached");
            logger.debug("Closing database connection");
            this.connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public ArrayList<PriceInformation> getCache() {
        return cache;
    }


}