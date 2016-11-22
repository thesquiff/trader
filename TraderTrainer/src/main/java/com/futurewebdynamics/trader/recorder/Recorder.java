package com.futurewebdynamics.trader.recorder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.futurewebdynamics.trader.common.DatabaseUtils;
import com.futurewebdynamics.trader.common.RestHelper;
import com.futurewebdynamics.trader.common.StatementBuffer;
import com.futurewebdynamics.trader.trader.providers.Oanda.data.Prices;
import org.apache.log4j.Logger;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.Queue;

public class Recorder {

    final static Logger logger = Logger.getLogger(Recorder.class);

    public static void main(String[] args) {

        StatementBuffer statementBuffer = new StatementBuffer();

        Properties prop = new Properties();
        InputStream input = null;

        try {
            input = new FileInputStream(args[0]);
            prop.load(input);
        } catch (FileNotFoundException e) {
            logger.error(e);
            e.printStackTrace();
            System.exit(1);
        } catch (IOException e) {
            logger.error(e);
            e.printStackTrace();
            System.exit(1);
        }


        (new Thread() {
            public void run() {

                String accountId = prop.getProperty("accountid");
                String token = prop.getProperty("token");
                int tickIntervalMs = Integer.parseInt(prop.getProperty("tickintervalms"));


                //record data indefinitely
                while (true) {

                    try {
                        String jsonResult = RestHelper.GetJson("https://api-fxpractice.oanda.com/v3/accounts/" + accountId + "/pricing?instruments=BCO_USD", token);

                        long timestamp = System.currentTimeMillis();
                        ObjectMapper jsonDeseserialiser = new ObjectMapper();
                        Prices prices = jsonDeseserialiser.readValue(jsonResult, Prices.class);

                        if (prices.prices.size() <= 0 || prices.prices.get(0).asks.size() <= 0 || prices.prices.get(0).bids.size() <= 0) {
                            logger.debug("Time: " + Long.toString(System.currentTimeMillis()) + " Price: NULL");
                        } else {

                            double askPrice = Double.parseDouble(prices.prices.get(0).asks.get(0).price);
                            double bidPrice = Double.parseDouble(prices.prices.get(0).bids.get(0).price);

                            String sql = "INSERT INTO price (timestamp, askprice, bidprice) VALUES(" + timestamp + ", " + askPrice + ", " + bidPrice + ")";

                            statementBuffer.AddToBuffer(sql);
                        }

                        Thread.currentThread().sleep(tickIntervalMs);

                    } catch (IOException e) {
                        logger.error(e);
                        e.printStackTrace();

                    } catch (InterruptedException e) {
                        logger.error(e);
                        e.printStackTrace();
                    }
                }

            }
        }).start();

        (new Thread() {
            public void run() {

                String dbDriver = prop.getProperty("dbdriver");
                String connectionString = prop.getProperty("dbconnectionstring");

                logger.info("loading mysql driver");
                try {
                    Class.forName(dbDriver).newInstance();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }

                logger.info("Going to create connection");
                Connection connection = null;
                logger.debug("Connection String: " + connectionString);

                //write from the buffer to the database
                Queue buffer = statementBuffer.getBuffer();
                while(true) {
                    if (!buffer.isEmpty()) {

                        connection = DatabaseUtils.refreshConnection(connection, connectionString);

                        try {
                            Statement statement = connection.createStatement();
                            statement.executeUpdate((String)buffer.remove());
                        } catch (SQLException e) {
                            logger.error(e);
                            e.printStackTrace();
                        }
                    }
                    try {
                        Thread.currentThread().sleep(25);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }
}
