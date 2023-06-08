package com.rushikeshsantoshv.wetrack.DataModels;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;

public class AttendanceModel {

    String emp_name, emp_contact, attend_emp_doc;
    DocumentReference attend_emp_reference, attend_company_reference;
    Timestamp attend_date;
    long attend_status;

    public AttendanceModel(String emp_name, String emp_contact, String attend_emp_doc, DocumentReference attend_emp_reference, DocumentReference attend_company_reference, Timestamp attend_date, long attend_status) {
        this.emp_name= emp_name;
        this.emp_contact= emp_contact;
        this.attend_emp_doc= attend_emp_doc;
        this.attend_emp_reference = attend_emp_reference;
        this.attend_company_reference = attend_company_reference;
        this.attend_date = attend_date;
        this.attend_status = attend_status;
    }

    public String getAttend_emp_doc() {
        return attend_emp_doc;
    }

    public void setAttend_emp_doc(String attend_emp_doc) {
        this.attend_emp_doc = attend_emp_doc;
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

    public DocumentReference getAttend_emp_reference() {
        return attend_emp_reference;
    }

    public void setAttend_emp_reference(DocumentReference attend_emp_reference) {
        this.attend_emp_reference = attend_emp_reference;
    }

    public DocumentReference getAttend_company_reference() {
        return attend_company_reference;
    }

    public void setAttend_company_reference(DocumentReference attend_company_reference) {
        this.attend_company_reference = attend_company_reference;
    }

    public Timestamp getAttend_date() {
        return attend_date;
    }

    public void setAttend_date(Timestamp attend_date) {
        this.attend_date = attend_date;
    }

    public long getAttend_status() {
        return attend_status;
    }

    public void setAttend_status(long attend_status) {
        this.attend_status = attend_status;
    }
}
