package com.rushikeshsantoshv.wetrack.Databases.RoomDatabase.Managers;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "Managers")
public class Managers {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "manager_id")
    private String manager_id;

    @ColumnInfo(name = "company_path")
    private String company_path;

    @ColumnInfo(name = "manager_contact")
    private String manager_contact;

    @ColumnInfo(name = "manager_name")
    private String manager_name;

    @ColumnInfo(name = "manager_status")
    private boolean manager_status;

    @ColumnInfo(name = "manager_timestamp")
    private Date manager_timestamp;

    public Managers(int id, String manager_id, String company_path, String manager_contact, String manager_name, boolean manager_status, Date manager_timestamp) {
        this.id = id;
        this.manager_id = manager_id;
        this.company_path = company_path;
        this.manager_contact = manager_contact;
        this.manager_name = manager_name;
        this.manager_status = manager_status;
        this.manager_timestamp = manager_timestamp;
    }

    @Ignore
    public Managers(String manager_id, String company_path, String manager_contact, String manager_name, boolean manager_status, Date manager_timestamp) {
        this.manager_id = manager_id;
        this.company_path = company_path;
        this.manager_contact = manager_contact;
        this.manager_name = manager_name;
        this.manager_status = manager_status;
        this.manager_timestamp = manager_timestamp;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getManager_id() {
        return manager_id;
    }

    public void setManager_id(String manager_id) {
        this.manager_id = manager_id;
    }

    public String getCompany_path() {
        return company_path;
    }

    public void setCompany_path(String company_path) {
        this.company_path = company_path;
    }

    public String getManager_contact() {
        return manager_contact;
    }

    public void setManager_contact(String manager_contact) {
        this.manager_contact = manager_contact;
    }

    public String getManager_name() {
        return manager_name;
    }

    public void setManager_name(String manager_name) {
        this.manager_name = manager_name;
    }

    public boolean isManager_status() {
        return manager_status;
    }

    public void setManager_status(boolean manager_status) {
        this.manager_status = manager_status;
    }

    public Date getManager_timestamp() {
        return manager_timestamp;
    }

    public void setManager_timestamp(Date manager_timestamp) {
        this.manager_timestamp = manager_timestamp;
    }
}
