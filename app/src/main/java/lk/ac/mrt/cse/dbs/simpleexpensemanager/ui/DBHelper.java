package lk.ac.mrt.cse.dbs.simpleexpensemanager.ui;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;

/**
 * Created by Pasindu Tennage on 2015-12-04.
 */
public class DBHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "130584U";
    public static final String TABLE_NAME = "transactions";
    public static final String TRANSACTIONS_DATE = "trans_date";
    public static final String TRANSACTIONS_ACCOUNTNO = "accountno";
    public static final String TRANSACTIONS_EXPENSE_TYPE = "expense";
    public static final String TRANSACTIONS_AMOUNT = "amount";


    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(
                "create table accounts " +
                        "(accountno varchar(20), bankname varchar(20),accountholdername varchar(20),balance float)"
        );
        db.execSQL(
                "create table transactions " +
                        "(trans_date varchar(20), accountno varchar(20) not null,expense varchar(20),amount float,FOREIGN KEY(accountno) REFERENCES accounts(accountno) )"
        );

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS accounts");
        db.execSQL("DROP TABLE IF EXISTS transactions");


        onCreate(db);
    }
    public SQLiteDatabase getDatabase() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db;

    }
}
