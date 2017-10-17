package com.futurewebdynamics.trader.postanalysers.providers;

import com.futurewebdynamics.trader.common.NormalisedPriceInformation;
import com.futurewebdynamics.trader.common.PriceRange;
import com.futurewebdynamics.trader.common.PriceType;
import com.futurewebdynamics.trader.common.TimeNormalisedDataCache;
import com.futurewebdynamics.trader.positions.Position;
import com.futurewebdynamics.trader.positions.PositionStatus;
import com.futurewebdynamics.trader.postanalysers.IPostAnalyser;
import org.apache.log4j.Logger;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Charlie on 12/05/2017.
 */
public class PositionLifetimeChart implements IPostAnalyser {

    final static Logger logger = Logger.getLogger(PositionLifetimeChart.class);

    private TimeNormalisedDataCache dataCache;
    private double msPerPixel;
    private double pencePerPixel;
    private int lookbackSeconds;
    private double takeProfitPercentage;
    private double stopLossPercentage;

    private int MAJOR_PRICE_TICK = 5;
    private int MAJOR_TIME_TICK = 30000;

    private String outputFolder;


    public PositionLifetimeChart(TimeNormalisedDataCache dataCache, int lookbackSeconds, double msPerPixel, double pencePerPixel, String outputFolder, double takeProfitPercentage, double stopLossPercentage) {
        this.dataCache = dataCache;
        this.msPerPixel = msPerPixel;
        this.pencePerPixel = pencePerPixel;
        this.outputFolder = outputFolder;
        this.lookbackSeconds = lookbackSeconds;
        this.takeProfitPercentage = takeProfitPercentage;
        this.stopLossPercentage = stopLossPercentage;
    }

