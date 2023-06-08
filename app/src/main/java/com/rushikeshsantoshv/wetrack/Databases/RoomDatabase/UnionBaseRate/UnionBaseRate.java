package com.rushikeshsantoshv.wetrack.Databases.RoomDatabase.UnionBaseRate;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "UnionBaseRate")
public class UnionBaseRate {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "unionbaserate_id")
    private String unionbaserate_id;

    @ColumnInfo(name = "company_reference")
    private String company_reference;

    @ColumnInfo(name = "union_rate_date")
    private Date union_rate_date;

    @ColumnInfo(name = "union_rate_val")
    private long union_rate_val;

    public UnionBaseRate(int id, String unionbaserate_id, String company_reference, Date union_rate_date, long union_rate_val) {
        this.id = id;
        this.unionbaserate_id = unionbaserate_id;
        this.company_reference = company_reference;
        this.union_rate_date = union_rate_date;
        this.union_rate_val = union_rate_val;
    }

    @Ignore
    public UnionBaseRate(String unionbaserate_id, String company_reference, Date union_rate_date, long union_rate_val) {
        this.unionbaserate_id = unionbaserate_id;
        this.company_reference = company_reference;
        this.union_rate_date = union_rate_date;
        this.union_rate_val = union_rate_val;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUnionbaserate_id() {
        return unionbaserate_id;
    }

    public void setUnionbaserate_id(String unionbaserate_id) {
        this.unionbaserate_id = unionbaserate_id;
    }

    public String getCompany_reference() {
        return company_reference;
    }

    public void setCompany_reference(String company_reference) {
        this.company_reference = company_reference;
    }

    public Date getUnion_rate_date() {
        return union_rate_date;
    }

    public void setUnion_rate_date(Date union_rate_date) {
        this.union_rate_date = union_rate_date;
    }

    public long getUnion_rate_val() {
        return union_rate_val;
    }

    public void setUnion_rate_val(long union_rate_val) {
        this.union_rate_val = union_rate_val;
    }
}
