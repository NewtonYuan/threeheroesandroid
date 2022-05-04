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

    private boolean perlaHeroUnlocked;
    private boolean alfredHeroUnlocked;
    private boolean erinaHeroUnlocked;

    public boolean selectedDPS = false;
    public boolean selectedSupport = false;
    public boolean selectedTank = false;
    private View v;
    long then = 0;

    //Characters
    public static boolean damageHeroSelected = false;
    public static boolean healerHeroSelected = false;
    public static boolean tankHeroSelected = false;
    public static boolean perlaHeroSelected = false;
    public static boolean alfredHeroSelected = false;
    public static boolean erinaHeroSelected = false;


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ready);


        ImageView background = (ImageView) findViewById(R.id.background);
        background.setColorFilter(ContextCompat.getColor(this, R.color.blackTint), PorterDuff.Mode.SRC_OVER);

        SharedPreferences characters = this.getSharedPreferences("characters", Context.MODE_PRIVATE);
        perlaHeroUnlocked = characters.getBoolean("perla", false);
        alfredHeroUnlocked = characters.getBoolean("alfred", false);
        erinaHeroUnlocked = characters.getBoolean("erina", false);

        ImageView dpsChar = findViewById(R.id.dpsCharacter);
        dpsChar.setTag(R.id.characterRole, 0);
        dpsChar.setTag(R.id.characterImage, R.drawable.damageidle);
        dpsChar.setOnTouchListener(this);

        ImageView erinaChar = (ImageView) findViewById(R.id.erinaCharacter);
        if (!erinaHeroUnlocked){
            erinaChar.setColorFilter(ContextCompat.getColor(this, R.color.colorPrimaryDark), PorterDuff.Mode.SRC_IN);
        } else {
            erinaChar.setTag(R.id.characterRole, 0);
            erinaChar.setTag(R.id.characterImage, R.drawable.erina);
            erinaChar.setOnTouchListener(this);
        }

        ImageView healerChar = findViewById(R.id.healerCharacter);
        healerChar.setTag(R.id.characterRole, 1);
        healerChar.setTag(R.id.characterImage, R.drawable.healer);
        healerChar.setOnTouchListener(this);

        ImageView perlaChar = (ImageView) findViewById(R.id.perlaCharacter);
        if (!perlaHeroUnlocked){
            perlaChar.setColorFilter(ContextCompat.getColor(this, R.color.colorPrimaryDark), PorterDuff.Mode.SRC_IN);
        } else {
            perlaChar.setTag(R.id.characterRole, 1);
            perlaChar.setTag(R.id.characterImage, R.drawable.perla);
            perlaChar.setOnTouchListener(this);
        }

        ImageView tankChar = findViewById(R.id.tankCharacter);
        tankChar.setTag(R.id.characterRole, 2);
        tankChar.setTag(R.id.characterImage, R.drawable.tank);
        tankChar.setOnTouchListener(this);

        ImageView alfredChar = (ImageView) findViewById(R.id.alfredCharacter);
        if (!alfredHeroUnlocked){
            alfredChar.setColorFilter(ContextCompat.getColor(this, R.color.colorPrimaryDark), PorterDuff.Mode.SRC_IN);
        } else {
            alfredChar.setTag(R.id.characterRole, 2);
            alfredChar.setTag(R.id.characterImage, R.drawable.alfred);
            alfredChar.setOnTouchListener(this);
        }
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
                    selectedDPS = true;
                    ImageView frame = (ImageView) findViewById(R.id.dpsFrame);
                    frame.setBackgroundResource(newImage);
                    if (newImage == R.drawable.damageidle){
                        damageHeroSelected = true;
                        erinaHeroSelected = false;

                    }
                    if (newImage == R.drawable.erina){
                        damageHeroSelected = false;
                        erinaHeroSelected = true;
                    }
                    return true;
                }
                if (chosenRole == 1){
                    selectedSupport = true;
                    ImageView frame = (ImageView) findViewById(R.id.healerFrame);
                    frame.setBackgroundResource(newImage);
                    if (newImage == R.drawable.perla){
                        perlaHeroSelected = true;
                        healerHeroSelected = false;
                    }
                    if (newImage == R.drawable.healer){
                        perlaHeroSelected = false;
                        healerHeroSelected = true;
                    }
                    return true;
                }
                if (chosenRole == 2){
                    selectedTank = true;
                    ImageView frame = (ImageView) findViewById(R.id.tankFrame);
                    frame.setBackgroundResource(newImage);
                    if (newImage == R.drawable.tank){
                        alfredHeroSelected = false;
                        tankHeroSelected = true;
                    }
                    if (newImage == R.drawable.alfred){
                        alfredHeroSelected = true;
                        tankHeroSelected = false;
                    }
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
        if (!selectedDPS || !selectedSupport || !selectedTank){
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