package com.mh.mytransaction;

public class Performance_list {

    String xValues;
    float yValues;

    public Performance_list(String xValues, float yValues) {
        this.xValues = xValues;
        this.yValues = yValues;
    }

    public String getxValues() {
        return xValues;
    }

    public void setxValues(String xValues) {
        this.xValues = xValues;
    }

    public float getyValues() {
        return yValues;
    }

    public void setyValues(float yValues) {
        this.yValues = yValues;
    }
}
