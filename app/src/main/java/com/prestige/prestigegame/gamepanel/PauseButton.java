package com.prestige.prestigegame.gamepanel;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.Log;

import com.prestige.prestigegame.GameActivity;
import com.prestige.prestigegame.R;
import com.prestige.prestigegame.gameobject.Player;

public class PauseButton {
    private Bitmap resumeButton;
    private Bitmap pauseButton;
    private int positionX;
    private int positionY;
    private int screenWidth;
    private int screenHeight;
    private Player player;

    public PauseButton(Context context, Player player) {
        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inScaled = false;
        resumeButton = BitmapFactory.decodeResource(context.getResources(), R.drawable.resume, bitmapOptions);
        pauseButton = BitmapFactory.decodeResource(context.getResources(), R.drawable.pause, bitmapOptions);
        screenWidth = context.getResources().getDisplayMetrics().widthPixels;
        screenHeight = context.getResources().getDisplayMetrics().heightPixels;
        positionX = screenWidth/5*4;
        positionY = 100;
        this.player = player;
    }

    public void drawPause(Canvas canvas){
        canvas.drawBitmap(pauseButton, (int)(positionX), (int)(positionY), null);
    }

    public void drawResume(Canvas canvas){
        canvas.drawBitmap(resumeButton, (int)(positionX), (int)(positionY), null);
    }

    public void draw(Canvas canvas, int touchPositionX, int touchPositionY){
        if (isClicked(touchPositionX, touchPositionY) || player.getHealthPoint() <= 0){
                drawResume(canvas);

        }
        else {
            drawPause(canvas);
        }
    }

    public boolean isClicked(int touchPositionX, int touchPositionY){
        return (touchPositionX >= positionX && touchPositionX <= positionX+pauseButton.getWidth() && touchPositionY >= positionY && touchPositionY <= positionY+pauseButton.getHeight());
    }
}
