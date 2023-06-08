package com.rushikeshsantoshv.wetrack.Databases.RoomDatabase.Owners;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "Owners")
public class Owners {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "owner_id")
    private String owner_id;

    @ColumnInfo(name = "company_path")
    private String company_path;

    @ColumnInfo(name = "contact_number")
    private String contact_number;

    @ColumnInfo(name = "full_name")
    private String full_name;

    @ColumnInfo(name = "email_id")
    private String email_id;

    @ColumnInfo(name = "gender")
    private String gender;

    @ColumnInfo(name = "isDetailsAdded")
    private boolean isDetailsAdded;

    public Owners(int id, String owner_id, String company_path, String contact_number, String full_name, String email_id, String gender, boolean isDetailsAdded) {
        this.id = id;
        this.owner_id = owner_id;
        this.company_path = company_path;
        this.contact_number = contact_number;
        this.full_name = full_name;
        this.email_id = email_id;
        this.gender = gender;
        this.isDetailsAdded = isDetailsAdded;
    }

    @Ignore
    public Owners(String owner_id, String company_path, String contact_number, String full_name, String email_id, String gender, boolean isDetailsAdded) {
        this.owner_id = owner_id;
        this.company_path = company_path;
        this.contact_number = contact_number;
        this.full_name = full_name;
        this.email_id = email_id;
        this.gender = gender;
        this.isDetailsAdded = isDetailsAdded;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getOwner_id() {
        return owner_id;
    }

    public void setOwner_id(String owner_id) {
        this.owner_id = owner_id;
    }

    public String getCompany_path() {
        return company_path;
    }

    public void setCompany_path(String company_path) {
        this.company_path = company_path;
    }

    public String getContact_number() {
        return contact_number;
    }

    public void setContact_number(String contact_number) {
        this.contact_number = contact_number;
    }

    public String getFull_name() {
        return full_name;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }

    public String getEmail_id() {
        return email_id;
    }

    public void setEmail_id(String email_id) {
        this.email_id = email_id;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public boolean isDetailsAdded() {
        return isDetailsAdded;
    }

    public void setDetailsAdded(boolean detailsAdded) {
        isDetailsAdded = detailsAdded;
    }
}
