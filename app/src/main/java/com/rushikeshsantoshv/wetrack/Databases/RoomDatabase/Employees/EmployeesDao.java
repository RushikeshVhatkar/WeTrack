package com.rushikeshsantoshv.wetrack.Databases.RoomDatabase.Employees;

import android.database.Cursor;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface EmployeesDao {

    @Query("select * from Employees")
    Cursor getAllEmployees();

    @Insert
    void addEmployee(Employees employees);


}
