package com.mh.mytransaction;

public class Emp_list {

    int emp_id,code;
    String name,email,pass,isactive;

    public Emp_list(int emp_id, int code, String name, String email,String pass, String isactive) {
        this.emp_id = emp_id;
        this.code = code;
        this.name = name;
        this.email = email;
        this.isactive = isactive;
        this.pass=pass;
    }

    public int getEmp_id() {
        return emp_id;
    }

    public void setEmp_id(int emp_id) {
        this.emp_id = emp_id;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getIsactive() {
        return isactive;
    }

    public void setIsactive(String isactive) {
        this.isactive = isactive;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    @Override
    public String toString() {
        return name;
    }
}
