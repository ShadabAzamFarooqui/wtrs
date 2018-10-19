package com.android.app.watersupply.bean;

/**
 * Created by Beryl on 03-Oct-18.
 */

public class Water {
    String name;
    Double rate;
    Integer qty;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getRate() {
        return rate;
    }

    public void setRate(Double rate) {
        this.rate = rate;
    }

    public Integer getQty() {
        return qty;
    }

    public void setQty(Integer qty) {
        this.qty = qty;
    }
}
