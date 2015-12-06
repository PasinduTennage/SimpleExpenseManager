package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.LinkedList;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.ui.AppContext;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.ui.DBHelper;

/**
 * Created by Pasindu Tennage on 2015-12-04.
 */
public class PersistentMemoryAccountDAO implements AccountDAO {
    private DBHelper db;
    public PersistentMemoryAccountDAO(){
        db = new DBHelper(AppContext.getContext());
    }
    @Override
    public List<String> getAccountNumbersList() {
        SQLiteDatabase database = db.getDatabase();
        List<String> list = new LinkedList<>();
        Cursor res = database.rawQuery("select accountno from accounts", null);
        res.moveToFirst();


        while (res.isAfterLast() == false) {

            list.add(res.getString(0));
            res.moveToNext();
        }
        database.close();
        return list;

    }

    @Override
    public List<Account> getAccountsList() {
        SQLiteDatabase database = db.getDatabase();
        List<Account> accounts = new LinkedList<>();
        Cursor res = database.rawQuery("select * from accounts", null);
        res.moveToFirst();

        while (res.isAfterLast() == false) {
            accounts.add(new Account(res.getString(0), res.getString(1), res.getString(2), Double.parseDouble(res.getString(3))));
            res.moveToNext();
        }
        database.close();
        return accounts;

    }

    @Override
    public Account getAccount(String accountNo) throws InvalidAccountException {
        SQLiteDatabase database = db.getDatabase();
        Cursor res = database.rawQuery("select * from accounts where accountno ='" + accountNo + "'", null);
        res.moveToFirst();

        while (res.isAfterLast() == false) {
            return new Account(res.getString(0), res.getString(1), res.getString(2), Double.parseDouble(res.getString(3)));

        }
        database.close();
        String msg = "Account " + accountNo + " is invalid.";
        throw new InvalidAccountException(msg);
    }

    @Override
    public void addAccount(Account account) {
        SQLiteDatabase database = db.getDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("accountno", account.getAccountNo());
        contentValues.put("bankname", account.getBankName());
        contentValues.put("accountholdername", account.getAccountHolderName());
        contentValues.put("balance", account.getBalance());
        database.insert("accounts", null, contentValues);
        database.close();

    }
    public boolean containsAccount(String accountNo) {
        SQLiteDatabase database = db.getDatabase();

        Cursor res = database.rawQuery("select * from accounts where accountno = '" + accountNo + "'", null);
        if (res.getCount() > 0) {
            return true;
        }
        db.close();
        return false;

    }

    @Override
    public void removeAccount(String accountNo) throws InvalidAccountException {
        if (!this.containsAccount(accountNo)) {
            String msg = "Account " + accountNo + " is invalid.";
            throw new InvalidAccountException(msg);
        }
        SQLiteDatabase database = db.getDatabase();
        database.delete("accounts",
                "accountno = ? ",
                new String[]{accountNo});
    }


    @Override
    public void updateBalance(String accountNo, ExpenseType expenseType, double amount) throws InvalidAccountException {
        if (!this.containsAccount(accountNo)) {
            String msg = "Account " + accountNo + " is invalid.";
            throw new InvalidAccountException(msg);
        }
        SQLiteDatabase databbase = db.getDatabase();
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
        databbase.update("accounts", contentValues, "accountno = ? ", new String[]{accountNo});
        databbase.close();
    }
}