    public void AnalysePosition(Position position) {

        //create a large buffered image to hold the bitmap
        Graphics2D g = null;
        BufferedImage img = null;
        NormalisedPriceInformation[] dataInRange = null;
        try {
            try {
                logger.debug("Going to create position lifetimechart for position " + position.getUniqueId() + " in output folder: " + outputFolder);

                long minTime = position.getTimeOpened().getTimeInMillis() - lookbackSeconds * 1000;
                long maxTime = position.getStatus() == PositionStatus.CLOSED ? position.getTimeClosed().getTimeInMillis() : System.currentTimeMillis();

                long timeWindow = maxTime - minTime;

                PriceRange range = dataCache.getPriceRange(minTime, maxTime, PriceType.BID_PRICE);

                int minPrice = range.getMinPrice();
                int maxPrice = range.getMaxPrice();

                logger.debug("timeWindow: " + timeWindow + ", minPrice: " + minPrice + ", maxPrice: " + maxPrice);

                int imgWidth = (int) (timeWindow / this.msPerPixel) + 150;
                int imgHeight = (int) ((range.getMaxPrice() - range.getMinPrice()) / pencePerPixel) + 125;

                logger.debug("attempting to create image width " + imgWidth + " height" + imgHeight);

                img = new BufferedImage(imgWidth, imgHeight, BufferedImage.TYPE_INT_RGB);



                //origin at top left of image
                dataInRange = dataCache.getDataInRange(minTime, maxTime);

                g = img.createGraphics();

                g.setFont(new Font("Serif", Font.BOLD, 12));

                //set background color
                g.setColor(Color.BLACK);
                g.drawRect(0, 0, img.getWidth(), img.getHeight());

                //draw x axis
                g.setColor(Color.white);
                g.drawLine(0, img.getHeight() - 100, img.getWidth(), img.getHeight() - 100);
                //draw y axis
                g.drawLine(100, 0, 100, img.getHeight() - 100);

                //draw bid prices
                g.setColor(Color.green);
                int lastx = 0;
                int lasty = 0;
                for (int i = 0; i < dataInRange.length; i++) {
                    if (dataInRange[i].isEmpty()) continue;
                    int price = dataInRange[i].getBidPrice();
                    if (price == 0) continue;

                    int x = (int) (100 + (dataInRange[i].getCorrectedTimestamp() - position.getTimeOpened().getTimeInMillis() + lookbackSeconds * 1000) / msPerPixel);
                    int y = img.getHeight() - 100 - (int) ((price- minPrice) / pencePerPixel);
                    if (i == 0) {
                        g.drawLine(x, y, x, y);
                    } else {
                        g.drawLine(lastx, lasty, x, y);
                    }
                    lastx = x;
                    lasty = y;
                }

                g.setColor(Color.MAGENTA);
                int takeProfitPence = (int) (position.getActualOpenPrice() * (1.0 + this.takeProfitPercentage / 100.0));
                int y = img.getHeight() - 100 - (int) ((takeProfitPence - minPrice) / pencePerPixel);
                //draw take profit price
                g.drawLine(100, y, img.getWidth(), y);

                g.setColor(Color.CYAN);
                int stopLossPence = (int) (position.getActualOpenPrice() * (1.0 - this.stopLossPercentage / 100.0));
                y = img.getHeight() - 100 - (int) ((stopLossPence - minPrice) / pencePerPixel);
                //draw stop loss price
                g.drawLine(100, y, img.getWidth(), y);

                //draw ask price
                g.setColor(Color.red);
                for (int i = 0; i < dataInRange.length; i++) {

                    if (dataInRange[i].isEmpty()) continue;

                    int price = dataInRange[i].getAskPrice();

                    if (price == 0) continue;

                    int x = (int) (100 + (dataInRange[i].getCorrectedTimestamp() - position.getTimeOpened().getTimeInMillis() + lookbackSeconds * 1000) / msPerPixel);
                    y = img.getHeight() - 100 - (int) ((price - minPrice) / pencePerPixel);

                    if (i == 0) {
                        g.drawLine(x, y, x, y);
                    } else {
                        g.drawLine(lastx, lasty, x, y);
                    }

                    lastx = x;
                    lasty = y;
                }

                //draw buy price
                g.setColor(Color.yellow);
                int x = (int) ((position.getTimeOpened().getTimeInMillis() - minTime) / (double) msPerPixel) + 100;
                g.drawLine(x, 0, x, img.getHeight() - 100);


                if (position.getStatus() == PositionStatus.CLOSED) {
                    //draw sell price
                    g.setColor(Color.blue);
                    x = (int) ((maxTime - minTime) / (double) msPerPixel) + 100;
                    g.drawLine(x, 0, x, img.getHeight() - 100);
                }

                //draw major price ticks
                Stroke dashed = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{9}, 0);
                g.setStroke(dashed);
                g.setColor(Color.lightGray);
                for (int yp = minPrice + MAJOR_PRICE_TICK; yp < maxPrice + MAJOR_PRICE_TICK; yp += MAJOR_PRICE_TICK) {
                    int yCoordinate = img.getHeight() - 100 - (int) ((yp - minPrice) / pencePerPixel);

                    g.drawLine(75, yCoordinate, img.getWidth(), yCoordinate);
                    g.drawString(String.valueOf(yp), 50, yCoordinate + 2);
                }

                Calendar tickDate = Calendar.getInstance();
                //draw major timestamp ticks
                SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/mm/YYYY");
                g.setColor(Color.lightGray);
                for (long xt = minTime; xt < maxTime; xt += MAJOR_TIME_TICK) {
                    int xCoord = 100 + (int) ((xt - minTime) / (double) msPerPixel);

                    g.drawLine(xCoord, 0, xCoord, img.getHeight() - 75);

                    tickDate.setTimeInMillis(xt);

                    String time = timeFormat.format(tickDate.getTime());
                    String date = dateFormat.format(tickDate.getTime());

                    g.drawString(time, xCoord - 15, img.getHeight() - 55);
                    g.drawString(date, xCoord - 18, img.getHeight() - 40);
                }

                SimpleDateFormat dateTimeFormat = new SimpleDateFormat("dd/MM/yy HH:mm:ss");

                String openDate = dateTimeFormat.format(position.getTimeOpened().getTime());
                String closeDate = dateTimeFormat.format(maxTime);

                String info = openDate + " @ " + position.getActualOpenPrice() + " -> " + closeDate + " @ " + position.getActualSellPrice();

                g.drawString(info, 0, img.getHeight() - 15);

                String additionalFileInfo = position.getStatus() == PositionStatus.CLOSED ? "" : "_OPEN";

                File f = new File(outputFolder + File.separator + position.getUniqueId() + additionalFileInfo + ".png");

                logger.info("Saving PositionLifeTimeChart to " + f.getAbsolutePath());
                ImageIO.write(img, "PNG", f);
            } catch (OutOfMemoryError e) {
                logger.error("Out of memory when creating positionLifeTimeChart");
            } finally {
                if (g!= null) {
                    g.dispose();
                }
                if (img != null) {
                    img.flush();
                    img = null;
                }
                dataInRange = null;
                System.gc();
            }
        } catch (Exception e) {
            logger.error("An error occurred creating the positionlifetimechart.", e);
            e.printStackTrace();
        }
    }
}
