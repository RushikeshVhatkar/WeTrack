package com.rushikeshsantoshv.wetrack.Databases.RoomDatabase.UnionBaseRate;

import android.database.Cursor;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface UnionBaseRateDao {

    @Query("select * from UnionBaseRate")
    Cursor getAllUnionBaseRates();

    @Insert
    void addUnionBaseRate(UnionBaseRate unionBaseRate);

}
