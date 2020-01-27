package com.mh.mytransaction;

public class Details_list {

    String bom_name;
    String uom;
    double qty;

    public Details_list(String bom_name, String uom, double qty) {
        this.bom_name = bom_name;
        this.uom = uom;
        this.qty = qty;
    }

    public String getBom_name() {
        return bom_name;
    }

    public void setBom_name(String bom_name) {
        this.bom_name = bom_name;
    }

    public String getUom() {
        return uom;
    }

    public void setUom(String uom) {
        this.uom = uom;
    }

    public double getQty() {
        return qty;
    }

    public void setQty(Double qty) {
        this.qty = qty;
    }
}
