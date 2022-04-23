package com.example.basicbudgetmanager;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;

import com.example.basicbudgetmanager.models.Expense;
import com.example.basicbudgetmanager.models.Income;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@Database(entities ={Income.class, Expense.class},version = 1,exportSchema = false)
public abstract class BudgetRoomDatabase extends RoomDatabase {


    public abstract BudgetDAO budgetDAO();
    public static volatile BudgetRoomDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS=4;
    static final ExecutorService databaseWriteExecutor= Executors.newFixedThreadPool(NUMBER_OF_THREADS);


    static BudgetRoomDatabase getDatabase(final Context context){

        if(INSTANCE==null){
            synchronized (BudgetRoomDatabase.class){
                if(INSTANCE==null){
                    INSTANCE= Room.databaseBuilder(context,BudgetRoomDatabase.class,"budget_database")
                            .addCallback(new RoomDatabase.Callback(){


                                @Override
                                public void onOpen(@NonNull SupportSQLiteDatabase db) {
                                    super.onOpen(db);

                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            BudgetDAO mDao=INSTANCE.budgetDAO();
                                            mDao.deleteAllIncome();
                                            mDao.deleteAllExpense();

                                            Income income=new Income("Yahoo",2000);
                                            mDao.insertIncome(income);

                                            Expense expense=new Expense("Urgent 2k",2000);
                                            mDao.insertExpense(expense);

                                        }
                                    }).start();





                                }
                            }).build();
                }
            }

        }


return INSTANCE;
    }

    @NonNull
    @Override
    protected SupportSQLiteOpenHelper createOpenHelper(DatabaseConfiguration config) {
        return null;
    }

    @NonNull
    @Override
    protected InvalidationTracker createInvalidationTracker() {
        return null;
    }

    @Override
    public void clearAllTables() {

    }
}
