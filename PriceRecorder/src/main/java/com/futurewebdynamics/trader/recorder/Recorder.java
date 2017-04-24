package com.futurewebdynamics.trader;

import org.apache.log4j.Logger;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.Properties;

public class Recorder {



    final static Logger logger = Logger.getLogger(Recorder.class);


    public static String getText(String url) throws Exception {
        URL website;
        website = new URL(url);
        URLConnection connection = website.openConnection();
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

        StringBuilder response = new StringBuilder();
        String inputLine;

        while ((inputLine = in.readLine()) != null)
            response.append(inputLine);

        in.close();

        return response.toString();
    }


    public static void main(String[] args) {

        //setup database connection

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

        StatementBuffer statementBuffer = new StatementBuffer();
        DatabaseConnector databaseConnector = new DatabaseConnector(connectionString, statementBuffer);
        DataPoller dataPoller = new DataPoller(statementBuffer);
        new Thread(dataPoller).start();
        new Thread(databaseConnector).start();

    }
}
