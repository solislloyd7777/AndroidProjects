package com.mh.mytransaction;

public class Delivery_module {
    int delivery_template_id;
    int branch_id;
    String branch_name;
    String name;
    String doc_num;
    String date_req;

    public Delivery_module(int delivery_template_id, int branch_id, String branch_name,String name, String doc_num, String date_req) {
        this.delivery_template_id = delivery_template_id;
        this.branch_id = branch_id;
        this.branch_name=branch_name;
        this.name = name;
        this.doc_num = doc_num;
        this.date_req = date_req;
    }

    public int getDelivery_template_id() {
        return delivery_template_id;
    }

    public void setDelivery_template_id(int delivery_template_id) {
        this.delivery_template_id = delivery_template_id;
    }

    public int getBranch_id() {
        return branch_id;
    }

    public void setBranch_id(int branch_id) {
        this.branch_id = branch_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDoc_num() {
        return doc_num;
    }

    public void setDoc_num(String doc_num) {
        this.doc_num = doc_num;
    }

    public String getDate_req() {
        return date_req;
    }

    public void setDate_req(String date_req) {
        this.date_req = date_req;
    }

    public String getBranch_name() {
        return branch_name;
    }

    public void setBranch_name(String branch_name) {
        this.branch_name = branch_name;
    }

    @Override
    public String toString() {
        String name1=name.substring(0,3)+"_"+branch_name.replace(" ","_")+"_"+date_req.replace("-","")+"_"+doc_num;
        return name1;
    }
}
