package com.calculator.tipcalculator.model;

import java.io.Serializable;

/**
 * Created by bharatkc on 2/9/14.
 */
public class Tip implements Serializable{

    private Integer id;
    private String place;
    private String date;
    private Double billAmount;
    private Double tipAmount;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Tip withId(Integer id) {
        setId(id);
        return this;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public Tip withPlace(String place) {
        setPlace(place);
        return this;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Tip withDate(String date) {
        setDate(date);
        return this;
    }

    public Double getBillAmount() {
        return billAmount;
    }

    public void setBillAmount(Double billAmount) {
        this.billAmount = billAmount;
    }

    public Tip withBillAmount(Double billAmount) {
        setBillAmount(billAmount);
        return this;
    }

    public Double getTipAmount() {
        return tipAmount;
    }

    public void setTipAmount(Double tipAmount) {
        this.tipAmount = tipAmount;
    }

    public Tip withTipAmount(Double tipAmount) {
        setTipAmount(tipAmount);
        return this;
    }
}
