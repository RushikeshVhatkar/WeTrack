package com.rushikeshsantoshv.wetrack.Databases.RoomDatabase.Attendances;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "Attendances")
public class Attendances {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "attendances_id")
    private String attendances_id;

    @ColumnInfo(name = "attend_company_reference")
    private String attend_company_reference;

    @ColumnInfo(name = "attend_date")
    private Date attend_date;

    @ColumnInfo(name = "attend_emp_reference")
    private String attend_emp_reference;

    @ColumnInfo(name = "attend_status")
    private long attend_status;

    public Attendances(int id, String attendances_id, String attend_company_reference, Date attend_date, String attend_emp_reference, long attend_status) {
        this.id = id;
        this.attendances_id = attendances_id;
        this.attend_company_reference = attend_company_reference;
        this.attend_date = attend_date;
        this.attend_emp_reference = attend_emp_reference;
        this.attend_status = attend_status;
    }

    @Ignore
    public Attendances(String attendances_id, String attend_company_reference, Date attend_date, String attend_emp_reference, long attend_status) {
        this.attendances_id = attendances_id;
        this.attend_company_reference = attend_company_reference;
        this.attend_date = attend_date;
        this.attend_emp_reference = attend_emp_reference;
        this.attend_status = attend_status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAttendances_id() {
        return attendances_id;
    }

    public void setAttendances_id(String attendances_id) {
        this.attendances_id = attendances_id;
    }

    public String getAttend_company_reference() {
        return attend_company_reference;
    }

    public void setAttend_company_reference(String attend_company_reference) {
        this.attend_company_reference = attend_company_reference;
    }

    public Date getAttend_date() {
        return attend_date;
    }

    public void setAttend_date(Date attend_date) {
        this.attend_date = attend_date;
    }

    public String getAttend_emp_reference() {
        return attend_emp_reference;
    }

    public void setAttend_emp_reference(String attend_emp_reference) {
        this.attend_emp_reference = attend_emp_reference;
    }

    public long getAttend_status() {
        return attend_status;
    }

    public void setAttend_status(long attend_status) {
        this.attend_status = attend_status;
    }
}
