package com.vitkaloff.benzorro;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Price {

    @SerializedName("fuel")
    @Expose
    private Integer fuel;
    @SerializedName("price")
    @Expose
    private Double price;
    @SerializedName("curr")
    @Expose
    private String curr;

    public Price(int fuel, double price, String curr) {
    }

    public Integer getFuel() {
        return fuel;
    }

    public void setFuel(Integer fuel) {
        this.fuel = fuel;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getCurr() {
        return curr;
    }

    public void setCurr(String curr) {
        this.curr = curr;
    }
}