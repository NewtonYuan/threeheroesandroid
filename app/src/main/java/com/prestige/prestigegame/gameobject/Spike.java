package com.prestige.prestigegame.gameobject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;

import androidx.core.content.ContextCompat;

import com.prestige.prestigegame.GameDisplay;
import com.prestige.prestigegame.GameLoop;
import com.prestige.prestigegame.R;
import com.prestige.prestigegame.graphics.CharacterList;

/**
 * Enemy is a character which always moves in the direction of the player.
 * The Enemy class is an extension of a Circle, which is an extension of a GameObject
 */
public class Spike extends Circle {

    private static final double SPAWNS_PER_MINUTE = 80;
    private static final double SPAWNS_PER_SECOND = SPAWNS_PER_MINUTE/80.0;
    private static final double UPDATES_PER_SPAWN = GameLoop.MAX_UPS/SPAWNS_PER_SECOND;
    private static double updatesUntilNextSpawn = UPDATES_PER_SPAWN;
    private Player player;
    private int screenWidth;
    private int screenHeight;
    private Bitmap spikeBmp;
    private CharacterList characterList;
    private Paint shadow;
    private double angle;
    private double newPositionX;
    private double newPositionY;

    public Spike(Context context, Player player, double positionX, double positionY, double radius) {
        super(context, ContextCompat.getColor(context, R.color.coin), positionX, positionY, radius);
        this.player = player;

        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inScaled = false;
        spikeBmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.spike, bitmapOptions);

        screenWidth = context.getResources().getDisplayMetrics().widthPixels;
        screenHeight = context.getResources().getDisplayMetrics().heightPixels;

        characterList = new CharacterList(context);

        shadow = new Paint();
        shadow.setColor(Color.BLACK);
        shadow.setStyle(Paint.Style.FILL_AND_STROKE);
        shadow.setAlpha(70);
    }

    /**
     * readyToSpawn checks if a new enemy should spawn, according to the decided number of spawns
     * per minute (see SPAWNS_PER_MINUTE at top)
     * @return
     */
    public static boolean readyToSpawn() {

        if (updatesUntilNextSpawn <= 0) {
            updatesUntilNextSpawn += UPDATES_PER_SPAWN;
            return true;
        } else {
            updatesUntilNextSpawn --;
            return false;
        }
    }

    public void drawSpike(Canvas canvas, GameDisplay gameDisplay) {
        canvas.drawBitmap(spikeBmp,
                (float) gameDisplay.gameToDisplayCoordinatesX(positionX),
                (float) gameDisplay.gameToDisplayCoordinatesY(positionY),
                null
        );
        canvas.drawOval((float) gameDisplay.gameToDisplayCoordinatesX(positionX)+5,
                (float) gameDisplay.gameToDisplayCoordinatesY(positionY)+50,
                (float) gameDisplay.gameToDisplayCoordinatesX(positionX)+45,
                (float) gameDisplay.gameToDisplayCoordinatesY(positionY)+60,
                shadow);
    }

    public void update() {
        angle = Math.random()*Math.PI*2;
        newPositionX = Math.cos(angle)*screenHeight+player.positionX;
        newPositionY = Math.sin(angle)*screenHeight+player.positionY;
    }

    public double getNewPositionX(){
        return newPositionX;
    }

    public double getNewPositionY(){
        return newPositionY;
    }
}

