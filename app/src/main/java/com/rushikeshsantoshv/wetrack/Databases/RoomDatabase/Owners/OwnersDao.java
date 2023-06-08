package com.rushikeshsantoshv.wetrack.Databases.RoomDatabase.Owners;

import android.database.Cursor;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface OwnersDao {
    @Query("select * from Owners")
    Cursor getAllOwners();

    @Insert
    void addOwner(Owners owners);
}
