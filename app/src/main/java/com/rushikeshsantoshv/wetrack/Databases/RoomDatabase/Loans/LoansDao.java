package com.rushikeshsantoshv.wetrack.Databases.RoomDatabase.Loans;

import android.database.Cursor;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface LoansDao {

    @Query("select * from Loans")
    Cursor getAllLoans();

    @Insert
    void addPayment(Loans loans);

}
