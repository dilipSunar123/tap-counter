package com.example.tapcounter;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.SystemClock;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

public class MainActivity extends AppCompatActivity {

    private TextView counterTextView;
    private Button tapBtn;
    private ImageView restartBtn;
    private Chronometer chronometer;
    private TextView scoreView;
    private TextView highScoreView;

    SQLiteDatabase database;
    HelperClass helper;

    int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        helper = new HelperClass(MainActivity.this);
        database = helper.getWritableDatabase();

        counterTextView = findViewById(R.id.counter);
        tapBtn = findViewById(R.id.tapBtn);
        restartBtn = findViewById(R.id.resetBtn);
        chronometer = findViewById(R.id.chronometer);
        scoreView = findViewById(R.id.scoreView);
        highScoreView = findViewById(R.id.highScore);

        restartBtn.setEnabled(false);

        tapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                incrementCount();
            }
        });
        restartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MainActivity.class);

                startActivity(intent);
                finish();
            }
        });

        Runnable stopMeter = new Runnable() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void run() {
                chronometer.stop();
                tapBtn.setEnabled(false);
                restartBtn.setEnabled(true);
                chronometer.setTextColor(getResources().getColor(R.color.timeUp));
                animateChronometerAfterTimeUp();
                scoreView.setText("Score : " + counterTextView.getText().toString());

                checkHighScore(counterTextView.getText().toString());
            }
        };
        chronometer.setBase(SystemClock.elapsedRealtime());
        chronometer.start();
        chronometer.postDelayed(stopMeter, 1000 * 21);

        Cursor fetchCursor  = database.rawQuery("SELECT * FROM " + CollectionClass.CollectionInnerClass.TABLE_NAME, null);

        if (fetchCursor.getCount() >= 1) {
            fetchCursor.moveToFirst();

            StringBuilder builder = new StringBuilder();
            do {
                builder.append(fetchCursor.getString(fetchCursor.getColumnIndex(CollectionClass.CollectionInnerClass.COLUMN_NAME)));
            }while (fetchCursor.moveToNext());

            highScoreView.setText("High Score : " + builder);
        } else {
            StringBuilder builder = new StringBuilder();
            builder.append("High Score : 0");

            highScoreView.setText(builder);
        }

    }

    public void incrementCount() {
        count++;
        counterTextView.setText(String.valueOf(count));
    }

    public void animateChronometerAfterTimeUp() {
        YoYo.with(Techniques.Shake)
                .duration(500)
                .repeat(1)
                .playOn(chronometer);
    }

    public void checkHighScore(String scoreVal) {
        Cursor checkCursor = database.rawQuery("SELECT * FROM " + CollectionClass.CollectionInnerClass.TABLE_NAME, null);

        if (checkCursor.getCount() == 0) {
            Log.d("testing", "adding highScore for the first time.");
            addHighScore(scoreVal);
        } else {
            Log.d("testing", "highScore already exists.");
            updateHighScore(scoreVal);
        }
    }

    public void addHighScore (String scoreVal) {
        Cursor addRawCursor = database.rawQuery("SELECT * FROM " + CollectionClass.CollectionInnerClass.TABLE_NAME, null);

        ContentValues cv = new ContentValues();
        cv.put(CollectionClass.CollectionInnerClass.COLUMN_NAME, scoreVal);
        database.insert(CollectionClass.CollectionInnerClass.TABLE_NAME, null, cv);

        addRawCursor.moveToFirst();

        StringBuilder builder = new StringBuilder();
        do {
            builder.append(addRawCursor.getString(addRawCursor.getColumnIndex(CollectionClass.CollectionInnerClass.COLUMN_NAME)));
        }while (addRawCursor.moveToNext());

        highScoreView.setText("High Score : " + builder);
    }

    public void updateHighScore (String scoreVal) {
        Cursor updateScoreCursor  = database.rawQuery("SELECT * FROM " + CollectionClass.CollectionInnerClass.TABLE_NAME,
                null);
        updateScoreCursor.moveToFirst();

        int presentHighScore = Integer.parseInt(updateScoreCursor.getString(updateScoreCursor.getColumnIndex(CollectionClass.CollectionInnerClass.COLUMN_NAME)));
        int newScore = Integer.parseInt(scoreVal);

        if (newScore > presentHighScore) {
            int id = 1;
            ContentValues cv = new ContentValues();
            cv.put(CollectionClass.CollectionInnerClass.COLUMN_NAME, scoreVal);

            database.update(CollectionClass.CollectionInnerClass.TABLE_NAME, cv,
                    CollectionClass.CollectionInnerClass._ID + "=?", new String[]{String.valueOf(id)});

            scoreView.setText("CONGRATULATIONS\nHigh Score : " + scoreVal);
            scoreView.setTextColor(getResources().getColor(R.color.newHighScoreColor));

            // Cheer the player up as its a highScore
            MediaPlayer player = MediaPlayer.create(MainActivity.this, R.raw.yay);
            player.start();
        }
        // Display the last highScore even though it is not a highScore (restarting the game).
        StringBuilder builder = new StringBuilder();
        do {
            builder.append(updateScoreCursor.getString(updateScoreCursor.getColumnIndex(CollectionClass.CollectionInnerClass.COLUMN_NAME)));
        }while (updateScoreCursor.moveToNext());

        highScoreView.setText("High Score : " + builder);
    }
}