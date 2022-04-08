package com.prestige.prestigegame.gameobject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
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
public class DamageExplosion extends Circle {

    private Player player;
    private int screenWidth;
    private int screenHeight;
    private Paint KBPaint;
    private Bitmap arrowExplosion;
    private Timer knockBackTimer = new Timer();
    public boolean activated = false;
    private boolean timerSet = false;
    private int positionX = 0;
    private int positionY = 0;
    private boolean firstKB = true;
    private int timeCounter = 0;
    private int KBAlpha = 100;
    private final double UPS = GameLoop.MAX_UPS;
    private final double initialDelay = UPS*15;
    public boolean improvedKnockBack = false;

    public DamageExplosion(Context context, Player player, double positionX, double positionY, double radius) {
        super(context, ContextCompat.getColor(context, R.color.coin), positionX, positionY, radius);
        this.player = player;

        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inScaled = false;
        arrowExplosion = BitmapFactory.decodeResource(context.getResources(), R.drawable.explosion, bitmapOptions);

        screenWidth = context.getResources().getDisplayMetrics().widthPixels;
        screenHeight = context.getResources().getDisplayMetrics().heightPixels;

        KBPaint = new Paint();
        KBPaint.setAlpha(KBAlpha);
    }

    public void drawExplosion(Canvas canvas) {
        if (activated){
            canvas.drawBitmap(arrowExplosion,
                    (float) (positionX-player.positionX+screenWidth/2-arrowExplosion.getWidth()/2),
                    (float) (positionY-player.positionY+screenHeight/2-arrowExplosion.getHeight()/2),
                    KBPaint
            );
            KBPaint.setAlpha(KBAlpha);
            KBAlpha -= 10;
            if (KBAlpha <= 0){
                KBAlpha = 255;
                activated = false;
            }
        }
    }

    public void update() {
        super.positionX = positionX;
        super.positionY = positionY;
    }

    public void getExplosionPosition(int x, int y){
        activated = true;
        this.positionX = (int)(x);
        this.positionY = (int)(y);
        super.positionX = positionX;
        super.positionY = positionY;
    }
}

