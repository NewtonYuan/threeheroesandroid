package com.prestige.prestigegame.gamepanel;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
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
    public Bitmap resumeButton;
    private Bitmap quitButton;
    private float resumeButtonX;
    private float resumeButtonY;
    private float quitButtonX;
    private float quitButtonY;

    public Paused(Context context) {
        this.context = context;
        screenWidth = context.getResources().getDisplayMetrics().widthPixels;
        screenHeight = context.getResources().getDisplayMetrics().heightPixels;

        backBox = new Paint();
        backBox.setColor(Color.BLACK);
        backBox.setStyle(Paint.Style.FILL_AND_STROKE);
        backBox.setAlpha(120);

        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inScaled = false;
        resumeButton = BitmapFactory.decodeResource(context.getResources(), R.drawable.resumebutton, bitmapOptions);
        quitButton = BitmapFactory.decodeResource(context.getResources(), R.drawable.quit, bitmapOptions);
    }

    public void draw(Canvas canvas) {
        final Typeface font = ResourcesCompat.getFont(context, R.font.customfont);
        String pausedText = "Game Paused";
        String continueText = "Continue?";

        Paint gameOverPaint = new Paint();
        int color = ContextCompat.getColor(context, R.color.white);
        gameOverPaint.setColor(color);
        gameOverPaint.setTypeface(font);
        float textSize = 100;
        gameOverPaint.setTextSize(textSize);
        gameOverPaint.setTextAlign(Paint.Align.CENTER);

        resumeButtonX = screenWidth/2-resumeButton.getWidth()/2;
        resumeButtonY = screenHeight/3*2+resumeButton.getHeight()/3;

        quitButtonX = screenWidth/2-quitButton.getWidth()/2;
        quitButtonY = screenHeight/4*3+quitButton.getHeight()/3;

        canvas.drawRect(screenWidth/7, screenHeight/6, screenWidth/7*6, screenHeight/6*5, backBox);
        canvas.drawText(pausedText, screenWidth/2, screenHeight/4, gameOverPaint);
        canvas.drawText(continueText, screenWidth/2, screenHeight/3*2, gameOverPaint);
        canvas.drawBitmap(resumeButton, resumeButtonX, resumeButtonY, null);
        canvas.drawBitmap(quitButton, quitButtonX, quitButtonY, null);
    }

    public boolean resumeButtonClicked(int touchPositionX, int touchPositionY){
        return (touchPositionX >= resumeButtonX && touchPositionX <= resumeButtonX+resumeButton.getWidth() && touchPositionY >= resumeButtonY && touchPositionY <= resumeButtonY+resumeButton.getHeight());
    }

    public boolean quitButtonClicked(int touchPositionX, int touchPositionY){
        return (touchPositionX >= quitButtonX && touchPositionX <= quitButtonX+quitButton.getWidth() && touchPositionY >= quitButtonY && touchPositionY <= quitButtonY+quitButton.getHeight());
    }
}
