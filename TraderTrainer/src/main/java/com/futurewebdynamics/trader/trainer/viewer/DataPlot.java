package com.futurewebdynamics.trader.trainer.viewer;

import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedList;
import java.util.ListIterator;

/**
 * Created by Charlie on 06/01/2017.
 */
public class DataPlot extends JFrame
{

    final static Logger logger = Logger.getLogger(DataPlot.class);

    private int msPerPixel;
    private double pencePerPixel;

    private int minPence;

    private long minMs;

    private LinkedList<LongPoint> data;

    private Rectangle bounds;

    private PlotType plotType;

    public DataPlot(int msPerPixel, double pencePerPixel, int minPence, int maxPence, long minMs, PlotType plotType) {
        this.msPerPixel = msPerPixel;
        this.pencePerPixel = pencePerPixel;
        this.minPence = minPence;
        this.minMs = minMs;
        this.plotType = plotType;
    }

    public void paint(Graphics2D g, Dimension size, int borderWidthPx, Rectangle bounds, Color defaultColour) {

        long minTimestampInWindow = bounds.x * msPerPixel + minMs;
        long maxTimestampInWindow = minTimestampInWindow + bounds.width * msPerPixel;

        long minPenceInWindow = (long)((size.getHeight() - borderWidthPx - bounds.y) * pencePerPixel) + minPence;
        long maxPenceInWindow = minPenceInWindow + (long)(bounds.height * pencePerPixel);


        if (data == null ) {
            return;
        }
        ListIterator<LongPoint> izzy = data.listIterator();

        int lastX = 0;
        int lastY = 0;

        g.setColor(defaultColour);

        while(izzy.hasNext()) {

            LongPoint p = izzy.next();

            if (p.x < minTimestampInWindow) continue;
            if (p.x > maxTimestampInWindow) break;

            int xCoordinate = (int)Math.round((p.x - minMs)/(double)msPerPixel) + borderWidthPx;
            int yCoordinate = (int)Math.round(size.height - borderWidthPx - (p.y - minPence) / pencePerPixel);

            if (plotType == PlotType.DATA) {
                g.drawLine(lastX, lastY, xCoordinate, yCoordinate);
            }

            if (plotType == PlotType.ACTIVITY) {
                g.setColor(defaultColour);
                g.drawLine(xCoordinate, yCoordinate, xCoordinate, yCoordinate+50);

                String tooltip = p.getTooltip();

                if (tooltip == null){
                    tooltip = p.getId();
                }

                g.setColor(p.getLabelColor());
                g.drawString(tooltip, xCoordinate-3*tooltip.length(), yCoordinate + 62);
            }

            lastX = xCoordinate;
            lastY = yCoordinate;

        }

    }

    public void setData(LinkedList<LongPoint> data) {
        this.data = data;
    }
}
