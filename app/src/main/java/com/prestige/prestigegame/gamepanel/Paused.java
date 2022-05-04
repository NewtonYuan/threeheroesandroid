package com.prestige.prestigegame.gamepanel;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Typeface;

import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.prestige.prestigegame.R;

public class Paused {
    private Context context;
    private int screenWidth;
    private int screenHeight;
    private Paint backBox;
    public Bitmap initialResumeButton;
    private Bitmap initialQuitButton;
    public Bitmap resumeButton;
    private Bitmap quitButton;
    private Bitmap initialMusicSound;
    private Bitmap initialMusicSoundMuted;
    private Bitmap initialSfxSound;
    private Bitmap initialSfxSoundMuted;
    private Bitmap musicSound;
    private Bitmap musicSoundMuted;
    private Bitmap sfxSound;
    private Bitmap sfxSoundMuted;
    private float height;
    private float scale;
    private float musicButtonX;
    private float musicButtonY;
    private float sfxButtonX;
    private float sfxButtonY;
    private float resumeButtonX;
    private float resumeButtonY;
    private float quitButtonX;
    private float quitButtonY;

    public boolean musicMuted;
    public boolean sfxMuted;

    public Paused(Context context) {
        this.context = context;
        screenWidth = context.getResources().getDisplayMetrics().widthPixels;
        screenHeight = context.getResources().getDisplayMetrics().heightPixels;

        SharedPreferences settings = context.getSharedPreferences("settings", Context.MODE_PRIVATE);
        musicMuted = settings.getBoolean("musicMuted", false);
        sfxMuted = settings.getBoolean("sfxMuted", false);

        backBox = new Paint();
        backBox.setColor(Color.BLACK);
        backBox.setStyle(Paint.Style.FILL_AND_STROKE);
        backBox.setAlpha(120);

        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inScaled = false;
        initialResumeButton = BitmapFactory.decodeResource(context.getResources(), R.drawable.resumebutton, bitmapOptions);
        initialQuitButton = BitmapFactory.decodeResource(context.getResources(), R.drawable.quit, bitmapOptions);
        initialMusicSound = BitmapFactory.decodeResource(context.getResources(), R.drawable.musicsound, bitmapOptions);
        initialMusicSoundMuted = BitmapFactory.decodeResource(context.getResources(), R.drawable.musicsoundmute, bitmapOptions);
        initialSfxSound = BitmapFactory.decodeResource(context.getResources(), R.drawable.sfxsound, bitmapOptions);
        initialSfxSoundMuted = BitmapFactory.decodeResource(context.getResources(), R.drawable.sfxsoundmute, bitmapOptions);

        height = initialResumeButton.getHeight();
        scale = ((float)(screenHeight/15.54)/height);

        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);

        resumeButton = Bitmap.createBitmap(initialResumeButton, 0, 0, initialResumeButton.getWidth(), (int) height, matrix, false);
        quitButton = Bitmap.createBitmap(initialQuitButton, 0, 0, initialQuitButton.getWidth(), initialQuitButton.getHeight(), matrix, false);

        musicSound = Bitmap.createBitmap(initialMusicSound, 0, 0, initialMusicSound.getWidth(), initialMusicSound.getHeight(), matrix, false);
        musicSoundMuted = Bitmap.createBitmap(initialMusicSoundMuted, 0, 0, initialMusicSoundMuted.getWidth(), initialMusicSoundMuted.getHeight(), matrix, false);
        sfxSound = Bitmap.createBitmap(initialSfxSound, 0, 0, initialSfxSound.getWidth(), initialSfxSound.getHeight(), matrix, false);
        sfxSoundMuted = Bitmap.createBitmap(initialSfxSoundMuted, 0, 0, initialSfxSoundMuted.getWidth(), initialSfxSoundMuted.getHeight(), matrix, false);
    }

    public void draw(Canvas canvas) {
        final Typeface font = ResourcesCompat.getFont(context, R.font.customfont);
        String pausedText = "Game Paused";
        String continueText = "Continue?";

        Paint gameOverPaint = new Paint();
        int color = ContextCompat.getColor(context, R.color.white);
        gameOverPaint.setColor(color);
        gameOverPaint.setTypeface(font);
        float textSize = scale*100;
        gameOverPaint.setTextSize(textSize);
        gameOverPaint.setTextAlign(Paint.Align.CENTER);

        resumeButtonX = screenWidth/2-resumeButton.getWidth()/2;
        resumeButtonY = screenHeight/3*2+resumeButton.getHeight()/3;

        quitButtonX = screenWidth/2-quitButton.getWidth()/2;
        quitButtonY = screenHeight/4*3+quitButton.getHeight()/3;

        musicButtonX = (float)(screenWidth/2-(1.2*musicSound.getWidth()));
        musicButtonY = screenHeight/3;
        sfxButtonX = (float)(screenWidth/2+(0.2*sfxSound.getWidth()));
        sfxButtonY = screenHeight/3;

        canvas.drawRect(screenWidth/7, screenHeight/6, screenWidth/7*6, screenHeight/6*5, backBox);
        canvas.drawText(pausedText, screenWidth/2, screenHeight/4, gameOverPaint);
        canvas.drawText(continueText, screenWidth/2, screenHeight/3*2, gameOverPaint);

        if (!musicMuted){
            canvas.drawBitmap(musicSound, musicButtonX, musicButtonY, null);
        }
        if (musicMuted) {
            canvas.drawBitmap(musicSoundMuted, musicButtonX, musicButtonY, null);
        }

        if (!sfxMuted) {
            canvas.drawBitmap(sfxSound, sfxButtonX, sfxButtonY, null);
        }
        if (sfxMuted) {
            canvas.drawBitmap(sfxSoundMuted, sfxButtonX, sfxButtonY, null);
        }

        canvas.drawBitmap(resumeButton, resumeButtonX, resumeButtonY, null);
        canvas.drawBitmap(quitButton, quitButtonX, quitButtonY, null);
    }

    public boolean resumeButtonClicked(int touchPositionX, int touchPositionY){
        return (touchPositionX >= resumeButtonX && touchPositionX <= resumeButtonX+resumeButton.getWidth() && touchPositionY >= resumeButtonY && touchPositionY <= resumeButtonY+resumeButton.getHeight());
    }

    public boolean quitButtonClicked(int touchPositionX, int touchPositionY){
        return (touchPositionX >= quitButtonX && touchPositionX <= quitButtonX+quitButton.getWidth() && touchPositionY >= quitButtonY && touchPositionY <= quitButtonY+quitButton.getHeight());
    }

    public boolean musicSoundClicked(int touchPositionX, int touchPositionY){
        return (touchPositionX >= musicButtonX && touchPositionX <= musicButtonX+musicSound.getWidth() && touchPositionY >= musicButtonY && touchPositionY <= musicButtonY+musicSound.getHeight());
    }
    public boolean sfxSoundClicked(int touchPositionX, int touchPositionY){
        return (touchPositionX >= sfxButtonX && touchPositionX <= sfxButtonX+sfxSound.getWidth() && touchPositionY >= sfxButtonY && touchPositionY <= sfxButtonY+sfxSound.getHeight());
    }
}
