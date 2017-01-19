package com.futurewebdynamics.trader.trainer.viewer;

import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.LinkedList;

public class TrainerViewer extends JFrame {

    final static Logger logger = Logger.getLogger(TrainerViewer.class);

    public TrainerViewer() {
        initUI();
    }

    private void initUI() {
        this.setExtendedState( this.getExtendedState()|JFrame.MAXIMIZED_BOTH );

        setTitle("Simple Java 2D example");
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                String dataFolder = args[0];

                int msPerPixel = 1000;
                double pencePerPixel = 0.1;
                int minPence = 4500;
                int maxPence = 6000;

                //load ticker data
                BufferedReader br = null;
                String line = "";

                LinkedList<LongPoint> askPricePoints = new LinkedList<LongPoint>();
                LinkedList<LongPoint> bidPricePoints = new LinkedList<LongPoint>();
                LinkedList<LongPoint> balanceOfCompletedTradesPoints = new LinkedList<LongPoint>();
                LinkedList<LongPoint> balanceOfOpenTradesPoints = new LinkedList<LongPoint>();
                LinkedList<LongPoint> shortOpenPoints = new LinkedList<LongPoint>();
                LinkedList<LongPoint> shortClosePoints = new LinkedList<LongPoint>();
                LinkedList<LongPoint> longOpenPoints = new LinkedList<LongPoint>();
                LinkedList<LongPoint> longClosePoints = new LinkedList<LongPoint>();

                TrainerViewer ex = new TrainerViewer();

                //read ticker data
                try {

                    br = new BufferedReader(new FileReader(args[0] + File.separator + "ticker.csv"));
                    while ((line = br.readLine()) != null) {
                        // use comma as separator
                        String[] tokens = line.split(",");

                        long timestamp = Long.valueOf(tokens[0]);

                        askPricePoints.add(new LongPoint(timestamp,Long.valueOf(tokens[1])));
                        bidPricePoints.add(new LongPoint(timestamp,Long.valueOf(tokens[2])));
                        balanceOfCompletedTradesPoints.add(new LongPoint(timestamp,Long.valueOf(tokens[3])));
                        balanceOfOpenTradesPoints.add(new LongPoint(timestamp,Math.round(Double.valueOf(tokens[4]))));
                    }

                } catch(Exception e) {
                    logger.error("An error occured reading ticker.csv", e);
                }

                //read activity data
                try {

                    br = new BufferedReader(new FileReader(args[0] + File.separator + "activity.csv"));
                    br.readLine(); //discard the header line
                    while ((line = br.readLine()) != null) {
                        // use comma as separator
                        String[] tokens = line.split(",");

                        long timestamp = Long.valueOf(tokens[0]);

                        String activityType = tokens[1];
                        switch (activityType.toUpperCase()) {
                            case "SHORT":
                                shortOpenPoints.add(new LongPoint(Long.valueOf(tokens[2]),Long.valueOf(tokens[3]), "S" + tokens[0], Color.BLUE));

                                int profit = Integer.valueOf(tokens[3]) - Integer.valueOf(tokens[5]);

                                String tooltip  = "S" + tokens[0] + "(" + String.valueOf(profit) + ")";

                                LongPoint lp = new LongPoint(Long.valueOf(tokens[4]),Long.valueOf(tokens[5]), "S" + tokens[0], profit > 0 ? Color.GREEN : Color.RED);
                                lp.setTooltip(tooltip);
                                shortClosePoints.add(lp);


                                break;
                            case "LONG":

                                profit = Integer.valueOf(tokens[5]) - Integer.valueOf(tokens[3]);

                                tooltip  = "L" + tokens[0] + "(" + String.valueOf(profit) + ")";

                                longOpenPoints.add(new LongPoint(Long.valueOf(tokens[2]),Long.valueOf(tokens[3]), "L" + tokens[0], Color.MAGENTA));

                                lp = new LongPoint(Long.valueOf(tokens[4]),Long.valueOf(tokens[5]), "L" + tokens[0], profit > 0 ? Color.GREEN : Color.RED);
                                lp.setTooltip(tooltip);
                                longClosePoints.add(lp);
                        }

                    }

                } catch(Exception e) {
                    logger.error("An error occured reading activity.csv", e);
                }


                GraphSurface graphSurface = new GraphSurface(msPerPixel, pencePerPixel, minPence, maxPence, askPricePoints.getFirst().x);

                graphSurface.setMaxMs(askPricePoints.getLast().x);
                graphSurface.setAskPriceData(askPricePoints);
                graphSurface.setBidPriceData(bidPricePoints);
                graphSurface.setBalanceOfCompletedTradesData(balanceOfCompletedTradesPoints);
                graphSurface.setBalanceOfOpenTradesData(balanceOfOpenTradesPoints);
                graphSurface.setShortOpenData(shortOpenPoints);
                graphSurface.setShortCloseData(shortClosePoints);
                graphSurface.setLongOpenData(longOpenPoints);
                graphSurface.setLongCloseData(longClosePoints);


                JScrollPane scrollPane = new JScrollPane(graphSurface);
                scrollPane.getHorizontalScrollBar().setUnitIncrement(1000);
                scrollPane.getVerticalScrollBar().setUnitIncrement(1000);
                scrollPane.getViewport().setDoubleBuffered(true);

                ex.add(scrollPane);
                ex.setVisible(true);
            }
        });
    }
}