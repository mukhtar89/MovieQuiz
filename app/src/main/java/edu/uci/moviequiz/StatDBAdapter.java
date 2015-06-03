package edu.uci.moviequiz;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by Mukhtar on 5/26/2015.
 */
public class StatDBAdapter extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "mydb";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "STATS";
    private static final String STAT_ID = "_id";
    private static final String SCORE = "score";
    private static final String ATTEMPT = "attempt";
    private static final String CORRECT = "correct";
    private static final String INCORRECT = "incorrect";
    private static final String SPENT = "spent";
    private static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS "
            + TABLE_NAME + "("
            + STAT_ID +" integer primary key autoincrement, "
            + SCORE +" integer not null, "
            + ATTEMPT +" integer not null, "
            + CORRECT +" integer not null, "
            + INCORRECT +" integer not null, "
            + SPENT +" integer not null);";
    private static final String SELECT_TABLE = "SELECT * FROM STATS;";
    private SQLiteDatabase mDb;
    private Context mContext;

    public StatDBAdapter(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
        this.mDb = getWritableDatabase();
        this.onCreate(mDb);
    }

    @Override
    public void onCreate (SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
        Cursor curr = fetchAll();
        if (curr.getCount() == 0) {
            ContentValues values = new ContentValues();
            values.put("SCORE",0);
            values.put("ATTEMPT",0);
            values.put("CORRECT",0);
            values.put("INCORRECT",0);
            values.put("SPENT",0);
            db.insert(TABLE_NAME, null, values);
        }
    }

    public void resetDB() {
        mDb.execSQL("DELETE FROM " + TABLE_NAME);
    }

    public void updateDB (boolean correct, int spent) {
        Cursor curr = fetchAll();
        if (curr.moveToFirst()) {
            ContentValues values = new ContentValues();
            values.put("SCORE", correct ? curr.getInt(0) + 5 : curr.getInt(0) - 1);
            values.put("ATTEMPT",curr.getInt(1) + 1);
            values.put("CORRECT",correct ? curr.getInt(2) + 1 : curr.getInt(2));
            values.put("INCORRECT",correct ? curr.getInt(3) : curr.getInt(3) + 1);
            values.put("SPENT",curr.getInt(4) + (int) spent/1000);
            mDb.update(TABLE_NAME, values, null, null);
        }
    }

    @Override
    public void onUpgrade (SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public Cursor fetchAll() {
        return mDb.query(TABLE_NAME, new String[] {SCORE, ATTEMPT, CORRECT, INCORRECT, SPENT}, null, null, null, null, null);
    }
}
