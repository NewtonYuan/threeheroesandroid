package com.prestige.prestigegame.gameobject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.util.Log;

import androidx.core.content.ContextCompat;

import com.prestige.prestigegame.GameDisplay;
import com.prestige.prestigegame.GameLoop;
import com.prestige.prestigegame.R;
import com.prestige.prestigegame.Utils;
import com.prestige.prestigegame.graphics.CharacterList;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Enemy is a character which always moves in the direction of the player.
 * The Enemy class is an extension of a Circle, which is an extension of a GameObject
 */
public class HealerFreeze extends Circle {
    MediaPlayer freezeSoundPlayer;
    private Player player;
    private int screenWidth;
    private int screenHeight;
    private Paint freezePaint;
    private Bitmap healerFreeze;
    private Timer knockBackTimer = new Timer();
    public boolean activated = false;
    private float positionX = 0;
    private float positionY = 0;
    private int newPositionX = 0;
    private int newPositionY = 0;
    private boolean firstFreeze = true;
    private int timeCounter = 0;
    private int alpha = 200;
    private final double UPS = GameLoop.MAX_UPS;
    private final double initialDelay = UPS*12;
    private double newDelay;
    private double angle;
    public boolean improvedFreeze = false;

    public HealerFreeze(Context context, Player player, double positionX, double positionY, double radius) {
        super(context, ContextCompat.getColor(context, R.color.coin), positionX, positionY, radius);
        this.player = player;

        freezeSoundPlayer = MediaPlayer.create(context, R.raw.freeze);

        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inScaled = false;
        healerFreeze = BitmapFactory.decodeResource(context.getResources(), R.drawable.healerfreeze, bitmapOptions);

        screenWidth = context.getResources().getDisplayMetrics().widthPixels;
        screenHeight = context.getResources().getDisplayMetrics().heightPixels;

        freezePaint = new Paint();
        freezePaint.setAlpha(alpha);

        this.positionX = screenWidth/2-300;
        this.positionY = screenHeight/2-300;
    }

    public void drawHealerFreeze(Canvas canvas, GameDisplay gameDisplay) {
        if (activated){
            canvas.drawBitmap(healerFreeze,
                    (float)(positionX-player.positionX),
                    (float)(positionY-player.positionY),
                    freezePaint
            );
            freezePaint.setAlpha(alpha);
            alpha -= 2;
            if (alpha <= 0){
                alpha = 255;
                activated = false;
            }
        }
    }

    public void update() {
        timeCounter++;
        if (player.tankLevel <= 10){
            newDelay = initialDelay-player.healerLevel*UPS;
        } else {
            newDelay = initialDelay-10*UPS;
        }
        if ((player.healerLevel >= 3 && timeCounter >= newDelay && !activated) || (player.healerLevel >= 3 && firstFreeze)){
            getRandomPosition();
            if (firstFreeze){
                activated = true;
                firstFreeze = false;
                timeCounter = 0;
                freezeSoundPlayer.start();
            } else {
                setKnockBackCounter();
            }
        }
        if (player.healerLevel >= 10 && !improvedFreeze){
            improvedFreeze = true;
        }

        super.positionX = positionX-screenWidth/2+150;
        super.positionY = positionY-screenHeight/2+150;
    }

    public void setKnockBackCounter(){
        freezeSoundPlayer.start();
        activated = true;
        timeCounter = 0;
    }

    public void getRandomPosition(){
        angle = Math.random()*Math.PI*2;
        positionX = (int)(Math.cos(angle)*screenWidth/3+player.positionX-150+screenWidth/2);
        positionY = (int)(Math.sin(angle)*screenWidth/3+player.positionY-150+screenHeight/2);
    }
}

