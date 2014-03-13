package com.calculator.tipcalculator.model;

import java.io.File;

/**
 * Created by bharatkc on 2/17/14.
 */
public class FilterData {
    private Double billAmount;
    private Double tipAmount;
    private Double totalAmount;

    public Double getBillAmount() {
        return billAmount;
    }

    public void setBillAmount(Double billAmount) {
        this.billAmount = billAmount;
    }

    public FilterData withBillAmount(Double billAmount) {
        setBillAmount(billAmount);
        return this;
    }

    public Double getTipAmount() {
        return tipAmount;
    }

    public void setTipAmount(Double tipAmount) {
        this.tipAmount = tipAmount;
    }

    public FilterData withTipAmount(Double tipAmount) {
        setTipAmount(tipAmount);
        return this;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public FilterData withTotalAmount(Double totalAmount) {
        setTotalAmount(totalAmount);
        return this;
    }
}
