package ir.itroid.databaselib;

import android.annotation.SuppressLint;
import android.app.Application;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import ir.itroid.easydatabase.DatabaseCore;
import ir.itroid.easydatabase.DatabaseTableQuery;
import ir.itroid.easydatabase.TABLE_TYPES;

@SuppressLint("StaticFieldLeak")
public class App extends Application {
    public static String DBG = "DBUG";

    @Override
    public void onCreate() {
        super.onCreate();
        initDB();
    }

    private void initDB() {
        new DatabaseCore(getApplicationContext(), "tasks", 1, new DatabaseCore.Callable() {
            @Override
            public void onCreate(SQLiteDatabase db) {
                String query = new DatabaseTableQuery("task").createTableIfNotExists()
                        .addColumn("domain", TABLE_TYPES.VARCHAR, false, null)
                        .addColumn("hash", TABLE_TYPES.VARCHAR, false, null)
                        .addColumn("ts", TABLE_TYPES.LONG, false, null)
                        .getQuery();
                db.execSQL(query);
                Log.i(DBG, "onCreate: " + query);
            }

            @Override
            public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

            }
        });
    }
}
