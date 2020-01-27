package com.mh.mytransaction;

public class myList {
    String product;
    String uom;
    String sku;


    public myList(String product, String uom,String sku) {
        this.product = product;
        this.uom = uom;
        this.sku=sku;
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

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }
}
