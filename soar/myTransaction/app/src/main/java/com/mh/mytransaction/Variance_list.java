package com.mh.mytransaction;

public class Variance_list {

    String product;
    String prod_uom;
    int id;


    public Variance_list(String product, String prod_uom,int id) {
        this.product = product;
        this.prod_uom = prod_uom;
        this.id=id;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getProd_uom() {
        return prod_uom;
    }

    public void setProd_uom(String prod_uom) {
        this.prod_uom = prod_uom;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
