package com.mh.mytransaction;

import java.io.Serializable;

public class Branch_list implements Serializable {

    int branch_id;
    String branch_name;
    String type;
    int set_branch;
    String isactive;

    public Branch_list(int branch_id, String branch_name,String type,int set_branch,String isactive) {
        this.branch_id = branch_id;
        this.branch_name = branch_name;
        this.type=type;
        this.set_branch=set_branch;
        this.isactive=isactive;
    }

    public int getBranch_id() {
        return branch_id;
    }

    public void setBranch_id(int branch_id) {
        this.branch_id = branch_id;
    }

    public String getBranch_name() {
        return branch_name;
    }

    public void setBranch_name(String branch_name) {
        this.branch_name = branch_name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getSet_branch() {
        return set_branch;
    }

    public void setSet_branch(int set_branch) {
        this.set_branch = set_branch;
    }

    public String getIsactive() {
        return isactive;
    }

    public void setIsactive(String isactive) {
        this.isactive = isactive;
    }

}
