package com.prestige.prestigegame.gamepanel;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.Log;

import androidx.core.content.res.ResourcesCompat;

import com.prestige.prestigegame.GameActivity;
import com.prestige.prestigegame.GameLoop;
import com.prestige.prestigegame.R;
import com.prestige.prestigegame.gameobject.Player;

public class TimeAlive {
    private int positionX;
    private int positionY;
    private int screenWidth;
    private int screenHeight;
    private final double UPS = GameLoop.MAX_UPS;
    private double timeCounter = 0;
    private double secondsToMinus = 0;
    public int finalSeconds = 0;
    public int minutes = 0;
    final Typeface font;

    public TimeAlive(Context context) {

        screenWidth = context.getResources().getDisplayMetrics().widthPixels;
        screenHeight = context.getResources().getDisplayMetrics().heightPixels;

        positionX = screenWidth/2;
        positionY = 200;

        font = ResourcesCompat.getFont(context, R.font.customfont);
    }

    public void drawTime(Canvas canvas){
        double time = Math.round(timeCounter);
        minutes = (int)(time/UPS/60);
        int seconds = (int)(time/UPS);
        finalSeconds = (int)(seconds - secondsToMinus);
        if (finalSeconds >= 60){
            secondsToMinus += 60;
        }
        if (minutes >= 60){
            minutes = 0;
        }
        String timeAliveText = String.format("%02d:%02d", minutes, finalSeconds);

        Paint timePaint = new Paint();
        timePaint.setTextSize(125);
        timePaint.setColor(Color.WHITE);
        timePaint.setTypeface(font);
        timePaint.setTextAlign(Paint.Align.CENTER);

        Paint strokePaint = new Paint();
        strokePaint.setTextSize(125);
        strokePaint.setColor(Color.BLACK);
        strokePaint.setStyle(Paint.Style.STROKE);
        strokePaint.setStrokeWidth(5);
        strokePaint.setTypeface(font);
        strokePaint.setTextAlign(Paint.Align.CENTER);

        canvas.drawText(timeAliveText, (int)positionX, (int)positionY, timePaint);
        canvas.drawText(timeAliveText, (int)positionX, (int)positionY, strokePaint);
    }

    public void update(){
        timeCounter++;
    }
}
