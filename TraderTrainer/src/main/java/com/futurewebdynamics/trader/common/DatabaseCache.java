package com.futurewebdynamics.trader.common;

import jdk.nashorn.internal.runtime.regexp.joni.exception.ValueException;
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

    long dateStartTimestampMs;
    long dateEndTimestampMs;

    public DatabaseCache(String connectionString) {
        this.connectionString = connectionString;
    }

    public DatabaseCache(String connectionString, long dateStartTimestampMs, long dateEndTimestampMs) {
        this.connectionString = connectionString;
        this.dateEndTimestampMs = dateEndTimestampMs;
        this.dateStartTimestampMs = dateStartTimestampMs;
    }

    public void loadData() {
        this.connection = DatabaseUtils.refreshConnection(this.connection, connectionString);

        try {

            Statement statement = this.connection.createStatement();

            ResultSet countResultSet;
            ResultSet dataResultSet;

            if (dateEndTimestampMs <= 0) {
                countResultSet = statement.executeQuery("SELECT COUNT(*) FROM price ");
                dataResultSet = connection.createStatement().executeQuery("SELECT price.timestamp, askprice, bidprice FROM price ORDER BY price.timestamp ASC");
            } else {
                countResultSet = statement.executeQuery("SELECT COUNT(*) FROM price WHERE price.timestamp >= " + dateStartTimestampMs + " AND price.timestamp <= " + dateEndTimestampMs);
                dataResultSet = connection.createStatement().executeQuery("SELECT price.timestamp, askprice, bidprice FROM price WHERE price.timestamp >= " + dateStartTimestampMs + " AND price.timestamp <= " + dateEndTimestampMs + " ORDER BY price.timestamp ASC");
            }

            countResultSet.next();

            long count = countResultSet.getLong(1);
            if (count > Integer.MAX_VALUE) {
                throw new ValueException("too many prices");
            }

            logger.info(count + " price records identified");

            cache = new ArrayList<PriceInformation>((int)count);

            int index = 0;
            while (dataResultSet.next()) {

                PriceInformation newPrice = new PriceInformation (dataResultSet.getLong(1),(int)(dataResultSet.getDouble(2)*100.0), (int)(dataResultSet.getDouble(3)*100.0));
                cache.add(newPrice);
                index++;
            }
            logger.info(index + " price records cached");
            logger.debug("Closing database connection");
            this.connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
            logger.error(e.getMessage(), e);
        }

    }

    public ArrayList<PriceInformation> getCache() {
        return cache;
    }


}