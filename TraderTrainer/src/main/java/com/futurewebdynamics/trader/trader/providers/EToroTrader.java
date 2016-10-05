package com.futurewebdynamics.trader.trader.providers;


import com.futurewebdynamics.trader.positions.Position;
import com.futurewebdynamics.trader.positions.PositionStatus;
import com.futurewebdynamics.trader.positions.PositionsManager;
import com.futurewebdynamics.trader.sellconditions.ISellConditionProvider;
import com.futurewebdynamics.trader.sellconditions.providers.StopLoss;
import com.futurewebdynamics.trader.sellconditions.providers.StopLossPercentage;
import com.futurewebdynamics.trader.sellconditions.providers.TakeProfit;
import com.futurewebdynamics.trader.sellconditions.providers.TakeProfitPercentage;
import com.futurewebdynamics.trader.trader.ITrader;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by 52con on 15/04/2016.
 */
public class EToroTrader implements ITrader {

    private String username;
    private String password;
    private WebDriver driver;
    private PositionsManager manager;
    private int idCounter = 0;

    final static Logger logger = Logger.getLogger(EToroTrader.class);


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

        navigateTo("https://www.etoro.com/accounts/logout");
        navigateTo("https://www.etoro.com/login");

        try {
            Thread.currentThread().sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        driver.findElement(By.id("username")).clear();
        driver.findElement(By.id("username")).sendKeys(username);

        try {
            Thread.currentThread().sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        driver.findElement(By.id("password")).clear();
        driver.findElement(By.id("password")).sendKeys(password);

        try {
            Thread.currentThread().sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        driver.findElement(By.tagName("button")).click();
        try {
            Thread.currentThread().sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        waitForSpinny();



    }

    public boolean openPosition(Position position, boolean isShortTrade) {


        if (getAvailableFunds() < position.getQuantity()) {
            logger.info("Insufficient funds");
            return false;
        }

        StopLoss stopLoss = null;
        ISellConditionProvider condition = position.getSellConditionOfType(StopLoss.class);
        if (condition != null) stopLoss = (StopLoss)condition;

        StopLossPercentage stopLossPercentage = null;
        condition = position.getSellConditionOfType(StopLossPercentage.class);
        if (condition != null) stopLossPercentage = (StopLossPercentage)condition;

        if (stopLoss == null && stopLossPercentage == null) {
            logger.debug("No stop loss specified.");
            return false;
        }

        TakeProfit takeProfit = null;
        condition = position.getSellConditionOfType(TakeProfit.class);
        if (condition != null) takeProfit = (TakeProfit)condition;

        TakeProfitPercentage takeProfitPercentage = null;
        condition = position.getSellConditionOfType(TakeProfitPercentage.class);
        if (condition != null) takeProfitPercentage = (TakeProfitPercentage)condition;

        if (takeProfit == null && takeProfitPercentage == null) {
            logger.debug("No take profit specified.");
            return false;
        }

        navigateTo("https://www.etoro.com/markets/oil");

        driver.findElement(By.className("head-action-button")).click();

        //buy button
        driver.findElement(By.className("execution-head-buttons")).findElements(By.tagName("button")).get(1).click();


        //leverage
        List<WebElement> tabs = driver.findElements(By.tagName("tabstitles")).get(1).findElements(By.tagName("tabtitle"));
        tabs.get(1).findElement(By.tagName("a")).click();

        //x1 leverage
        driver.findElements(By.className("risk-itemlevel")).get(0).click();

        //stop loss
        tabs.get(0).findElement(By.tagName("a")).click();

        WebElement textBox = driver.findElement(By.className("execution-main")).findElement(By.className("tab-box-wrapper")).findElement(By.className("stepper-value"));

        if (stopLoss != null) {
            textBox.clear();
            textBox.sendKeys("$" + (position.getTargetOpenPrice() - stopLoss.getDecrease())/100.0);
        } else if (stopLossPercentage != null) {
            textBox.clear();
            int targetOpenPrice = position.getTargetOpenPrice();
            double decreasePercentage = stopLossPercentage.getDecreasePercentage();
            double stopLossToEnter = (targetOpenPrice - (targetOpenPrice * decreasePercentage/100.0))/100.0;
            textBox.sendKeys("$" + stopLossToEnter);
        }

        //take profit
        tabs.get(2).findElement(By.tagName("a")).click();

        textBox = driver.findElement(By.className("execution-main")).findElement(By.className("tab-box-wrapper")).findElement(By.className("stepper-value"));

        if (takeProfit != null) {
            textBox.clear();
            textBox.sendKeys("$" + (position.getTargetOpenPrice() + takeProfit.getIncrease())/100.0);
        } else if (takeProfitPercentage != null) {
            textBox.clear();
            int targetOpenPrice = position.getTargetOpenPrice();
            double increasePercentage = takeProfitPercentage.getIncreasePercentage();
            double takeProfitToEnter = (targetOpenPrice + (targetOpenPrice * increasePercentage/100.0))/100.0;
            textBox.sendKeys("$" + takeProfitToEnter);
        }



        return true;
    }

    public boolean checkPosition(Position position) {
        return true;
    }

    public boolean closePosition(Position position) {
        navigateTo("https://www.etoro.com/portfolio/oil");

        if (!driver.getCurrentUrl().equals("https://www.etoro.com/portfolio/oil")) {
            logger.info("There are no oil positions, current url is " + driver.getCurrentUrl());

            return false;
        }

        List<WebElement> rows = driver.findElements(By.className("w-portfolio-table-row"));

        ListIterator izzy = rows.listIterator();

        while (izzy.hasNext()) {
            WebElement row = (WebElement) izzy.next();

            if (!izzy.hasNext()) break; //skip the footer
            String openTime = row.findElement(By.className("i-ptc-01")).findElement(By.className("i-portfolio-table-hat-fullname")).getText();

            Calendar cal = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.ENGLISH);
            try {
                cal.setTime(sdf.parse(openTime));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            String quantity = row.findElement(By.className("i-ptc-02")).getText().replace("$", "");
            String openPrice = row.findElement(By.className("i-ptc-04")).getText().replace(".", "");

            if (position.getTimeOpened().getTimeInMillis() == cal.getTimeInMillis() && position.getQuantity() == Integer.parseInt(quantity) && position.getActualOpenPrice() == Integer.parseInt(openPrice)) {

                row.findElement(By.className("stop")).click();
                waitForSpinny();

                driver.findElement(By.id("uidialog6")).findElement(By.className("red")).click();

                //this is the one
                position.setStatus(PositionStatus.CLOSED);
                break;

            }
        }



        return true;
    }

    public void navigateTo(String url) {
        driver.get(url);
        waitForSpinny();

        try {
            Thread.currentThread().sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //close any popup
        try {
            driver.findElement(By.className("inmplayer-popover-close-button")).click();
        } catch(Exception e) {}
    }

    public int getAvailableFunds() {
        navigateTo("https://www.etoro.com/portfolio/oil");

        String balance = driver.findElement(By.className("balance")).findElement(By.className("w-footer-unit-value")).getText().replace("$", "");

        logger.debug("Available funds are: " + balance);

        return (int)(Double.parseDouble(balance) * 100);
    }

    public void getPositions(PositionsManager manager, Collection<ISellConditionProvider> defaultSellPositions) {

        navigateTo("https://www.etoro.com/portfolio/oil");

        if (!driver.getCurrentUrl().equals("https://www.etoro.com/portfolio/oil")) {
            logger.info("There are no oil positions, current url is " + driver.getCurrentUrl());

            return;
        }

        List<WebElement> rows = driver.findElements(By.className("w-portfolio-table-row"));

        ListIterator izzy = rows.listIterator();

        while (izzy.hasNext()) {
            WebElement row = (WebElement) izzy.next();

            if (!izzy.hasNext()) break; //skip the footer
            String openTime = row.findElement(By.className("i-ptc-01")).findElement(By.className("i-portfolio-table-hat-fullname")).getText();

            String quantity = row.findElement(By.className("i-ptc-02")).getText();
            String openPrice = row.findElement(By.className("i-ptc-04")).getText();

            String stopLoss = row.findElement(By.className("i-ptc-07")).findElement(By.tagName("span")).getText();
            String takeProfit = row.findElement(By.className("i-ptc-08")).findElement(By.tagName("span")).getText();

            logger.info(String.format("Time %1s  Quantity %2s  Open Price %3s  Stop Loss %4s  Take Profit %5s", openTime, quantity, openPrice, stopLoss, takeProfit));

            Position p = new Position();
            p.setStatus(PositionStatus.OPEN);
            p.setActualOpenPrice(Integer.parseInt(openPrice.replace(".","")));
            p.setQuantity((int)Math.round(Double.parseDouble(quantity.replace("$", "").replace(".", ""))));
            p.addSellCondition(new StopLoss(p.getActualOpenPrice() - Integer.parseInt(stopLoss.replace(".", ""))));
            p.addSellCondition(new TakeProfit(p.getActualOpenPrice() - Integer.parseInt(takeProfit.replace(".", ""))));
            p.setUniqueId(++idCounter);

            Calendar cal = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.ENGLISH);
            try {
                cal.setTime(sdf.parse(openTime));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            p.setTimeOpened(cal);

            manager.addExistingPosition(p);

        }

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
