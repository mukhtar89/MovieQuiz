package edu.uci.moviequiz;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.PrivateKey;
import java.util.Random;

/**
 * Created by Mukhtar on 5/26/2015.
 */
public class QuizDBLoader extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "mydb";
    private static final int DATABASE_VERSION = 1;
    private static final String[] TABLE_NAME = new String[] {"movies", "stars", "stars_in_movies"};
    private static final int MOVIE_COUNT=243;
    private static final int STAR_COUNT=243;
    private static final int STARS_IN_MOVIES_COUNT=133;
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
        while (iter < 3) {
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
 public  String[] getOptions(int option, String solution){
     String [] options= new String[4];
     int i = 0;
     Random r;
     int ran;
     Cursor cs;
     String col="",table="";
     switch (option) {

         case 4:
             col = "title";
             table = "movies";
          break;
         case 1:
         case 6:
         case 5:
             col = "director";
             table = "movies";
             break;
         case 2:
             String exp="";
             for (int j = 0; j < 4; j++) {

                 cs = mDb.rawQuery("select year from movies where year!='"+solution+"'" + exp + " limit 1 ", null);
                 cs.moveToFirst();
                 while (!cs.isAfterLast()) {
                     options[i] = Integer.toString(cs.getInt(0));
                     exp=exp+" and year !='"+options[i]+"'";
                     cs.moveToNext();
                 }
                 i++;
             }
             return options;

         case 7:
         case 3:
             for (int k = 0; k < 4; k++) {
                 r = new Random();
                 ran = r.nextInt(240);
                 String []temp = solution.split(" ");
                 cs = mDb.rawQuery("select first_name, last_name from stars  where first_name not like '"+temp[0]+"' and last_name not like'"+temp[1]+"' limit 1 offset " + ran, null);
                 cs.moveToFirst();
                 while (!cs.isAfterLast()) {
                     options[i] = cs.getString(0).replace("\"", "")+" "+cs.getString(1).replace("\"", "");

                     cs.moveToNext();
                 }
                 i++;
             }
             return options;

     }

         //  mDb.execSQL(query);
             for (int l = 0; l < 4; l++) {
             r = new Random() ;
             ran = r.nextInt(230);
             cs = mDb.rawQuery("select " + col + " from " + table + " where " + col + " not like '" + solution.replaceAll("'","''") + "' limit 1 offset " + ran, null);

             cs.moveToFirst();
             while (!cs.isAfterLast()) {
                 options[i] = cs.getString(0).replace('"', ' ');

                 cs.moveToNext();
             }
             i++;
         }

     return options;
 }


    @Override
    public void onUpgrade (SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
    public  Cursor queryRunner(String query, int count){
        Random r = new Random();
        int ran=0;
        if(count!=0)
         ran = r.nextInt(count);


        Cursor cs = mDb.rawQuery( query+" limit 1 offset " + ran,null);
        cs.moveToFirst();
        return cs;
    }
    public QuestAns makeQuest(int select){
        QuestAns qs = new QuestAns();
        Cursor cur ;
        String query;
        switch (select){
            case 1:
            cur=queryRunner("select title,director from movies",MOVIE_COUNT);
            qs.setQuestion("Who directed the movie " + cur.getString(0).replace("\"","") + "?");
            qs.setAnswer(cur.getString(1).replace('"', ' '));
            break;
            case 2:
                cur=queryRunner("select title,year from movies",MOVIE_COUNT);
                qs.setQuestion("When was the movie " + cur.getString(0).replace("\"","") + " released ?");
                qs.setAnswer(cur.getString(1).replace('"', ' '));
                break;
            case 3:
                cur=queryRunner("select title,first_name, last_name from movies  join stars  ,stars_in_movies  where movies._id=stars_in_movies.movie_id and stars_in_movies.star_id=stars._id ",MOVIE_COUNT);
                qs.setQuestion("Which star was in the movie "+cur.getString(0).replace("\"", "")+" ?");
                qs.setAnswer(cur.getString(1).replace("\"", "") + " " + cur.getString(2).replace("\"", ""));
                break;
            case 4:

                query="select s1.first_name, s1.last_name, s2.first_name,s2.last_name, m.title " +
                        "from stars_in_movies sm1, stars_in_movies sm2, stars s1, stars s2, movies m " +
                        "where sm1.star_id not like sm2.star_id " +
                        "and sm1.movie_id = sm2.movie_id " +
                        "and sm1.star_id = s1._id " +
                        "and sm2.star_id = s2._id " +
                        "and sm1.movie_id = m._id ";

                qs.setQuestion(query);

                 cur= queryRunner(query, 1160);
                 if( cur.moveToFirst() ) {
                    qs.setQuestion("In which movie the stars " + cur.getString(0).replace("\"", "") + " " + cur.getString(1).replace("\"", "") + " and  " + cur.getString(2).replace("\"", "") + " " + cur.getString(3).replace("\"", "") + " appear together?");
                    qs.setAnswer(cur.getString(4).replace("\"","") );
                    cur.moveToNext();
                      //qs.setQuestion("Which star was in the movie " + cur.getString(0).replace('"', ' ') + cur.getString(1).replace('"', ' ') + cur.getString(2).replace('"', ' ') + " and " + cur2.getString(1).replace('"', ' ') + cur2.getString(2).replace('"', ' ') + " appear together?");
                }
                 break;
            case 5:
                cur=queryRunner("select director,first_name, last_name from movies  join stars  ,stars_in_movies  where movies._id=stars_in_movies.movie_id and stars_in_movies.star_id=stars._id ", MOVIE_COUNT);
                qs.setQuestion("Who directed the star " + cur.getString(1).replace("\"","")+" "+ cur.getString(2).replace("\"","")+"?");
                qs.setAnswer(cur.getString(0).replace("\"",""));
                break;
            case 6:
                cur=queryRunner("select director,first_name, last_name,year from movies  join stars  ,stars_in_movies  where movies._id=stars_in_movies.movie_id and stars_in_movies.star_id=stars._id ", MOVIE_COUNT);
                qs.setQuestion("Who directed the star " + cur.getString(1).replace("\"","")+" "+ cur.getString(2).replace("\"","")+" in year "+cur.getInt(3)+"?");
                qs.setAnswer(cur.getString(0).replace("\"",""));
                break;
            case 7:
                query="select s1.first_name, s1.last_name, m.title, m1.title " +
                        "from stars_in_movies sm1, stars_in_movies sm2, stars s1, stars s2, movies m, movies m1 " +
                        "where sm1.star_id = sm2.star_id " +
                        "and sm1.movie_id not like sm2.movie_id " +
                        "and sm1.star_id = s1._id " +
                        "and sm2.star_id = s2._id " +
                        "and sm1.movie_id = m._id "+
                        "and sm2.movie_id = m1._id";

                qs.setQuestion(query);

                cur= queryRunner(query, 1179);
                if( cur.moveToFirst() ) {
                    qs.setQuestion("Which star appears in both movies " + cur.getString(2).replace("\"", "") + " and " + cur.getString(3).replace("\"", "") + " ?");
                    qs.setAnswer(cur.getString(0).replace("\"","")+" "+cur.getString(1).replace("\"",""));
                    cur.moveToNext();
                    //qs.setQuestion("Which star was in the movie " + cur.getString(0).replace('"', ' ') + cur.getString(1).replace('"', ' ') + cur.getString(2).replace('"', ' ') + " and " + cur2.getString(1).replace('"', ' ') + cur2.getString(2).replace('"', ' ') + " appear together?");
                }
                break;
        }
        return qs;
    }

    public class QuestAns{
        String question;
        String answer;
        public String getQuestion(){
            return question;
        }

        public String getAnswer() {
            return answer;
        }
        public void setQuestion(String ques){
            question=ques;
        }
        public void  setAnswer(String ans){
            answer=ans;
        }
    }
}
