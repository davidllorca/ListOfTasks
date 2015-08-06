package com.davidllorca.listoftasks;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by David Llorca <davidllorcabaron@gmail.com> on 7/14/14.
 */
public class DataBaseHelper {

    // Variables
    private Context mContext = null;
    private DataBaseHelperInternal mDBHelper = null;
    private SQLiteDatabase mDb = null;

    //Constants
    private static final String DATABASE_NAME = "TODOLIST";
    private static final int DATABASE_VERSION = 1;
    // Table's fields
    private static final String DATABASE_TABLE_TODOLIST = "todolist";
    public static final String SL_ID = "_id";
    public static final String SL_ITEM = "task";
    public static final String SL_PLACE = "place";
    public static final String SL_IMPORTANCE = "importance";
    public static final String SL_DESCRIPTION = "description";
    // Query for create table
    private static final String DATABASE_CREATE_TODOLIST_QUERY = "CREATE TABLE "
            + DATABASE_TABLE_TODOLIST + " (" + SL_ID + " INTEGER PRIMARY KEY,"
            + SL_ITEM + " TEXT NOT NULL," + SL_PLACE + " TEXT NOT NULL,"
            + SL_IMPORTANCE + " INTEGER NOT NULL," + SL_DESCRIPTION + " TEXT)";

    // Constructor
    public DataBaseHelper(Context mContext) {
        this.mContext = mContext;
    }

    /**
     * Open database SQLite in mode writable.
     *
     * @return
     */
    public DataBaseHelper open() {
        mDBHelper = new DataBaseHelperInternal(mContext);
        mDb = mDBHelper.getWritableDatabase();
        return this;
    }

    /**
     * Close database SQLite.
     */
    public void close() {
        mDBHelper.close();
    }

    /**
     * Return all items of table ordered by SL_IMPORTANCE.
     *
     * @return Cursor object. Positioned before first entry.
     */
    public Cursor getItems() {
        // (table, columns, where, selectionArgs, groupBy, having, orderBy)
        return mDb.query(DATABASE_TABLE_TODOLIST, new String[]{SL_ID, SL_ITEM, SL_PLACE, SL_IMPORTANCE, SL_DESCRIPTION}, null, null, null, null, SL_IMPORTANCE);
    }

    /**
     * Insert item in DATABASE_TABLE_TODOLIST.
     *
     * @param item
     * @param place
     * @param description
     * @param importance
     * @return the row ID of the newly inserted row, or -1 if an error occurred
     */
    public long insertItem(String item, String place, String description, int importance) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(SL_ITEM, item);
        initialValues.put(SL_PLACE, place);
        initialValues.put(SL_DESCRIPTION, description);
        initialValues.put(SL_IMPORTANCE, importance);
        // Convenience method for inserting a row into the database.
        return mDb.insert(DATABASE_TABLE_TODOLIST, null, initialValues);
    }

    public int deleteItem(int itemId) {
        /*
            @param table the table to delete from
            @param whereClause the optional WHERE clause to apply when deleting. Passing null will
            delete all rows.
            @param whereArgs You may include ?s in the where clause, which will be replaced by the
            values from whereArgs. The values will be bound as Strings.
            @return the number of rows affected if a whereClause is passed in, 0 otherwise. To remove
            all rows and get a count pass "1" as the whereClause.
        */
        return mDb.delete(DATABASE_TABLE_TODOLIST, SL_ID + "=?", new String[]{Integer.toString(itemId)});
    }

    public Cursor getItem(int itemId) {
        /*
            @param sql the SQL query. The SQL string must not be ; terminated
            @param selectionArgs You may include ?s in where clause in the query, which will be
             replaced by the values from selectionArgs. The values will be bound as Strings.
            @return A {@link Cursor} object, which is positioned before the first entry.
         */
        return mDb.rawQuery("SELECT " + SL_ITEM + ","
                        + SL_PLACE + ","
                        + SL_DESCRIPTION + ","
                        + SL_IMPORTANCE + ","
                        + SL_ID + " FROM "
                        + DATABASE_TABLE_TODOLIST
                        + " WHERE " + SL_ID + "=?",
                new String[]{Integer.toString(itemId)});
    }

    public int updateItem(int itemId, String item, String place, String description, int importance){
        ContentValues cv = new ContentValues();
        cv.put(SL_ITEM, item);
        cv.put(SL_PLACE, place);
        cv.put(SL_DESCRIPTION, description);
        cv.put(SL_IMPORTANCE, importance);
        return mDb.update(DATABASE_TABLE_TODOLIST, cv,SL_ID + "=?", new String[]{Integer.toString(itemId)});
    }

    /*
        Private class for SQLite control.
     */
    private static class DataBaseHelperInternal extends SQLiteOpenHelper {

        // Constructor
        public DataBaseHelperInternal(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            createTables(db);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            deleteTables(db);
            createTables(db);
        }

        // Methods
        private void createTables(SQLiteDatabase db) {
            db.execSQL(DATABASE_CREATE_TODOLIST_QUERY);
        }

        private void deleteTables(SQLiteDatabase db) {
            db.execSQL("DROP TABLE IF EXIST " + DATABASE_TABLE_TODOLIST);
        }
    }
}
