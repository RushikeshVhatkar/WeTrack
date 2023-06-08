package com.rushikeshsantoshv.wetrack.Databases.RoomDatabase.CompanyBaseRate;

import android.database.Cursor;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface CompanyBaseRateDao {

    @Query("select * from CompanyBaseRate")
    Cursor getAllCompanyBaseRates();

    @Insert
    void addCompanyBaseRate(CompanyBaseRate companyBaseRate);

}
