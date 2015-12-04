package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

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
        return db.getAccountNumbersList();
    }

    @Override
    public List<Account> getAccountsList() {
        return db.getAccountsList();
    }

    @Override
    public Account getAccount(String accountNo) throws InvalidAccountException {
        return db.getAccount(accountNo);
    }

    @Override
    public void addAccount(Account account) {
        db.addAccount(account);
    }

    @Override
    public void removeAccount(String accountNo) throws InvalidAccountException {
        db.removeAccount(accountNo);
    }

    @Override
    public void updateBalance(String accountNo, ExpenseType expenseType, double amount) throws InvalidAccountException {
        db.updateBalance(accountNo, expenseType, amount);
    }
}
