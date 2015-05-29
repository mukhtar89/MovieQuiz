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
public class QuizDBLoader extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "mydb";
    private static final int DATABASE_VERSION = 1;
    private static final String[] TABLE_NAME = new String[] {"movies", "stars", "stars_in_movies"};

    private static final String ID = "_id";
    private static final String TITLE = "title";
    private static final String YEAR = "year";
    private static final String DIRECTOR = "director";
    private static final String FNAME = "first_name";
    private static final String LNAME = "last_name";
    private static final String DOB = "dob";
    private static final String STARID = "star_id";
    private static final String MOVIEID = "movie_id";

    private static final String[][] COLUMNS = new String [][] {{ID, TITLE, YEAR, DIRECTOR},
            {ID, FNAME, LNAME, DOB}, {STARID, MOVIEID}};

    private static final String[] CREATE_TABLE = new String[] {"CREATE TABLE IF NOT EXISTS movies ("
            + ID +" integer primary key autoincrement, "
            + TITLE +" text not null, "
            + YEAR +" integer not null, "
            + DIRECTOR +" text not null);",
            "CREATE TABLE IF NOT EXISTS stars ("
                    + ID +" integer primary key autoincrement, "
                    + FNAME +" text not null, "
                    + LNAME +" text not null, "
                    + DOB +" text not null);",
            "CREATE TABLE IF NOT EXISTS stars_in_movies ("
                    + STARID +" integer not null, "
                    + MOVIEID +" integer not null);"};

    private SQLiteDatabase mDb;
    private Context mContext;

    public QuizDBLoader(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
        this.mDb = getWritableDatabase();
        this.onCreate(mDb);
    }

    @Override
    public void onCreate (SQLiteDatabase db) {
        int iter = 0, iter1 = 0;
        while (iter != 3) {
            db.execSQL(CREATE_TABLE[iter]);
            Cursor curr = db.query(TABLE_NAME[iter], COLUMNS[iter], null, null, null, null, null);
            if (curr.getCount() == 0) {
                try {
                    BufferedReader in = new BufferedReader(new InputStreamReader(mContext.getAssets().open(TABLE_NAME[iter] + ".csv")));
                    String line;

                    while((line=in.readLine())!=null) {
                        ContentValues values = new ContentValues();
                        String[] columns = line.split(",");
                        iter1 = 0;
                        while (iter1 != COLUMNS[iter].length) {
                            values.put(COLUMNS[iter][iter1], columns[iter1]);
                            iter1++;
                        }
                        db.insert(TABLE_NAME[iter], null, values);
                    }
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            iter++;
        }
    }

    @Override
    public void onUpgrade (SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}
