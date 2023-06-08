package com.rushikeshsantoshv.wetrack.Databases.RoomDatabase.Attendances;

import android.database.Cursor;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface AttendancesDao {
    @Query("select * from Attendances")
    Cursor getAllAttendances();

    @Insert
    void addEmployee(Attendances attendances);
}
