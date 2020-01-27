package com.mh.mytransaction;

import java.io.Serializable;

public class Temp_list implements Serializable {

    int id,f_lead,t_lead;
    String template_name,isactive,recipient,sku;

    public Temp_list(int id, int f_lead,int t_lead, String template_name, String isactive,String recipient,String sku) {
        this.id = id;
        this.f_lead = f_lead;
        this.t_lead=t_lead;
        this.template_name = template_name;
        this.isactive = isactive;
        this.recipient=recipient;
        this.sku=sku;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTemplate_name() {
        return template_name;
    }

    public void setTemplate_name(String template_name) {
        this.template_name = template_name;
    }

    public String getIsactive() {
        return isactive;
    }

    public void setIsactive(String isactive) {
        this.isactive = isactive;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public int getF_lead() {
        return f_lead;
    }

    public void setF_lead(int f_lead) {
        this.f_lead = f_lead;
    }

    public int getT_lead() {
        return t_lead;
    }

    public void setT_lead(int t_lead) {
        this.t_lead = t_lead;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    @Override
    public String toString() {
        return template_name;
    }
}
