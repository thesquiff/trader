package com.futurewebdynamics.trader.common;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Charlie on 22/08/2016.
 */
public class RestHelper {

    public static String GetJson(String targetUrl, String token) {

        StringBuilder result = new StringBuilder();
        URL url = null;
        try {
            url = new URL(targetUrl);
            HttpURLConnection conn = null;
            conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("GET");

            conn.setRequestProperty("Authorization", "Bearer " + token);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Connection", "Keep-Alive");

            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }
            rd.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result.toString();
    }

    public static String PostJson(String targetUrl, String token, String payload) {

        StringBuilder result = new StringBuilder();
        URL url = null;
        try {
            url = new URL(targetUrl);
            HttpURLConnection conn = null;
            conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");

            conn.setRequestProperty("Authorization", "Bearer " + token);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setDoOutput(true);

            DataOutputStream wr = new DataOutputStream( conn.getOutputStream() );
            wr.write( payload.getBytes() );

            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;

            while ((line = rd.readLine()) != null) {
                result.append(line);
            }
            rd.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result.toString();
    }

    public static String PutJson(String targetUrl, String token, String payload) {

        StringBuilder result = new StringBuilder();
        URL url = null;
        try {
            url = new URL(targetUrl);
            HttpURLConnection conn = null;
            conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("PUT");

            conn.setRequestProperty("Authorization", "Bearer " + token);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setDoOutput(true);

            DataOutputStream wr = new DataOutputStream( conn.getOutputStream() );
            wr.write( payload.getBytes() );

            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;

            while ((line = rd.readLine()) != null) {
                result.append(line);
            }
            rd.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result.toString();
    }
}
