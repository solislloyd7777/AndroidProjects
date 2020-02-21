package com.mh.mytransaction;

import java.io.Serializable;

public class Temp_content implements Serializable {

    String prod,uom,isnega,isactive,iscomputed,isgrab;
    int id,fav;
    double price,qty;

    public Temp_content(String prod, String uom, String isnega, String isactive, int id, double price,String iscomputed,double qty,int fav,String isgrab) {
        this.prod = prod;
        this.uom = uom;
        this.isnega = isnega;
        this.isactive = isactive;
        this.id = id;
        this.price = price;
        this.iscomputed=iscomputed;
        this.qty=qty;
        this.fav=fav;
        this.isgrab=isgrab;
    }

    public String getProd() {
        return prod;
    }

    public void setProd(String prod) {
        this.prod = prod;
    }

    public String getUom() {
        return uom;
    }

    public void setUom(String uom) {
        this.uom = uom;
    }

    public String getIsnega() {
        return isnega;
    }

    public void setIsnega(String isnega) {
        this.isnega = isnega;
    }

    public String getIsactive() {
        return isactive;
    }

    public void setIsactive(String isactive) {
        this.isactive = isactive;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getIscomputed() {
        return iscomputed;
    }

    public void setIscomputed(String iscomputed) {
        this.iscomputed = iscomputed;
    }

    public double getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public int getFav() {
        return fav;
    }

    public void setFav(int fav) {
        this.fav = fav;
    }

    public void setQty(double qty) {
        this.qty = qty;
    }

    public String getIsgrab() {
        return isgrab;
    }

    public void setIsgrab(String isgrab) {
        this.isgrab = isgrab;
    }
}
