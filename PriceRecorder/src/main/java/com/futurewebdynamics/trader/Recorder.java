package com.futurewebdynamics.trader;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Recorder {

    public final static String CLOSE_PATTERN = "\"Close\":(?<price>[0-9\\.]*)\\}";

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

            input = new FileInputStream("C:\\Users\\52con\\Documents\\trader\\PriceRecorder\\src\\main\\resources\\Recorder.properties");

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


        try {
            System.out.println("loading mysql driver");
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            System.out.println("jdbc:mysql://" + dbHost + "/price?user=" + dbUsername + "&password=" + dbPassword);
            System.out.println("Going to create connection");
            Connection connection = DriverManager.getConnection("jdbc:mysql://" + dbHost + "/trader?user=" + dbUsername + "&password=" + dbPassword);

            System.out.println("Database connected!");

            Pattern p = Pattern.compile(CLOSE_PATTERN);

            //record data indefinitely
            while(true) {

                try {
                    URL url = new URL("https://candle.etoro.com/candles/desc.json/OneMinute/2/17?1459765157852");

                    InputStream inputStream = url.openStream();         // throws an IOException

                    DataInputStream dataInputStream = new DataInputStream(new BufferedInputStream(inputStream));

                    String s = dataInputStream.readLine();

                    Matcher matcher = p.matcher(s);

                    if (matcher.find()) {
                        String priceStr = matcher.group("price");

                        System.out.println(s);

                        Statement statement = connection.createStatement();
                        System.out.println("INSERT INTO price (timestamp, price) VALUES(CURRENT_TIMESTAMP, " + priceStr.replace(".", "") + ")");
                        statement.executeUpdate("INSERT INTO price (timestamp, price) VALUES(CURRENT_TIMESTAMP, " + priceStr.replace(".", "") + ")");

                    }

                    Thread.currentThread().sleep(60000);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        } catch (SQLException e) {
            throw new IllegalStateException("Cannot connect the database - oh no!", e);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            System.err.println("Database driver not found");
            e.printStackTrace();
        }

    }
}
