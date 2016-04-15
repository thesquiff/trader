package com.futurewebdynamics.trader.common;

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
        DatabaseUtils.refreshConnection(connection, connectionString);

        try {
            ResultSet resultSet = connection.createStatement().executeQuery("SELECT COUNT(*) FROM price");
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


}