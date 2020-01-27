package com.mh.mytransaction;

public class Created_transaction_module1 {
    String doc_num,filename;

    public Created_transaction_module1(String doc_num, String filename) {
        this.doc_num = doc_num;
        this.filename = filename;
    }

    public String getDoc_num() {
        return doc_num;
    }

    public void setDoc_num(String doc_num) {
        this.doc_num = doc_num;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    @Override
    public String toString() {
        return filename;
    }
}
