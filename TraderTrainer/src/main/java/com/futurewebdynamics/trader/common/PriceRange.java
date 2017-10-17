package com.futurewebdynamics.trader.common;

/**
 * Created by Charlie on 12/05/2017.
 */
public class PriceRange {

    private int maxPrice;
    private int minPrice;

    public PriceRange(int maxPrice, int minPrice) {
        this.maxPrice = maxPrice;
        this.minPrice = minPrice;
    }

    public int getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(int maxPrice) {
        this.maxPrice = maxPrice;
    }

    public int getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(int minPrice) {
        this.minPrice = minPrice;
    }
}
