package com.futurewebdynamics.trader.trainer.viewer;

import java.awt.*;

/**
 * Created by Charlie on 09/01/2017.
 */
public class LongPoint {

    public LongPoint(long x, long y) {
        this.x = x;
        this.y = y;
    }

    public LongPoint (long x, long y, String id, Color labelColor) {
        this.x = x;
        this.y = y;
        this.id = id;
        this.labelColor = labelColor;
    }

    public long x;
    public long y;
    private String id;
    private String tooltip;
    private Color labelColor;

    public void setTooltip(String tooltip) {
        this.tooltip = tooltip;
    }

    public String getTooltip() {
        return tooltip;
    }

    public Color getLabelColor() {
        return labelColor;
    }

    public void setLabelColor(Color labelColor) {
        this.labelColor = labelColor;
    }

    public String getId() {
        return id;
    }
}
