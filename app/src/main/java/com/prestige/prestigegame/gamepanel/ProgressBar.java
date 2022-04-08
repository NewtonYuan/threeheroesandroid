package com.prestige.prestigegame.gamepanel;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Color;
import android.util.Log;

public class ProgressBar {
    private int screenWidth;
    private int screenHeight;
    private int positionX;
    private int positionY;
    private int expNeeded = 1;
    private int expGained = 0;
    public int level = 0;
    private double ratio = 0;
    public boolean levelUpped = false;

    Paint ExpPaint = new Paint();
    Paint GainedPaint = new Paint();

    public ProgressBar(Context context){
        screenWidth = context.getResources().getDisplayMetrics().widthPixels;
        screenHeight = context.getResources().getDisplayMetrics().heightPixels;

        ExpPaint.setColor(Color.WHITE);
        ExpPaint.setStrokeWidth(3);
        ExpPaint.setAlpha(10);

        GainedPaint.setColor(Color.parseColor("#a1d6ff"));
        GainedPaint.setStrokeWidth(3);
        ExpPaint.setAlpha(200);
    }

    public void draw(Canvas canvas){
        canvas.drawRect( (int)(screenWidth*ratio), 0, screenWidth, 30, ExpPaint);
        canvas.drawRect( 0, 0, (int)(screenWidth*ratio), 30, GainedPaint);
    }

    public int getProgressPoints(){
        return expGained;
    }

    public void setProgressPoints(int expGained) {
        // Only allow positive values
        if (expGained < expNeeded){
            this.expGained = expGained;
            this.ratio = (double)this.expGained/expNeeded;
        } else{
            this.expGained = 0;
            this.expNeeded++;
            this.level++;
            this.ratio = 0.0;
            levelUpped = true;
        }
    }
}
