package com.rushikeshsantoshv.wetrack.Databases.RoomDatabase;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.rushikeshsantoshv.wetrack.Databases.RoomDatabase.Attendances.Attendances;
import com.rushikeshsantoshv.wetrack.Databases.RoomDatabase.Attendances.AttendancesDao;
import com.rushikeshsantoshv.wetrack.Databases.RoomDatabase.Companies.Companies;
import com.rushikeshsantoshv.wetrack.Databases.RoomDatabase.Companies.CompaniesDao;
import com.rushikeshsantoshv.wetrack.Databases.RoomDatabase.CompanyBaseRate.CompanyBaseRate;
import com.rushikeshsantoshv.wetrack.Databases.RoomDatabase.CompanyBaseRate.CompanyBaseRateDao;
import com.rushikeshsantoshv.wetrack.Databases.RoomDatabase.Employees.Employees;
import com.rushikeshsantoshv.wetrack.Databases.RoomDatabase.Employees.EmployeesDao;
import com.rushikeshsantoshv.wetrack.Databases.RoomDatabase.Loans.Loans;
import com.rushikeshsantoshv.wetrack.Databases.RoomDatabase.Loans.LoansDao;
import com.rushikeshsantoshv.wetrack.Databases.RoomDatabase.Managers.Managers;
import com.rushikeshsantoshv.wetrack.Databases.RoomDatabase.Managers.ManagersDao;
import com.rushikeshsantoshv.wetrack.Databases.RoomDatabase.Owners.Owners;
import com.rushikeshsantoshv.wetrack.Databases.RoomDatabase.Owners.OwnersDao;
import com.rushikeshsantoshv.wetrack.Databases.RoomDatabase.Payments.Payments;
import com.rushikeshsantoshv.wetrack.Databases.RoomDatabase.Payments.PaymentsDao;
import com.rushikeshsantoshv.wetrack.Databases.RoomDatabase.UnionBaseRate.UnionBaseRate;
import com.rushikeshsantoshv.wetrack.Databases.RoomDatabase.UnionBaseRate.UnionBaseRateDao;

@Database(entities = {Employees.class, Companies.class, Owners.class, Attendances.class, Loans.class, Managers.class,
        Payments.class, CompanyBaseRate.class, UnionBaseRate.class}, exportSchema = false, version = 1)
@TypeConverters({DateConverter.class})
public abstract class RoomDatabaseHelper extends RoomDatabase {

    private static final String DBName= "WeTrackDB";
    private static RoomDatabaseHelper instance;

    public static synchronized RoomDatabaseHelper getRoomDatabaseDB(Context context){
        if(instance==null){
            instance= Room.databaseBuilder(context, RoomDatabaseHelper.class, DBName)
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries()
                    .build();
        }
        return instance;
    }

    public abstract EmployeesDao employeesDao();

    public abstract AttendancesDao attendancesDao();

    public abstract CompaniesDao companiesDao();

    public abstract CompanyBaseRateDao companyBaseRateDao();

    public abstract LoansDao loansDao();

    public abstract ManagersDao managersDao();

    public abstract OwnersDao ownersDao();

    public abstract PaymentsDao paymentsDao();

    public abstract UnionBaseRateDao unionBaseRateDao();
}
