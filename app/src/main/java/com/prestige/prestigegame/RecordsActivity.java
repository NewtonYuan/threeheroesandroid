package com.prestige.prestigegame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class RecordsActivity extends AppCompatActivity {
    private boolean toPauseMusic = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_records);
        MainActivity.bgmPlayer.start();

        SharedPreferences prefs = this.getSharedPreferences("highScores", Context.MODE_PRIVATE);
        int highScoreMin = prefs.getInt("highScoreMin", 0);
        int highScoreSec = prefs.getInt("highScoreSec", 0);
        int totalEnemies = prefs.getInt("totalEnemiesDefeated", 0);
        int highestDPS = prefs.getInt("highestDPSLevel", 0);
        int highestHealer = prefs.getInt("highestHealerLevel", 0);
        int highestTank = prefs.getInt("highestTankLevel", 0);
        int totalCoins = prefs.getInt("totalCoinsCollected", 0);
        int totalEXP = prefs.getInt("totalEXPGained", 0);
        int highestCombined = prefs.getInt("highestCombinedLevel", 0);

        String a = String.format("%02d:%02d", highScoreMin, highScoreSec);
        String b = String.valueOf(totalEnemies);
        String c = String.valueOf(highestDPS);
        String d = String.valueOf(highestHealer);
        String e = String.valueOf(highestTank);
        String f = String.valueOf(totalCoins);
        String g = String.valueOf(totalEXP);
        String h = String.valueOf(highestCombined);

        TextView mTextView = (TextView) findViewById(R.id.a);
        mTextView.setText(a);
        mTextView = (TextView) findViewById(R.id.b);
        mTextView.setText(b);
        mTextView = (TextView) findViewById(R.id.c);
        mTextView.setText(c);
        mTextView = (TextView) findViewById(R.id.d);
        mTextView.setText(d);
        mTextView = (TextView) findViewById(R.id.e);
        mTextView.setText(e);
        mTextView = (TextView) findViewById(R.id.f);
        mTextView.setText(f);
        mTextView = (TextView) findViewById(R.id.g);
        mTextView.setText(g);
        mTextView = (TextView) findViewById(R.id.h);
        mTextView.setText(h);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (MainActivity.bgmPlayer != null) {
            if (MainActivity.bgmPlayer.isPlaying() && toPauseMusic) {
                MainActivity.bgmPlayer.pause();
            }
        }
        toPauseMusic = true;
    }

    public void backButtonClick(View view){
        super.onBackPressed();
        toPauseMusic = false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        toPauseMusic = false;
    }
}