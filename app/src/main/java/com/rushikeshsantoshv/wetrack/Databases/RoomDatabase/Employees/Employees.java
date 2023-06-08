package com.rushikeshsantoshv.wetrack.Databases.RoomDatabase.Employees;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "Employees")
public class Employees {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "emp_id")
    private String emp_id;

    @ColumnInfo(name = "company_path")
    private String company_path;

    @ColumnInfo(name = "emp_name")
    private String emp_name;

    @ColumnInfo(name = "emp_contact")
    private String emp_contact;

    @ColumnInfo(name = "emp_status")
    private boolean emp_status;

    @ColumnInfo(name = "emp_timestamp")
    private Date emp_timestamp;

    @ColumnInfo(name = "emp_advance_loans")
    private long emp_advance_loans;

    @ColumnInfo(name = "emp_sal_arrears")
    private long emp_sal_arrears;

    @ColumnInfo(name = "emp_sal_paid")
    private long emp_sal_paid;

    @ColumnInfo(name = "emp_sal_total")
    private long emp_sal_total;

    public Employees(int id, String emp_id, String company_path, String emp_name, String emp_contact, boolean emp_status, Date emp_timestamp, long emp_advance_loans, long emp_sal_arrears, long emp_sal_paid, long emp_sal_total) {
        this.id = id;
        this.emp_id = emp_id;
        this.company_path = company_path;
        this.emp_name = emp_name;
        this.emp_contact = emp_contact;
        this.emp_status = emp_status;
        this.emp_timestamp = emp_timestamp;
        this.emp_advance_loans = emp_advance_loans;
        this.emp_sal_arrears = emp_sal_arrears;
        this.emp_sal_paid = emp_sal_paid;
        this.emp_sal_total = emp_sal_total;
    }

    @Ignore
    public Employees(String emp_id, String company_path, String emp_name, String emp_contact, boolean emp_status, Date emp_timestamp, long emp_advance_loans, long emp_sal_arrears, long emp_sal_paid, long emp_sal_total) {
        this.emp_id = emp_id;
        this.company_path = company_path;
        this.emp_name = emp_name;
        this.emp_contact = emp_contact;
        this.emp_status = emp_status;
        this.emp_timestamp = emp_timestamp;
        this.emp_advance_loans = emp_advance_loans;
        this.emp_sal_arrears = emp_sal_arrears;
        this.emp_sal_paid = emp_sal_paid;
        this.emp_sal_total = emp_sal_total;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmp_id() {
        return emp_id;
    }

    public void setEmp_id(String emp_id) {
        this.emp_id = emp_id;
    }

    public String getCompany_path() {
        return company_path;
    }

    public void setCompany_path(String company_path) {
        this.company_path = company_path;
    }

    public String getEmp_name() {
        return emp_name;
    }

    public void setEmp_name(String emp_name) {
        this.emp_name = emp_name;
    }

    public String getEmp_contact() {
        return emp_contact;
    }

    public void setEmp_contact(String emp_contact) {
        this.emp_contact = emp_contact;
    }

    public boolean isEmp_status() {
        return emp_status;
    }

    public void setEmp_status(boolean emp_status) {
        this.emp_status = emp_status;
    }

    public Date getEmp_timestamp() {
        return emp_timestamp;
    }

    public void setEmp_timestamp(Date emp_timestamp) {
        this.emp_timestamp = emp_timestamp;
    }

    public long getEmp_advance_loans() {
        return emp_advance_loans;
    }

    public void setEmp_advance_loans(long emp_advance_loans) {
        this.emp_advance_loans = emp_advance_loans;
    }

    public long getEmp_sal_arrears() {
        return emp_sal_arrears;
    }

    public void setEmp_sal_arrears(long emp_sal_arrears) {
        this.emp_sal_arrears = emp_sal_arrears;
    }

    public long getEmp_sal_paid() {
        return emp_sal_paid;
    }

    public void setEmp_sal_paid(long emp_sal_paid) {
        this.emp_sal_paid = emp_sal_paid;
    }

    public long getEmp_sal_total() {
        return emp_sal_total;
    }

    public void setEmp_sal_total(long emp_sal_total) {
        this.emp_sal_total = emp_sal_total;
    }
}
