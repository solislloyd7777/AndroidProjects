package com.mh.mytransaction;

import java.io.Serializable;

public class Created_transaction_module implements Serializable {

    String name,doc_num,branch_name,file_name,ref_num,remarks,locator,product,uom,isnegative,iscomputed,created_by,date_req,date_ref;
    double price,subtotal;
    double qty;

    public Created_transaction_module(String name, String doc_num, String branch_name, String file_name, String ref_num, String remarks, String locator, String product, String uom, double qty, double price, double subtotal, String isnegative, String iscomputed, String date_req, String date_ref, String created_by) {
        this.name = name;
        this.doc_num = doc_num;
        this.branch_name = branch_name;
        this.file_name = file_name;
        this.ref_num = ref_num;
        this.remarks = remarks;
        this.locator = locator;
        this.product = product;
        this.uom = uom;
        this.isnegative = isnegative;
        this.iscomputed = iscomputed;
        this.created_by = created_by;
        this.date_req = date_req;
        this.date_ref = date_ref;
        this.price = price;
        this.subtotal = subtotal;
        this.qty = qty;
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

    public String getBranch_name() {
        return branch_name;
    }

    public void setBranch_name(String branch_name) {
        this.branch_name = branch_name;
    }

    public String getFile_name() {
        return file_name;
    }

    public void setFile_name(String file_name) {
        this.file_name = file_name;
    }

    public String getRef_num() {
        return ref_num;
    }

    public void setRef_num(String ref_num) {
        this.ref_num = ref_num;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getLocator() {
        return locator;
    }

    public void setLocator(String locator) {
        this.locator = locator;
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

    public String getIsnegative() {
        return isnegative;
    }

    public void setIsnegative(String isnegative) {
        this.isnegative = isnegative;
    }

    public String getIscomputed() {
        return iscomputed;
    }

    public void setIscomputed(String iscomputed) {
        this.iscomputed = iscomputed;
    }

    public String getCreated_by() {
        return created_by;
    }

    public void setCreated_by(String created_by) {
        this.created_by = created_by;
    }

    public String getDate_req() {
        return date_req;
    }

    public void setDate_req(String date_req) {
        this.date_req = date_req;
    }

    public String getDate_ref() {
        return date_ref;
    }

    public void setDate_ref(String date_ref) {
        this.date_ref = date_ref;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }

    public double getQty() {
        return qty;
    }

    public void setQty(double qty) {
        this.qty = qty;
    }

    @Override
    public String toString() {
        return file_name;
    }
}
