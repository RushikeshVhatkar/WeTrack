package com.rushikeshsantoshv.wetrack.DataModels;

import com.google.firebase.firestore.DocumentReference;

public class EmployeeModel {

    DocumentReference company_path;
    String emp_name, emp_contact, emp_reference;
    boolean emp_status;
    Double emp_advance_loans, emp_sal_arrears, emp_sal_paid, emp_sal_total;

    public EmployeeModel(DocumentReference company_path, String emp_name, String emp_contact, String emp_reference, boolean emp_status,
                         Double emp_advance_loans, Double emp_sal_arrears, Double emp_sal_paid, Double emp_sal_total) {
        this.company_path = company_path;
        this.emp_name = emp_name;
        this.emp_contact = emp_contact;
        this.emp_reference = emp_reference;
        this.emp_status = emp_status;
        this.emp_advance_loans = emp_advance_loans;
        this.emp_sal_arrears = emp_sal_arrears;
        this.emp_sal_paid = emp_sal_paid;
        this.emp_sal_total= emp_sal_total;
    }

    public Double getEmp_sal_total() {
        return emp_sal_total;
    }

    public void setEmp_sal_total(Double emp_sal_total) {
        this.emp_sal_total = emp_sal_total;
    }

    public DocumentReference getCompany_path() {
        return company_path;
    }

    public void setCompany_path(DocumentReference company_path) {
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

    public String getEmp_reference() {
        return emp_reference;
    }

    public void setEmp_reference(String emp_reference) {
        this.emp_reference = emp_reference;
    }

    public boolean isEmp_status() {
        return emp_status;
    }

    public void setEmp_status(boolean emp_status) {
        this.emp_status = emp_status;
    }

    public Double getEmp_advance_loans() {
        return emp_advance_loans;
    }

    public void setEmp_advance_loans(Double emp_advance_loans) {
        this.emp_advance_loans = emp_advance_loans;
    }

    public Double getEmp_sal_arrears() {
        return emp_sal_arrears;
    }

    public void setEmp_sal_arrears(Double emp_sal_arrears) {
        this.emp_sal_arrears = emp_sal_arrears;
    }

    public Double getEmp_sal_paid() {
        return emp_sal_paid;
    }

    public void setEmp_sal_paid(Double emp_sal_paid) {
        this.emp_sal_paid = emp_sal_paid;
    }
}
