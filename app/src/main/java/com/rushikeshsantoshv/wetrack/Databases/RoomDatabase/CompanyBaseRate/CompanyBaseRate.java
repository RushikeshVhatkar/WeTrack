package com.rushikeshsantoshv.wetrack.Databases.RoomDatabase.CompanyBaseRate;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "CompanyBaseRate")
public class CompanyBaseRate {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "companybaserate_id")
    private String companybaserate_id;

    @ColumnInfo(name = "company_rate_date")
    private Date company_rate_date;

    @ColumnInfo(name = "company_rate_val")
    private long company_rate_val;

    @ColumnInfo(name = "company_reference")
    private String company_reference;

    public CompanyBaseRate(int id, String companybaserate_id, Date company_rate_date, long company_rate_val, String company_reference) {
        this.id = id;
        this.companybaserate_id = companybaserate_id;
        this.company_rate_date = company_rate_date;
        this.company_rate_val = company_rate_val;
        this.company_reference = company_reference;
    }

    @Ignore
    public CompanyBaseRate(String companybaserate_id, Date company_rate_date, long company_rate_val, String company_reference) {
        this.companybaserate_id = companybaserate_id;
        this.company_rate_date = company_rate_date;
        this.company_rate_val = company_rate_val;
        this.company_reference = company_reference;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCompanybaserate_id() {
        return companybaserate_id;
    }

    public void setCompanybaserate_id(String companybaserate_id) {
        this.companybaserate_id = companybaserate_id;
    }

    public Date getCompany_rate_date() {
        return company_rate_date;
    }

    public void setCompany_rate_date(Date company_rate_date) {
        this.company_rate_date = company_rate_date;
    }

    public long getCompany_rate_val() {
        return company_rate_val;
    }

    public void setCompany_rate_val(long company_rate_val) {
        this.company_rate_val = company_rate_val;
    }

    public String getCompany_reference() {
        return company_reference;
    }

    public void setCompany_reference(String company_reference) {
        this.company_reference = company_reference;
    }
}
