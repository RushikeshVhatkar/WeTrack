package com.rushikeshsantoshv.wetrack.Databases.RoomDatabase.Managers;

import android.database.Cursor;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface ManagersDao {
    @Query("select * from Managers")
    Cursor getAllManagers();

    @Insert
    void addManager(Managers managers);
}
