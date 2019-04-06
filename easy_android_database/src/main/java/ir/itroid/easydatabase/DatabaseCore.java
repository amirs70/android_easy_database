package ir.itroid.easydatabase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseCore extends SQLiteOpenHelper {

    public static SQLiteDatabase DB;
    private Callable callable;

    public DatabaseCore(Context context, String name, int version, Callable callable) {
        super(context, name, null, version);
        this.callable = callable;
        DB = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        callable.onCreate(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        callable.onUpgrade(db, oldVersion, newVersion);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        super.onDowngrade(db, oldVersion, newVersion);
        callable.onDowngrade(db, oldVersion, newVersion);
    }

    /*private void runQueries(SQLiteDatabase db) {
        if (TABLE_SCHEMA.size() > 0) {
            db.beginTransaction();
            for (String query : TABLE_SCHEMA) {
                db.execSQL(query);
            }
            db.setTransactionSuccessful();
            db.endTransaction();
        }
    }*/

    public interface Callable {
        void onCreate(SQLiteDatabase db);

        void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion);

        default void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }
}
