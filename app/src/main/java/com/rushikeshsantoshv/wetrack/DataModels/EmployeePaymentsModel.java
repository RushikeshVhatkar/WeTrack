package com.rushikeshsantoshv.wetrack.DataModels;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;

public class EmployeePaymentsModel {
    DocumentReference pemp_company_reference, pemp_emp_reference;
    Double pemp_base_rate, pemp_loan_paid, pemp_loan_total, pemp_sal_arrears, pemp_sal_paid, pemp_sal_total, pemp_wage_paid, pemp_wage_total, pemp_loan_balance;
    Timestamp pemp_timestamp, pemp_start_date;

    public EmployeePaymentsModel(DocumentReference pemp_company_reference, DocumentReference pemp_emp_reference,
                                 Double pemp_base_rate, Double pemp_loan_paid, Double pemp_loan_total, Double pemp_sal_arrears,
                                 Double pemp_sal_paid, Double pemp_sal_total, Double pemp_wage_paid, Double pemp_wage_total,
                                 Double pemp_loan_balance, Timestamp pemp_timestamp, Timestamp pemp_start_date) {
        this.pemp_company_reference = pemp_company_reference;
        this.pemp_emp_reference = pemp_emp_reference;
        this.pemp_base_rate = pemp_base_rate;
        this.pemp_loan_paid = pemp_loan_paid;
        this.pemp_loan_total = pemp_loan_total;
        this.pemp_sal_arrears = pemp_sal_arrears;
        this.pemp_sal_paid = pemp_sal_paid;
        this.pemp_sal_total = pemp_sal_total;
        this.pemp_wage_paid = pemp_wage_paid;
        this.pemp_wage_total = pemp_wage_total;
        this.pemp_loan_balance = pemp_loan_balance;
        this.pemp_timestamp = pemp_timestamp;
        this.pemp_start_date = pemp_start_date;
    }

    public DocumentReference getPemp_company_reference() {
        return pemp_company_reference;
    }

    public void setPemp_company_reference(DocumentReference pemp_company_reference) {
        this.pemp_company_reference = pemp_company_reference;
    }

    public DocumentReference getPemp_emp_reference() {
        return pemp_emp_reference;
    }

    public void setPemp_emp_reference(DocumentReference pemp_emp_reference) {
        this.pemp_emp_reference = pemp_emp_reference;
    }

    public Double getPemp_base_rate() {
        return pemp_base_rate;
    }

    public void setPemp_base_rate(Double pemp_base_rate) {
        this.pemp_base_rate = pemp_base_rate;
    }

    public Double getPemp_loan_paid() {
        return pemp_loan_paid;
    }

    public void setPemp_loan_paid(Double pemp_loan_paid) {
        this.pemp_loan_paid = pemp_loan_paid;
    }

    public Double getPemp_loan_total() {
        return pemp_loan_total;
    }

    public void setPemp_loan_total(Double pemp_loan_total) {
        this.pemp_loan_total = pemp_loan_total;
    }

    public Double getPemp_sal_arrears() {
        return pemp_sal_arrears;
    }

    public void setPemp_sal_arrears(Double pemp_sal_arrears) {
        this.pemp_sal_arrears = pemp_sal_arrears;
    }

    public Double getPemp_sal_paid() {
        return pemp_sal_paid;
    }

    public void setPemp_sal_paid(Double pemp_sal_paid) {
        this.pemp_sal_paid = pemp_sal_paid;
    }

    public Double getPemp_sal_total() {
        return pemp_sal_total;
    }

    public void setPemp_sal_total(Double pemp_sal_total) {
        this.pemp_sal_total = pemp_sal_total;
    }

    public Double getPemp_wage_paid() {
        return pemp_wage_paid;
    }

    public void setPemp_wage_paid(Double pemp_wage_paid) {
        this.pemp_wage_paid = pemp_wage_paid;
    }

    public Double getPemp_wage_total() {
        return pemp_wage_total;
    }

    public void setPemp_wage_total(Double pemp_wage_total) {
        this.pemp_wage_total = pemp_wage_total;
    }

    public Double getPemp_loan_balance() {
        return pemp_loan_balance;
    }

    public void setPemp_loan_balance(Double pemp_loan_balance) {
        this.pemp_loan_balance = pemp_loan_balance;
    }

    public Timestamp getPemp_timestamp() {
        return pemp_timestamp;
    }

    public void setPemp_timestamp(Timestamp pemp_timestamp) {
        this.pemp_timestamp = pemp_timestamp;
    }

    public Timestamp getPemp_start_date() {
        return pemp_start_date;
    }

    public void setPemp_start_date(Timestamp pemp_start_date) {
        this.pemp_start_date = pemp_start_date;
    }
}
