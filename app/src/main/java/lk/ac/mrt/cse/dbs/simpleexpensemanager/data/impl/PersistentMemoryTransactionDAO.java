package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.ui.AppContext;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.ui.DBHelper;

/**
 * Created by Pasindu Tennage on 2015-12-04.
 */
public class PersistentMemoryTransactionDAO implements TransactionDAO {
    private DBHelper db ;
    public PersistentMemoryTransactionDAO(){
        db = new DBHelper(AppContext.getContext());
    }
    @Override
    public void logTransaction(Date date, String accountNo, ExpenseType expenseType, double amount) {

        ContentValues contentValues = new ContentValues();
        contentValues.put("trans_date", date.toString());
        contentValues.put("accountno", accountNo);
        contentValues.put("expense", String.valueOf(expenseType));
        contentValues.put("amount", amount);
        SQLiteDatabase database = db.getDatabase();

        database.insert("transactions", null, contentValues);
        database.close();
    }

    public int numberOfRows(String tableName) {
        SQLiteDatabase database = db.getDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(database, tableName);
        database.close();
        return numRows;
    }


    public List<Transaction> getAllTransactionLogs() {
        List<Transaction> transactions = new LinkedList<>();
        SQLiteDatabase database = db.getDatabase();
        Cursor res = database.rawQuery("select * from transactions", null);
        res.moveToFirst();

        while (res.isAfterLast() == false) {
            ExpenseType type = null;
            if (res.getString(2).equals("EXPENSE")) {
                type = ExpenseType.EXPENSE;
            } else {
                type = ExpenseType.INCOME;
            }
            transactions.add(new Transaction(new Date(res.getString(0)), res.getString(1), type, Double.parseDouble(res.getString(3))));
            res.moveToNext();
        }
        database.close();
        return transactions;
    }


    @Override
    public List<Transaction> getPaginatedTransactionLogs(int limit) {
        int size = this.numberOfRows("transactions");
        if (size <= limit) {
            return getAllTransactionLogs();
        }

        return getAllTransactionLogs().subList(size - limit, size);
    }
}
