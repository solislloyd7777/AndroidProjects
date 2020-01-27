package com.mh.mytransaction;

import java.io.Serializable;

public class File_list implements Serializable {

    String doc_num;
    String filename;
    String creaor;
    String choosed;

    public File_list(String doc_num, String filename, String creaor, String choosed) {
        this.doc_num = doc_num;
        this.filename = filename;
        this.creaor = creaor;
        this.choosed = choosed;
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

    public String getCreaor() {
        return creaor;
    }

    public void setCreaor(String creaor) {
        this.creaor = creaor;
    }

    public String getChoosed() {
        return choosed;
    }

    public void setChoosed(String choosed) {
        this.choosed = choosed;
    }
}
