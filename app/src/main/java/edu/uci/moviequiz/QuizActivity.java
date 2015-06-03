package edu.uci.moviequiz;

import android.content.Intent;
import android.content.SharedPreferences;
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
    private static  long duration = 11000;
    Button submit;
    private TextView question;
    QuizDBLoader loader;
    RadioButton []radio = new RadioButton[4];
    int timer;
    int ans_no;
    Bundle savedInstanceState_local;
    long elapsed;
    private Runnable updateTask = new Runnable() {
        public void run() {
            long now = SystemClock.uptimeMillis();
            if(savedInstanceState_local!=null)
                duration=savedInstanceState_local.getLong("elapsed");
           /* else */
                elapsed = duration - (now - mStart);

            if (elapsed > 0) {
                int seconds = (int) (elapsed / 1000);
                int minutes = seconds / 60;
                seconds     = seconds % 60;
                timer=seconds;
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
        question = (TextView) findViewById(R.id.quizQuest);
        intent = new Intent(QuizActivity.this, QuizActivity.class);
        savedInstanceState_local=savedInstanceState;
        duration=11000;
        mTimeLabel = (TextView) this.findViewById(R.id.timeLabel);
        mStart = SystemClock.uptimeMillis();
        mHandler.post(updateTask);

        if(savedInstanceState ==null) {
            question = (TextView) findViewById(R.id.quizQuest);
            loader = new QuizDBLoader(this);

            Random r = new Random();
            int selection = r.nextInt(5) + 1;

            QuizDBLoader.QuestAns questAns = loader.makeQuest(selection);
            String option[] = loader.getOptions(selection,questAns.getAnswer());
            question.setText(questAns.getQuestion());
            radio[0] = (RadioButton) findViewById(R.id.radioChoice1);
            radio[0].setText(option[0]);
            radio[1] = (RadioButton) findViewById(R.id.radioChoice2);
            radio[1].setText(option[1]);
            radio[2] = (RadioButton) findViewById(R.id.radioChoice3);
            radio[2].setText(option[2]);
            radio[3] = (RadioButton) findViewById(R.id.radioChoice4);
            radio[3].setText(option[3]);
            ans_no = r.nextInt(3);
            radio[ans_no].setText(questAns.getAnswer());
        }else {
            question.setText(savedInstanceState.getString("question"));
            radio[0] = (RadioButton) findViewById(R.id.radioChoice1);
            radio[1] = (RadioButton) findViewById(R.id.radioChoice2);
            radio[2] = (RadioButton) findViewById(R.id.radioChoice3);
            radio[3] = (RadioButton) findViewById(R.id.radioChoice4);
            radio[0].setText(savedInstanceState.getString("option1"));
            radio[1].setText(savedInstanceState.getString("option2"));
            radio[2].setText(savedInstanceState.getString("option3"));
            radio[3].setText(savedInstanceState.getString("option4"));
            ans_no =savedInstanceState.getInt("ans_no");

        }
        final TextView descion = (TextView)findViewById(R.id.decision);
        descion.setText("");
        descion.setTextSize(25);
        submit = (Button)findViewById(R.id.submit);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(radio[ans_no].isChecked()){
                    descion.setTextColor(Color.GREEN); descion.setText("correct");
                }
                else {
                    descion.setText("incorrect");
                    descion.setTextColor(Color.RED);
                }

                new Handler().postDelayed(new Runnable()  {
                    @Override
                    public void run() {
                        mHandler.removeCallbacks(updateTask);
                        mHandler.removeCallbacks(this);
                        duration=11000;
                        startActivity(intent);

                finish();
                    }
                }, 1000);
            }
        });


    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        // Save UI state changes to the savedInstanceState.
        // This bundle will be passed to onCreate if the process is
        // killed and restarted.

        savedInstanceState.putInt("time", timer);
        savedInstanceState.putInt("ans_no", ans_no);
        savedInstanceState.putString("question", question.getText().toString());
        savedInstanceState.putString("option1", radio[0].getText().toString());
        savedInstanceState.putString("option2", radio[1].getText().toString());
        savedInstanceState.putString("option3", radio[2].getText().toString());
        savedInstanceState.putString("option4", radio[3].getText().toString());
        savedInstanceState.putLong("elapsed", elapsed);
        // etc.
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
