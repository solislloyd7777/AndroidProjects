package com.mh.mytransaction;

import java.io.Serializable;

public class Prod_list implements Serializable {

    int prod_id;
    String prod_name;
    String uom;

    public Prod_list(int prod_id, String prod_name, String uom) {
        this.prod_id = prod_id;
        this.prod_name = prod_name;
        this.uom = uom;
    }

    public int getProd_id() {
        return prod_id;
    }

    public void setProd_id(int prod_id) {
        this.prod_id = prod_id;
    }

    public String getProd_name() {
        return prod_name;
    }

    public void setProd_name(String prod_name) {
        this.prod_name = prod_name;
    }

    public String getUom() {
        return uom;
    }

    public void setUom(String uom) {
        this.uom = uom;
    }
}


