package com.rushikeshsantoshv.wetrack.Databases.RoomDatabase.Loans;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "Loans")
public class Loans {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "loan_id")
    private String loan_id;

    @ColumnInfo(name = "loan_amount")
    private long loan_amount;

    @ColumnInfo(name = "loan_balance")
    private long loan_balance;

    @ColumnInfo(name = "loan_company_reference")
    private String loan_company_reference;

    @ColumnInfo(name = "loan_emp_reference")
    private String loan_emp_reference;

    @ColumnInfo(name = "loan_from_sal")
    private boolean loan_from_sal;

    @ColumnInfo(name = "loan_pay_status")
    private boolean loan_pay_status;

    @ColumnInfo(name = "loan_timestamp")
    private Date loan_timestamp;

    public Loans(int id, String loan_id, long loan_amount, long loan_balance, String loan_company_reference, String loan_emp_reference, boolean loan_from_sal, boolean loan_pay_status, Date loan_timestamp) {
        this.id = id;
        this.loan_id = loan_id;
        this.loan_amount = loan_amount;
        this.loan_balance = loan_balance;
        this.loan_company_reference = loan_company_reference;
        this.loan_emp_reference = loan_emp_reference;
        this.loan_from_sal = loan_from_sal;
        this.loan_pay_status = loan_pay_status;
        this.loan_timestamp = loan_timestamp;
    }

    @Ignore
    public Loans(String loan_id, long loan_amount, long loan_balance, String loan_company_reference, String loan_emp_reference, boolean loan_from_sal, boolean loan_pay_status, Date loan_timestamp) {
        this.loan_id = loan_id;
        this.loan_amount = loan_amount;
        this.loan_balance = loan_balance;
        this.loan_company_reference = loan_company_reference;
        this.loan_emp_reference = loan_emp_reference;
        this.loan_from_sal = loan_from_sal;
        this.loan_pay_status = loan_pay_status;
        this.loan_timestamp = loan_timestamp;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLoan_id() {
        return loan_id;
    }

    public void setLoan_id(String loan_id) {
        this.loan_id = loan_id;
    }

    public long getLoan_amount() {
        return loan_amount;
    }

    public void setLoan_amount(long loan_amount) {
        this.loan_amount = loan_amount;
    }

    public long getLoan_balance() {
        return loan_balance;
    }

    public void setLoan_balance(long loan_balance) {
        this.loan_balance = loan_balance;
    }

    public String getLoan_company_reference() {
        return loan_company_reference;
    }

    public void setLoan_company_reference(String loan_company_reference) {
        this.loan_company_reference = loan_company_reference;
    }

    public String getLoan_emp_reference() {
        return loan_emp_reference;
    }

    public void setLoan_emp_reference(String loan_emp_reference) {
        this.loan_emp_reference = loan_emp_reference;
    }

    public boolean isLoan_from_sal() {
        return loan_from_sal;
    }

    public void setLoan_from_sal(boolean loan_from_sal) {
        this.loan_from_sal = loan_from_sal;
    }

    public boolean isLoan_pay_status() {
        return loan_pay_status;
    }

    public void setLoan_pay_status(boolean loan_pay_status) {
        this.loan_pay_status = loan_pay_status;
    }

    public Date getLoan_timestamp() {
        return loan_timestamp;
    }

    public void setLoan_timestamp(Date loan_timestamp) {
        this.loan_timestamp = loan_timestamp;
    }
}
