package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import java.util.Date;
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
        if (expenseType == ExpenseType.EXPENSE){
            db.logTransaction(date.toString(),accountNo,"EXPENSE",(float)amount);
        }else{
            db.logTransaction(date.toString(),accountNo,"INCOME",(float)amount);
        }
    }

    @Override
    public List<Transaction> getAllTransactionLogs() {
        return db.getAllTransactions();
    }

    @Override
    public List<Transaction> getPaginatedTransactionLogs(int limit) {
        return db.getPaginatedTransactions(limit);
    }
}
