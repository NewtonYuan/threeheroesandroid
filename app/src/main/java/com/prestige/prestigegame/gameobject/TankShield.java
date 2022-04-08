package com.prestige.prestigegame.gameobject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.media.MediaPlayer;

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
public class TankShield extends Circle {
    MediaPlayer shieldSoundPlayer;
    private static final double SPAWNS_PER_MINUTE = 120;
    private static final double SPAWNS_PER_SECOND = SPAWNS_PER_MINUTE/80.0;
    private static final double UPDATES_PER_SPAWN = GameLoop.MAX_UPS/SPAWNS_PER_SECOND;
    private static double updatesUntilNextSpawn = UPDATES_PER_SPAWN;
    private Player player;
    private int screenWidth;
    private int screenHeight;
    private Paint shieldPaint;
    private Bitmap tankShield;
    private Bitmap tankShieldYellow;
    private Bitmap tankShieldOrange;
    private Bitmap tankShieldRed;
    private Timer shieldTimer = new Timer();
    private final int initialDelay = 18000;
    private int delay = 0;
    public boolean activated = false;
    public boolean firstPhase = true;
    public boolean secondPhase = false;
    public boolean thirdPhase = false;
    public boolean fourthPhase = false;
    private boolean timerSet = false;
    private boolean firstShield = true;

    public TankShield(Context context, Player player, double positionX, double positionY, double radius) {
        super(context, ContextCompat.getColor(context, R.color.coin), positionX, positionY, radius);
        this.player = player;

        shieldSoundPlayer = MediaPlayer.create(context, R.raw.shieldup);

        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inScaled = false;
        tankShield = BitmapFactory.decodeResource(context.getResources(), R.drawable.tankshield, bitmapOptions);
        tankShieldYellow = BitmapFactory.decodeResource(context.getResources(), R.drawable.tankshieldyellow, bitmapOptions);
        tankShieldOrange = BitmapFactory.decodeResource(context.getResources(), R.drawable.tankshieldorange, bitmapOptions);
        tankShieldRed = BitmapFactory.decodeResource(context.getResources(), R.drawable.tankshieldred, bitmapOptions);

        screenWidth = context.getResources().getDisplayMetrics().widthPixels;
        screenHeight = context.getResources().getDisplayMetrics().heightPixels;

        shieldPaint = new Paint();
        shieldPaint.setAlpha(150);
    }

    public void drawTankShield(Canvas canvas) {
        if (activated) {
            if (firstPhase){
                canvas.drawBitmap(tankShield,
                        (float) screenWidth/2-250,
                        (float) screenHeight/2-220,
                        shieldPaint
                );
            } if (secondPhase){
                canvas.drawBitmap(tankShieldYellow,
                        (float) screenWidth/2-250,
                        (float) screenHeight/2-220,
                        shieldPaint
                );
            } if (thirdPhase){
                canvas.drawBitmap(tankShieldOrange,
                        (float) screenWidth/2-250,
                        (float) screenHeight/2-220,
                        shieldPaint
                );
            } if (fourthPhase){
                canvas.drawBitmap(tankShieldRed,
                        (float) screenWidth/2-250,
                        (float) screenHeight/2-220,
                        shieldPaint
                );
            }
        }
    }

    public void update() {
        if ((player.tankLevel >= 3 && !firstPhase && !timerSet) || (player.tankLevel >= 3 && firstShield)){
            if (firstShield){
                activated = true;
                firstShield = false;
            } else {
                if (player.tankLevel <= 10){
                    delay = initialDelay - player.tankLevel*1000;
                } else {
                    delay = initialDelay - 10*1000;
                }
                shieldRegenTimer();
                timerSet = true;
            }
        }

        super.positionX = player.positionX;
        super.positionY = player.positionY;
    }

    public void shieldRegenTimer(){
        shieldTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                shieldSoundPlayer.start();
                if (player.tankLevel >= 9){
                    if (secondPhase){
                        secondPhase = false;
                        firstPhase = true;
                    }
                    if (thirdPhase){
                        thirdPhase = false;
                        secondPhase = true;
                    }
                    if (fourthPhase){
                        fourthPhase = false;
                        thirdPhase = true;
                    }
                    if (!activated){
                        activated = true;
                        fourthPhase = true;
                    }
                }
                if (player.tankLevel >= 6 && player.tankLevel < 9){
                    if (secondPhase){
                        secondPhase = false;
                        firstPhase = true;
                    }
                    if (fourthPhase){
                        fourthPhase = false;
                        secondPhase = true;
                    }
                    if (!activated){
                        activated = true;
                        fourthPhase = true;
                    }
                }
                if (player.tankLevel >= 3 && player.tankLevel < 6){
                    if (fourthPhase){
                        fourthPhase = false;
                        firstPhase = true;
                    }
                    if (!activated){
                        activated = true;
                        fourthPhase = true;
                    }
                }
                timerSet = false;
            }
        }, delay);
    }

    public void checkShield(){
        if(player.tankLevel >= 9){
            if (fourthPhase){
                fourthPhase = false;
                activated = false;
            }
            if (thirdPhase){
                thirdPhase = false;
                fourthPhase = true;
            }
            if (secondPhase){
                secondPhase = false;
                thirdPhase = true;
            }
            if (firstPhase){
                firstPhase = false;
                secondPhase = true;
            }
        }
        if(player.tankLevel >= 6 && player.tankLevel < 9){
            if (fourthPhase){
                fourthPhase = false;
                activated = false;
            }
            if (secondPhase){
                secondPhase = false;
                fourthPhase = true;
            }
            if (firstPhase){
                firstPhase = false;
                secondPhase = true;
            }
        }
        if(player.tankLevel >= 3 && player.tankLevel < 6){
            if (fourthPhase){
                fourthPhase = false;
                activated = false;
            }
            if (firstPhase){
                firstPhase = false;
                fourthPhase = true;
            }
        }
    }
}

