package edu.uci.moviequiz;

import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.Random;


public class QuizActivity extends ActionBarActivity {
    private TextView mTimeLabel;
    private Handler mHandler = new Handler();
    Intent intent;
    private long mStart;
    private static final long duration = 11000;
    Button submit;
    private StatDBAdapter stats = new StatDBAdapter(this);

    private Runnable updateTask = new Runnable() {
        public void run() {
            long now = SystemClock.uptimeMillis();
            long elapsed = duration - (now - mStart);

            if (elapsed > 0) {
                int seconds = (int) (elapsed / 1000);
                int minutes = seconds / 60;
                seconds     = seconds % 60;

                if (seconds < 10) {
                    mTimeLabel.setText("" + minutes + ":0" + seconds);
                } else {
                    mTimeLabel.setText("" + minutes + ":" + seconds);
                }

                 mHandler.postAtTime(this, now + 1000);
            }
            else {
                mTimeLabel.setTextColor(Color.RED);
                mTimeLabel.setText("Times up");
                mHandler.removeCallbacks(this);
                submit.setText("Next");
               // Intent intent = new Intent(QuizActivity.this, QuizActivity.class);
              //  startActivity(intent);
              // finish();
            }

        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);
        intent = new Intent(QuizActivity.this, QuizActivity.class);
        mTimeLabel = (TextView)this.findViewById(R.id.timeLabel);
        mStart = SystemClock.uptimeMillis();
        mHandler.post(updateTask);

        TextView question = (TextView)findViewById(R.id.quizQuest);

        QuizDBLoader loader = new QuizDBLoader(this);
        final RadioButton []radio = new RadioButton[4];
        Random r = new Random();
        int selection=r.nextInt(5)+1;

        QuizDBLoader.QuestAns questAns = loader.makeQuest(4);
        String option[] = loader.getOptions(selection);
        question.setText(questAns.getQuestion());
        radio[0]= (RadioButton)findViewById(R.id.radioChoice1);
        radio[0].setText(option[0]);
        radio[1]= (RadioButton)findViewById(R.id.radioChoice2);
        radio[1].setText(option[1]);
        radio[2] =(RadioButton)findViewById(R.id.radioChoice3);
        radio[2].setText(option[2]);
        radio[3]= (RadioButton)findViewById(R.id.radioChoice4);
        radio[3].setText(option[3]);
        final int ans_no=r.nextInt(3);
        radio[ans_no].setText(questAns.getAnswer());
        final TextView descion = (TextView)findViewById(R.id.decision);
        descion.setText("");
        descion.setTextSize(25);
        submit = (Button)findViewById(R.id.submit);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(radio[ans_no].isChecked()){
                    descion.setTextColor(Color.GREEN); descion.setText("correct");
                    stats.updateDB(true,6);
                }
                else {
                    descion.setText("incorrect");
                    descion.setTextColor(Color.RED);
                    stats.updateDB(false,6);
                }

                new Handler().postDelayed(new Runnable()  {
                    @Override
                    public void run() {
                        mHandler.removeCallbacks(updateTask);
                        mHandler.removeCallbacks(this);
                        startActivity(intent);

                finish();
                    }
                }, 1000);
            }
        });


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_quiz, menu);
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
