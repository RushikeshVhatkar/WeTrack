package com.rushikeshsantoshv.wetrack.Databases.RoomDatabase.Companies;

import android.database.Cursor;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface CompaniesDao {
    @Query("select * from Companies")
    Cursor getAllCompanies();

    @Insert
    void addCompany(Companies companies);
}
