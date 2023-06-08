package com.rushikeshsantoshv.wetrack.DataModels;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;

public class EmployeeLoansModel {

    Double loan_amount, loan_balance;
    DocumentReference loan_company_reference, loan_emp_reference;
    Timestamp loan_timestamp;
    boolean loan_from_sal, loan_pay_status;

    public EmployeeLoansModel(Double loan_amount, Double loan_balance, DocumentReference loan_company_reference,
                              DocumentReference loan_emp_reference, Timestamp loan_timestamp, boolean loan_from_sal,
                              boolean loan_pay_status) {
        this.loan_amount = loan_amount;
        this.loan_balance = loan_balance;
        this.loan_company_reference = loan_company_reference;
            this.loan_emp_reference = loan_emp_reference;
        this.loan_timestamp = loan_timestamp;
        this.loan_from_sal = loan_from_sal;
        this.loan_pay_status = loan_pay_status;
    }

    public Double getLoan_amount() {
        return loan_amount;
    }

    public void setLoan_amount(Double loan_amount) {
        this.loan_amount = loan_amount;
    }

    public Double getLoan_balance() {
        return loan_balance;
    }

    public void setLoan_balance(Double loan_balance) {
        this.loan_balance = loan_balance;
    }

    public DocumentReference getLoan_company_reference() {
        return loan_company_reference;
    }

    public void setLoan_company_reference(DocumentReference loan_company_reference) {
        this.loan_company_reference = loan_company_reference;
    }

    public DocumentReference getLoan_emp_reference() {
        return loan_emp_reference;
    }

    public void setLoan_emp_reference(DocumentReference loan_emp_reference) {
        this.loan_emp_reference = loan_emp_reference;
    }

    public Timestamp getLoan_timestamp() {
        return loan_timestamp;
    }

    public void setLoan_timestamp(Timestamp loan_timestamp) {
        this.loan_timestamp = loan_timestamp;
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
}
