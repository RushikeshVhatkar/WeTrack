package com.rushikeshsantoshv.wetrack.DataModels;

import com.google.firebase.firestore.DocumentReference;

public class EmpAttendanceRateModel {

    private String emp_name, phone;
    private float rate_num;
    private long present_count, halfday_count, absent_count;
    private DocumentReference emp_reference;

    public EmpAttendanceRateModel(DocumentReference emp_reference, String emp_name, String phone, float rate_num, long present_count, long halfday_count, long absent_count) {
        this.emp_reference= emp_reference;
        this.emp_name = emp_name;
        this.phone = phone;
        this.rate_num = rate_num;
        this.present_count = present_count;
        this.halfday_count = halfday_count;
        this.absent_count = absent_count;
    }

    public DocumentReference getEmp_reference() {
        return emp_reference;
    }

    public void setEmp_reference(DocumentReference emp_reference) {
        this.emp_reference = emp_reference;
    }

    public String getEmp_name() {
        return emp_name;
    }

    public void setEmp_name(String emp_name) {
        this.emp_name = emp_name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public float getRate_num() {
        return rate_num;
    }

    public void setRate_num(float rate_num) {
        this.rate_num = rate_num;
    }

    public long getPresent_count() {
        return present_count;
    }

    public void setPresent_count(long present_count) {
        this.present_count = present_count;
    }

    public long getHalfday_count() {
        return halfday_count;
    }

    public void setHalfday_count(long halfday_count) {
        this.halfday_count = halfday_count;
    }

    public long getAbsent_count() {
        return absent_count;
    }

    public void setAbsent_count(long absent_count) {
        this.absent_count = absent_count;
    }
}
