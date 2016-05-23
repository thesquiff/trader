package com.futurewebdynamics.trader.trader.providers;


import com.futurewebdynamics.trader.positions.Position;
import com.futurewebdynamics.trader.trader.ITrader;
import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Properties;

/**
 * Created by 52con on 15/04/2016.
 */
public class EToroTrader implements ITrader {

    private String username;
    private String password;
    private WebDriver driver;

    public EToroTrader(String propertiesFile) {
        init(propertiesFile);
    }

    public void init(String propertiesFile) {
        Properties prop = new Properties();
        InputStream input = null;

        try {
            input = new FileInputStream(propertiesFile);

            prop.load(input);

            username = prop.getProperty("etorousername");
            password = prop.getProperty("etoropassword");

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.exit(1);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        driver = new FirefoxDriver();
    }

    public void login() {

        driver.get("https://www.etoro.com/accounts/logout");
        driver.get("https://www.etoro.com/login");

        /*try {
            Thread.currentThread().sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/

        driver.findElement(By.id("username")).clear();
        driver.findElement(By.id("username")).sendKeys(username);

        driver.findElement(By.id("password")).clear();
        driver.findElement(By.id("password")).sendKeys(password);

        try {
            Thread.currentThread().sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        driver.findElement(By.tagName("button")).click();


        //close any popup

        driver.findElement(By.className("inmplayer-popover-close-button")).click();

    }

    public boolean openPosition(Position position) {
        return true;
    }

    public boolean checkPosition(Position position) {
        return true;
    }

    public boolean closePosition(Position position) {
        return true;
    }

    public ArrayList<Position> getPositions() {


        driver.get("https://www.etoro.com/portfolio/oil");

        waitForSpinny();

        List<WebElement> rows = driver.findElements(By.className("w-portfolio-table-row"));

        ListIterator izzy = rows.listIterator();

        while (izzy.hasNext()) {
            WebElement row = (WebElement) izzy.next();

            String openTime = row.findElement(By.className("i-ptc-01")).findElement(By.className("i-portfolio-table-hat-fullname")).getText();

            String quantity = row.findElement(By.className("i-ptc-03")).getText();
            String openPrice = row.findElement(By.className("i-ptc-04")).getText();

            String stopLoss = row.findElement(By.className("i-ptc-07")).findElement(By.tagName("span")).getText();
            String takeProfit = row.findElement(By.className("i-ptc-08")).findElement(By.tagName("span")).getText();

            System.out.println(String.format("Time {0}  Quantity {1}  Open Price {2}  Stop Loss {3}  Take Profit {4}", openTime, quantity, openPrice, stopLoss, takeProfit));

        }


        return null;
    }

    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    private boolean waitForSpinny() {
        while (driver.findElement(By.className("isp-2")).isDisplayed()) {
            try {
                Thread.currentThread().sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return true;
    }

    public static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
        InputStream is = new URL(url).openStream();
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsonText = readAll(rd);
            JSONObject json = new JSONObject(jsonText);
            return json;
        } finally {
            is.close();
        }
    }
}
