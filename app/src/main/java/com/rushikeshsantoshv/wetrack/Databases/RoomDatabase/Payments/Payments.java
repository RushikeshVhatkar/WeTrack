package com.rushikeshsantoshv.wetrack.Databases.RoomDatabase.Payments;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "Payments")
public class Payments {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "payment_id")
    private String payment_id;

    @ColumnInfo(name = "pemp_company_reference")
    private String pemp_company_reference;

    @ColumnInfo(name = "pemp_emp_reference")
    private String pemp_emp_reference;

    @ColumnInfo(name = "pemp_loan_paid")
    private long pemp_loan_paid;

    @ColumnInfo(name = "pemp_loan_total")
    private long pemp_loan_total;

    @ColumnInfo(name = "pemp_sal_arrears")
    private long pemp_sal_arrears;

    @ColumnInfo(name = "pemp_sal_paid")
    private long pemp_sal_paid;

    @ColumnInfo(name = "pemp_sal_total")
    private long pemp_sal_total;

    @ColumnInfo(name = "pemp_timestamp")
    private Date pemp_timestamp;

    @ColumnInfo(name = "pemp_wage_paid")
    private long pemp_wage_paid;

    @ColumnInfo(name = "pemp_wage_total")
    private long pemp_wage_total;

    public Payments(int id, String payment_id, String pemp_company_reference, String pemp_emp_reference, long pemp_loan_paid, long pemp_loan_total, long pemp_sal_arrears, long pemp_sal_paid, long pemp_sal_total, Date pemp_timestamp, long pemp_wage_paid, long pemp_wage_total) {
        this.id = id;
        this.payment_id = payment_id;
        this.pemp_company_reference = pemp_company_reference;
        this.pemp_emp_reference = pemp_emp_reference;
        this.pemp_loan_paid = pemp_loan_paid;
        this.pemp_loan_total = pemp_loan_total;
        this.pemp_sal_arrears = pemp_sal_arrears;
        this.pemp_sal_paid = pemp_sal_paid;
        this.pemp_sal_total = pemp_sal_total;
        this.pemp_timestamp = pemp_timestamp;
        this.pemp_wage_paid = pemp_wage_paid;
        this.pemp_wage_total = pemp_wage_total;
    }

    @Ignore
    public Payments(String payment_id, String pemp_company_reference, String pemp_emp_reference, long pemp_loan_paid, long pemp_loan_total, long pemp_sal_arrears, long pemp_sal_paid, long pemp_sal_total, Date pemp_timestamp, long pemp_wage_paid, long pemp_wage_total) {
        this.payment_id = payment_id;
        this.pemp_company_reference = pemp_company_reference;
        this.pemp_emp_reference = pemp_emp_reference;
        this.pemp_loan_paid = pemp_loan_paid;
        this.pemp_loan_total = pemp_loan_total;
        this.pemp_sal_arrears = pemp_sal_arrears;
        this.pemp_sal_paid = pemp_sal_paid;
        this.pemp_sal_total = pemp_sal_total;
        this.pemp_timestamp = pemp_timestamp;
        this.pemp_wage_paid = pemp_wage_paid;
        this.pemp_wage_total = pemp_wage_total;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPayment_id() {
        return payment_id;
    }

    public void setPayment_id(String payment_id) {
        this.payment_id = payment_id;
    }

    public String getPemp_company_reference() {
        return pemp_company_reference;
    }

    public void setPemp_company_reference(String pemp_company_reference) {
        this.pemp_company_reference = pemp_company_reference;
    }

    public String getPemp_emp_reference() {
        return pemp_emp_reference;
    }

    public void setPemp_emp_reference(String pemp_emp_reference) {
        this.pemp_emp_reference = pemp_emp_reference;
    }

    public long getPemp_loan_paid() {
        return pemp_loan_paid;
    }

    public void setPemp_loan_paid(long pemp_loan_paid) {
        this.pemp_loan_paid = pemp_loan_paid;
    }

    public long getPemp_loan_total() {
        return pemp_loan_total;
    }

    public void setPemp_loan_total(long pemp_loan_total) {
        this.pemp_loan_total = pemp_loan_total;
    }

    public long getPemp_sal_arrears() {
        return pemp_sal_arrears;
    }

    public void setPemp_sal_arrears(long pemp_sal_arrears) {
        this.pemp_sal_arrears = pemp_sal_arrears;
    }

    public long getPemp_sal_paid() {
        return pemp_sal_paid;
    }

    public void setPemp_sal_paid(long pemp_sal_paid) {
        this.pemp_sal_paid = pemp_sal_paid;
    }

    public long getPemp_sal_total() {
        return pemp_sal_total;
    }

    public void setPemp_sal_total(long pemp_sal_total) {
        this.pemp_sal_total = pemp_sal_total;
    }

    public Date getPemp_timestamp() {
        return pemp_timestamp;
    }

    public void setPemp_timestamp(Date pemp_timestamp) {
        this.pemp_timestamp = pemp_timestamp;
    }

    public long getPemp_wage_paid() {
        return pemp_wage_paid;
    }

    public void setPemp_wage_paid(long pemp_wage_paid) {
        this.pemp_wage_paid = pemp_wage_paid;
    }

    public long getPemp_wage_total() {
        return pemp_wage_total;
    }

    public void setPemp_wage_total(long pemp_wage_total) {
        this.pemp_wage_total = pemp_wage_total;
    }
}
