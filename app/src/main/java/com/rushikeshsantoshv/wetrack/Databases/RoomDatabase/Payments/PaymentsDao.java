package com.rushikeshsantoshv.wetrack.Databases.RoomDatabase.Payments;

import android.database.Cursor;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface PaymentsDao {

    @Query("select * from Payments")
    Cursor getAllPayments();

    @Insert
    void addPayment(Payments payments);
}
