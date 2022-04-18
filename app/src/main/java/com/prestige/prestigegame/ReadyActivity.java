package com.prestige.prestigegame;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ReadyActivity extends AppCompatActivity implements View.OnTouchListener{
    private boolean toPauseMusic = true;
    String msg;
    private android.widget.RelativeLayout.LayoutParams layoutParams;
    int x_cord;
    int y_cord;
    private int charactersSelected = 0;
    private View v;
    long then = 0;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ready);

        ImageView background = (ImageView) findViewById(R.id.background);
        background.setColorFilter(ContextCompat.getColor(this, R.color.blackTint), PorterDuff.Mode.SRC_OVER);

        ImageView dpsChar = findViewById(R.id.dpsCharacter);
        dpsChar.setTag(R.id.characterRole, 0);
        dpsChar.setTag(R.id.characterImage, R.drawable.damageidle);
        dpsChar.setOnTouchListener(this);

        ImageView healerChar = findViewById(R.id.healerCharacter);
        healerChar.setTag(R.id.characterRole, 1);
        healerChar.setTag(R.id.characterImage, R.drawable.healer);
        healerChar.setOnTouchListener(this);

        ImageView tankChar = findViewById(R.id.tankCharacter);
        tankChar.setTag(R.id.characterRole, 2);
        tankChar.setTag(R.id.characterImage, R.drawable.tank);
        tankChar.setOnTouchListener(this);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                return false;
            case MotionEvent.ACTION_UP:
                int newImage = Integer.parseInt(String.valueOf(v.getTag(R.id.characterImage)));
                int chosenRole = Integer.parseInt(String.valueOf(v.getTag(R.id.characterRole)));
                if (chosenRole == 0){
                    charactersSelected++;
                    ImageView frame = (ImageView) findViewById(R.id.dpsFrame);
                    frame.setBackgroundResource(newImage);
                    return true;
                }
                if (chosenRole == 1){
                    charactersSelected++;
                    ImageView frame = (ImageView) findViewById(R.id.healerFrame);
                    frame.setBackgroundResource(newImage);
                    return true;
                }
                if (chosenRole == 2){
                    charactersSelected++;
                    ImageView frame = (ImageView) findViewById(R.id.tankFrame);
                    frame.setBackgroundResource(newImage);
                    return true;
                }
        }
        return false;
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
        startActivity(new Intent(ReadyActivity.this, MainActivity.class));
        toPauseMusic = true;
    }

    public void heroSelect(View view){
    }

    public void startButtonClick(View view){
        if (charactersSelected < 3){
            Toast.makeText(this, "Please select ALL 3 heroes!", Toast.LENGTH_SHORT).show();
        } else {
            startActivity(new Intent(ReadyActivity.this, GameActivity.class));
            toPauseMusic = true;
        }
    }

    public void resetButtonClick(View view){
        super.recreate();
        toPauseMusic = false;
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(ReadyActivity.this, MainActivity.class));
        toPauseMusic = true;
    }
}