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

    public boolean logTransaction(String trans_date, String accountNo, String expense, float amount) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("trans_date", trans_date);
        contentValues.put("accountno", accountNo);
        contentValues.put("expense", expense);
        contentValues.put("amount", amount);
        db.insert("transactions", null, contentValues);
        db.close();
        return true;
    }


    public int numberOfRows(String tableName) {
        SQLiteDatabase db = getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, tableName);
        db.close();
        return numRows;
    }


    public List<Transaction> getAllTransactions() {
        List<Transaction> transactions = new LinkedList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor res = db.rawQuery("select * from transactions", null);
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
        db.close();
        return transactions;
    }

    public List<Transaction> getPaginatedTransactions(int limit) {
        int size = this.numberOfRows("transactions");
        if (size <= limit) {
            return getAllTransactions();
        }

        return getAllTransactions().subList(size - limit, size);
    }

    public List<String> getAccountNumbersList() {
        List<String> list = new LinkedList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select accountno from accounts", null);
        res.moveToFirst();

        while (res.isAfterLast() == false) {

            list.add(res.getString(0));
            res.moveToNext();
        }
        db.close();
        return list;
    }

    public List<Account> getAccountsList() {
        List<Account> accounts = new LinkedList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor res = db.rawQuery("select * from accounts", null);
        res.moveToFirst();

        while (res.isAfterLast() == false) {
            accounts.add(new Account(res.getString(0), res.getString(1), res.getString(2), Double.parseDouble(res.getString(3))));
            res.moveToNext();
        }
        db.close();
        return accounts;
    }

    public Account getAccount(String accountNo) throws InvalidAccountException {

        SQLiteDatabase db = getReadableDatabase();

        Cursor res = db.rawQuery("select * from accounts where accountno ='" + accountNo + "'", null);
        res.moveToFirst();

        while (res.isAfterLast() == false) {
            return new Account(res.getString(0), res.getString(1), res.getString(2), Double.parseDouble(res.getString(3)));

        }
        db.close();
        String msg = "Account " + accountNo + " is invalid.";
        throw new InvalidAccountException(msg);

    }

    public boolean addAccount(Account account) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("accountno", account.getAccountNo());
        contentValues.put("bankname", account.getBankName());
        contentValues.put("accountholdername", account.getAccountHolderName());
        contentValues.put("balance", account.getBalance());
        db.insert("accounts", null, contentValues);
        db.close();
        return true;
    }

    public boolean containsAccount(String accountNo) {
        SQLiteDatabase db = getReadableDatabase();

        Cursor res = db.rawQuery("select * from accounts where accountno = '" + accountNo + "'", null);
        if (res.getCount() > 0) {
            return true;
        }
        db.close();
        return false;

    }

    public Integer removeAccount(String accountNo) throws InvalidAccountException {
        if (!this.containsAccount(accountNo)) {
            String msg = "Account " + accountNo + " is invalid.";
            throw new InvalidAccountException(msg);
        }
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("accounts",
                "accountno = ? ",
                new String[]{accountNo});
    }

    public void updateBalance(String accountNo, ExpenseType expenseType, double amount) throws InvalidAccountException {
        if (!this.containsAccount(accountNo)) {
            String msg = "Account " + accountNo + " is invalid.";
            throw new InvalidAccountException(msg);
        }
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();


        Account account = this.getAccount(accountNo);

        switch (expenseType) {
            case EXPENSE:
                contentValues.put("balance", account.getBalance() - amount);

                account.setBalance(account.getBalance() - amount);
                break;
            case INCOME:
                contentValues.put("balance", account.getBalance() + amount);
                break;
        }
        db.update("accounts", contentValues, "accountno = ? ", new String[]{accountNo});
        db.close();
    }


}
