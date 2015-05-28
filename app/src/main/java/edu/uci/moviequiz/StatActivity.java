package edu.uci.moviequiz;

import android.database.Cursor;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;


public class StatActivity extends ActionBarActivity {

    private TextView score, attempt, correct, incorrect, spent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stat);

        score = (TextView) findViewById(R.id.scoreValue);
        attempt = (TextView) findViewById(R.id.attemptValue);
        correct = (TextView) findViewById(R.id.correctValue);
        incorrect = (TextView) findViewById(R.id.incorrectValue);
        spent = (TextView) findViewById(R.id.spentValue);
        StatDBAdapter db = new StatDBAdapter(this);
        Cursor cur = db.fetchAll();

        cur.moveToFirst();
        while (!cur.isAfterLast()) {
            score.setText(cur.getString(0));
            attempt.setText(cur.getString(1));
            correct.setText(cur.getString(2));
            incorrect.setText(cur.getString(3));
            spent.setText(cur.getString(4));
            cur.moveToNext();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_stat, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}