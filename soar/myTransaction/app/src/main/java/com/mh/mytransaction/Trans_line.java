package com.mh.mytransaction;

import java.io.Serializable;

public class Trans_line implements Serializable {
    int template_id;
    String product,uom,isnegative,is_computed,changed,isgrab;
    double price,price_temp,qty,qty_temp;
    int fav;

    public Trans_line(int template_id, String product, String uom, double price, double qty,String isnegative,String is_computed,double qty_temp,double price_temp,String changed,int fav,String isgrab) {
        this.template_id = template_id;
        this.qty = qty;
        this.product = product;
        this.uom = uom;
        this.price = price;
        this.isnegative=isnegative;
        this.is_computed=is_computed;
        this.qty_temp=qty_temp;
        this.price_temp=price_temp;
        this.changed=changed;
        this.fav=fav;
        this.isgrab=isgrab;
    }


    public int getTemplate_id() {
        return template_id;
    }

    public void setTemplate_id(int template_id) {
        this.template_id = template_id;
    }

    public double getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getUom() {
        return uom;
    }

    public void setUom(String uom) {
        this.uom = uom;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getIsnegative() {
        return isnegative;
    }

    public void setIsnegative(String isnegative) {
        this.isnegative = isnegative;
    }

    public String getIs_computed() {
        return is_computed;
    }

    public void setIs_computed(String is_computed) {
        this.is_computed = is_computed;
    }

    public double getQty_temp() {
        return qty_temp;
    }

    public void setQty_temp(int qty_temp) {
        this.qty_temp = qty_temp;
    }

    public String getChanged() {
        return changed;
    }

    public void setChanged(String changed) {
        this.changed = changed;
    }

    public double getPrice_temp() {
        return price_temp;
    }

    public void setPrice_temp(double price_temp) {
        this.price_temp = price_temp;
    }

    public void setQty(double qty) {
        this.qty = qty;
    }

    public void setQty_temp(double qty_temp) {
        this.qty_temp = qty_temp;
    }

    public int getFav() {
        return fav;
    }

    public void setFav(int fav) {
        this.fav = fav;
    }

    public String getIsgrab() {
        return isgrab;
    }

    public void setIsgrab(String isgrab) {
        this.isgrab = isgrab;
    }
}
