package com.futurewebdynamics.trader.trainer;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by 52con on 14/04/2016.
 */
public class TraderTrainer {

    public static void main(String args[]) {

        Properties prop = new Properties();
        InputStream input = null;
        String dbHost = "";
        String dbUsername = "";
        String dbPassword = "";

        try {
            input = new FileInputStream(args[0]);

            prop.load(input);

            dbHost = prop.getProperty("dbhost");
            dbUsername = prop.getProperty("dbusername");
            dbPassword = prop.getProperty("dbpassword");

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.exit(1);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        String connectionString = "jdbc:mysql://" + dbHost + "/trader?user=" + dbUsername + "&password=" + dbPassword;

        DatabaseCache databaseCache = new DatabaseCache(connectionString);
        databaseCache.loadData();

        TimeNormalisedDataCache dataCache = new TimeNormalisedDataCache(databaseCache.getCache());





    }



}
