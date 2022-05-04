package com.prestige.prestigegame.heroes.tank;

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

import com.prestige.prestigegame.Game;
import com.prestige.prestigegame.GameDisplay;
import com.prestige.prestigegame.GameLoop;
import com.prestige.prestigegame.R;
import com.prestige.prestigegame.Utils;
import com.prestige.prestigegame.gameobject.Circle;
import com.prestige.prestigegame.gameobject.Player;
import com.prestige.prestigegame.graphics.CharacterList;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Enemy is a character which always moves in the direction of the player.
 * The Enemy class is an extension of a Circle, which is an extension of a GameObject
 */
public class KnockBack extends Circle {
    MediaPlayer knockBackSoundPlayer;
    private Player player;
    private int screenWidth;
    private int screenHeight;
    private Paint KBPaint;
    private Bitmap tankKnockBack;
    private Timer knockBackTimer = new Timer();
    public boolean activated = false;
    public boolean activated2 = false;
    private boolean timerSet = false;
    private int positionX = 0;
    private int positionY = 0;
    private boolean firstKB = true;
    private int timeCounter = 0;
    private int KBAlpha = 255;
    private final double UPS = GameLoop.MAX_UPS;
    private final double initialDelay = UPS*15;
    private double newDelay;
    public boolean improvedKnockBack = false;
    private Game game;

    public KnockBack(Context context, Player player, double positionX, double positionY, double radius, Game game) {
        super(context, ContextCompat.getColor(context, R.color.coin), positionX, positionY, radius);
        this.player = player;
        this.game = game;

        knockBackSoundPlayer = MediaPlayer.create(context, R.raw.knockback);

        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inScaled = false;
        tankKnockBack = BitmapFactory.decodeResource(context.getResources(), R.drawable.tankknockback, bitmapOptions);

        screenWidth = context.getResources().getDisplayMetrics().widthPixels;
        screenHeight = context.getResources().getDisplayMetrics().heightPixels;

        KBPaint = new Paint();
        KBPaint.setAlpha(KBAlpha);

        this.positionX = screenWidth/2-300;
        this.positionY = screenHeight/2-300;
    }

    public void drawTankKnockBack(Canvas canvas) {
        if (activated){
            canvas.drawBitmap(tankKnockBack,
                    (float) (positionX-player.positionX),
                    (float) (positionY-player.positionY),
                    null
            );
            activated = false;
            activated2 = true;
        }
        if (activated2) {
            canvas.drawBitmap(tankKnockBack,
                    (float) (positionX-player.positionX),
                    (float) (positionY-player.positionY),
                    KBPaint
            );
            KBPaint.setAlpha(KBAlpha);
            KBAlpha -= 2;
            if (KBAlpha <= 0){
                KBAlpha = 255;
                activated2 = false;
            }
        }
    }

    public void update() {
        timeCounter++;
        if (player.tankLevel <= 10){
            newDelay = initialDelay-player.tankLevel*UPS;
        } else {
            newDelay = initialDelay-10*UPS;
        }
        if ((player.tankLevel >= 5 && timeCounter >= newDelay && !activated) || (player.tankLevel >= 5 && firstKB)){
            if (firstKB){
                if (!game.sfxMuted) { knockBackSoundPlayer.start(); }
                positionX = (int) player.positionX+screenWidth/2-300;
                positionY = (int) player.positionY+screenHeight/2-300;
                activated = true;
                firstKB = false;
            } else {
                setKnockBackCounter();
                timerSet = true;
            }
        }
        if (player.tankLevel >= 10 && !improvedKnockBack){
            improvedKnockBack = true;
        }

        super.positionX = player.positionX;
        super.positionY = player.positionY;
    }

    public void setKnockBackCounter(){
        if (!game.sfxMuted) { knockBackSoundPlayer.start(); }
        activated = true;
        positionX = (int) player.positionX+screenWidth/2-300;
        positionY = (int) player.positionY+screenHeight/2-300;
        timeCounter = 0;
    }
}

