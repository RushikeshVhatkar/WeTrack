package com.rushikeshsantoshv.wetrack.DataModels;

import com.google.firebase.firestore.DocumentReference;

public class ManagerModel {

    DocumentReference company_path;
    String manager_name, manager_contact, manger_path;
    boolean manager_status;

    public ManagerModel(DocumentReference company_path, String manager_name, String manager_contact, String manger_path, boolean manager_status) {
        this.company_path = company_path;
        this.manager_name = manager_name;
        this.manager_contact = manager_contact;
        this.manger_path = manger_path;
        this.manager_status = manager_status;
    }

    public DocumentReference getCompany_path() {
        return company_path;
    }

    public void setCompany_path(DocumentReference company_path) {
        this.company_path = company_path;
    }

    public String getManager_name() {
        return manager_name;
    }

    public void setManager_name(String manager_name) {
        this.manager_name = manager_name;
    }

    public String getManager_contact() {
        return manager_contact;
    }

    public void setManager_contact(String manager_contact) {
        this.manager_contact = manager_contact;
    }

    public String getManger_path() {
        return manger_path;
    }

    public void setManger_path(String manger_path) {
        this.manger_path = manger_path;
    }

    public boolean isManager_status() {
        return manager_status;
    }

    public void setManager_status(boolean manager_status) {
        this.manager_status = manager_status;
    }
}
