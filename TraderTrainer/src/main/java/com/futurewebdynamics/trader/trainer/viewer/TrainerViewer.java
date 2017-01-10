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

                int msPerPixel = 60000;
                int pencePerPixel = 1;
                int minPence = 4000;
                int maxPence = 6000;

                //load ticker data
                BufferedReader br = null;
                String line = "";

                LinkedList<LongPoint> askPricePoints = new LinkedList<LongPoint>();
                LinkedList<LongPoint> bidPricePoints = new LinkedList<LongPoint>();
                LinkedList<LongPoint> balanceOfCompletedTradesPoints = new LinkedList<LongPoint>();
                LinkedList<LongPoint> balanceOfOpenTradesPoints = new LinkedList<LongPoint>();

                TrainerViewer ex = new TrainerViewer();

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



                GraphSurface graphSurface = new GraphSurface();
                graphSurface.setMinMs(askPricePoints.get(0).x);
                graphSurface.setMinPence(minPence);
                graphSurface.setMaxPence(maxPence);
                graphSurface.setPencePerPixel(pencePerPixel);
                graphSurface.setMsPerPixel(msPerPixel);
                graphSurface.setAskPriceData(askPricePoints);
                graphSurface.setBidPriceData(bidPricePoints);
                graphSurface.setBalanceOfCompletedTradesData(balanceOfCompletedTradesPoints);
                graphSurface.setBalanceOfOpenTradesData(balanceOfOpenTradesPoints);


                ex.add(graphSurface);


                ex.setVisible(true);


            }
        });
    }
}