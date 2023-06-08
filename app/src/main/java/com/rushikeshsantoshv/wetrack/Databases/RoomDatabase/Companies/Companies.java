package com.rushikeshsantoshv.wetrack.Databases.RoomDatabase.Companies;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "Companies")
public class Companies {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "company_id")
    private String company_id;

    @ColumnInfo(name = "company_contactnum")
    private String company_contactnum;

    @ColumnInfo(name = "company_created")
    private Date company_created;

    @ColumnInfo(name = "company_location")
    private String company_location;

    @ColumnInfo(name = "company_name")
    private String company_name;

    @ColumnInfo(name = "company_services")
    private String company_services;

    @ColumnInfo(name = "company_year_exp")
    private String company_year_exp;

    public Companies(int id, String company_id, String company_contactnum, Date company_created, String company_location, String company_name, String company_services, String company_year_exp) {
        this.id = id;
        this.company_id = company_id;
        this.company_contactnum = company_contactnum;
        this.company_created = company_created;
        this.company_location = company_location;
        this.company_name = company_name;
        this.company_services = company_services;
        this.company_year_exp = company_year_exp;
    }

    @Ignore
    public Companies(String company_id, String company_contactnum, Date company_created, String company_location, String company_name, String company_services, String company_year_exp) {
        this.company_id = company_id;
        this.company_contactnum = company_contactnum;
        this.company_created = company_created;
        this.company_location = company_location;
        this.company_name = company_name;
        this.company_services = company_services;
        this.company_year_exp = company_year_exp;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCompany_id() {
        return company_id;
    }

    public void setCompany_id(String company_id) {
        this.company_id = company_id;
    }

    public String getCompany_contactnum() {
        return company_contactnum;
    }

    public void setCompany_contactnum(String company_contactnum) {
        this.company_contactnum = company_contactnum;
    }

    public Date getCompany_created() {
        return company_created;
    }

    public void setCompany_created(Date company_created) {
        this.company_created = company_created;
    }

    public String getCompany_location() {
        return company_location;
    }

    public void setCompany_location(String company_location) {
        this.company_location = company_location;
    }

    public String getCompany_name() {
        return company_name;
    }

    public void setCompany_name(String company_name) {
        this.company_name = company_name;
    }

    public String getCompany_services() {
        return company_services;
    }

    public void setCompany_services(String company_services) {
        this.company_services = company_services;
    }

    public String getCompany_year_exp() {
        return company_year_exp;
    }

    public void setCompany_year_exp(String company_year_exp) {
        this.company_year_exp = company_year_exp;
    }
}
