package com.prestige.prestigegame.heroes.perla;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.MediaPlayer;

import com.prestige.prestigegame.Game;
import com.prestige.prestigegame.GameDisplay;
import com.prestige.prestigegame.GameLoop;
import com.prestige.prestigegame.R;
import com.prestige.prestigegame.gameobject.Circle;
import com.prestige.prestigegame.gameobject.Enemy;
import com.prestige.prestigegame.gameobject.Player;
import com.prestige.prestigegame.graphics.EnemyAnimator;
import com.prestige.prestigegame.heroes.damage.Damage;

import java.util.Iterator;

public class Perla {
    //Animation Arrays
    private Bitmap[] healerAnimation;
    private Bitmap[] healerFlippedAnimation;
    private final int idleFrame = 0;
    private int movingFrame = 1;

    //Animation Sprites
    private Bitmap healerIdle;
    private Bitmap healerMove1;
    private Bitmap healerMove2;
    private Bitmap healerFlipped;
    private Bitmap healerFlipped2;
    private Bitmap healerFlipped3;

    //Character Abilities
    private Heal heal;
    private SlowMo slowMo;
    private int enemySpeedTimer = 0;
    private boolean enemySpeedReduced = false;

    //Character Values
    private String level;
    public String levelUpMsg;
    public float dpsReloadRatio = 1;
    public float dpsDamageRatio = 1;
    private int slowMoTimeCounter = 0;
    private int healTimeCounter = 0;
    private int slowMoCoolDown = 12;
    public int healCoolDown = 11;
    public int healAmount = 2;
    public boolean firstSlowMo = true;
    public boolean healActivated = false;
    public boolean slowMoActivated = false;
    public boolean improvedSlowMo = false;

    //Sound
    MediaPlayer healSoundPlayer;
    MediaPlayer slowMoSoundPlayer;

    //Constants
    private int screenWidth;
    private int screenHeight;
    private int updatesBeforeNextMoveFrame = MAX_UPDATES_BEFORE_NEXT_MOVE_FRAME;
    private static final int MAX_UPDATES_BEFORE_NEXT_MOVE_FRAME = 5;
    private final double UPS = GameLoop.MAX_UPS;
    private float imgPositionX;
    private float imgPositionY;
    private Context context;

    public Perla(Context context, int screenWidth, int screenHeight, Player player) {
        //Variables
        this.context = context;
        imgPositionX = screenWidth/2-100;
        imgPositionY = screenHeight/2;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;

        //Sound
        healSoundPlayer = MediaPlayer.create(context, R.raw.heal);
        slowMoSoundPlayer = MediaPlayer.create(context, R.raw.slowmo);

        //Sprites
        slowMo = new SlowMo(context, player);
        heal = new Heal(context);

        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inScaled = false;

        Matrix matrix = new Matrix();

        healerMove1 = BitmapFactory.decodeResource(context.getResources(), R.drawable.perlamove1, bitmapOptions);
        healerIdle = BitmapFactory.decodeResource(context.getResources(), R.drawable.perlaidle, bitmapOptions);
        healerMove2 = BitmapFactory.decodeResource(context.getResources(), R.drawable.perlamove2, bitmapOptions);

        matrix.postScale(-1, 1, healerMove1.getWidth()/2, healerMove1.getHeight()/2);
        healerFlipped = Bitmap.createBitmap(healerIdle, 0, 0, healerMove1.getWidth(), healerMove1.getHeight(), matrix, true);
        healerFlipped2 = Bitmap.createBitmap(healerMove1, 0, 0, healerMove1.getWidth(), healerMove1.getHeight(), matrix, true);
        healerFlipped3 = Bitmap.createBitmap(healerMove2, 0, 0, healerMove1.getWidth(), healerMove1.getHeight(), matrix, true);

        healerAnimation = new Bitmap[]{healerIdle, healerMove1, healerMove2};
        healerFlippedAnimation = new Bitmap[]{healerFlipped, healerFlipped2, healerFlipped3};
    }

    public void draw(Canvas canvas, Player player, Paint paint, Paint levelPaint, Paint strokePaint, GameDisplay gameDisplay) {
        level = String.valueOf(player.healerLevel);
        switch (player.getPlayerState().getState()) {
            case NOT_MOVING:
            case STARTED_MOVING:
                canvas.drawBitmap(healerAnimation[idleFrame], imgPositionX, imgPositionY, paint);
                break;
            case IS_MOVING:

                if (player.playerVelocityX < 0){
                    canvas.drawBitmap(healerAnimation[movingFrame], imgPositionX, imgPositionY, paint);
                } else {
                    canvas.drawBitmap(healerFlippedAnimation[movingFrame], imgPositionX, imgPositionY, paint);
                }
                break;
            default:
                break;
        }
        slowMo.drawClock(canvas);
        heal.drawHeal(canvas);

        canvas.drawText(level, imgPositionX+80, imgPositionY+30, strokePaint);
        canvas.drawText(level, imgPositionX+80, imgPositionY+30, levelPaint);
        toggleIdxMovingFrame();
    }

    public void update(Player player, EnemyAnimator enemyAnimator, Game game){
        //Timings
        if (slowMoActivated){
            slowMoTimeCounter++;
            if (slowMoTimeCounter >= this.slowMoCoolDown*UPS || firstSlowMo){
                if (firstSlowMo){
                    firstSlowMo = false;
                }
                slowMo.activated = true;
                if (!game.sfxMuted) { slowMoSoundPlayer.start(); }
                slowMoTimeCounter = 0;
                for (Enemy enemy: enemyAnimator.enemyList){
                    enemy.MAX_SPEED *= 0.5;
                    if (improvedSlowMo){
                        enemy.hitPoints--;
                    }
                }
                enemySpeedReduced = true;
            }
            if (enemySpeedReduced){
                enemySpeedTimer++;
                if (enemySpeedTimer >= UPS*4){
                    for (Enemy enemy: enemyAnimator.enemyList){
                        enemy.MAX_SPEED = enemy.ORIGINAL_SPEED;
                    }
                    enemySpeedTimer = 0;
                }
            }
        }

        if (healActivated){
            healTimeCounter++;
            if (healTimeCounter >= this.healCoolDown*UPS && player.getHealthPoint() < player.MAX_HEALTH_POINTS) {
                player.setHealthPoint(player.getHealthPoint()+this.healAmount);
                if (player.getHealthPoint() > player.MAX_HEALTH_POINTS){
                    player.setHealthPoint(player.MAX_HEALTH_POINTS);
                } else {
                    if (!game.sfxMuted) { healSoundPlayer.start(); }
                    heal.activated = true;
                }
                healTimeCounter = 0;
            }
        }
    }

    private void toggleIdxMovingFrame() {
        updatesBeforeNextMoveFrame--;
        if (updatesBeforeNextMoveFrame == 0){
            if(movingFrame == 1)
                movingFrame = 2;
            else
                movingFrame = 1;
            updatesBeforeNextMoveFrame = MAX_UPDATES_BEFORE_NEXT_MOVE_FRAME;
        }
    }
    public void getLevelUpText(int healerLevel) {
        if (healerLevel < 1) {
            levelUpMsg = "-5% DPS Reload Time";
        }
        if (healerLevel == 1) {
            levelUpMsg = "+10% DPS Damage";
        }
        if (healerLevel == 2) {
            levelUpMsg = "+Ability: Healing \n(10s Cool-down)";
        }
        if (healerLevel == 3) {
            levelUpMsg = "-5% DPS Reload Time, \n+1 Healing Power";
        }
        if (healerLevel == 4) {
            levelUpMsg = "+Ability: Slow-Mo \n(12s Cool-down)";
        }
        if (healerLevel == 5) {
            levelUpMsg = "+10% DPS Damage, \n-2s Slow-Mo Cool-down";
        }
        if (healerLevel == 6) {
            levelUpMsg = "-5% DPS Reload Time, \n+2 Healing Power";
        }
        if (healerLevel == 7) {
            levelUpMsg = "+10% DPS Damage, \n-2s Slow-Mo Cool-down";
        }
        if (healerLevel == 8) {
            levelUpMsg = "-5% DPS Reload Time, \5+2 Healing Power";
        }
        if (healerLevel == 9) {
            levelUpMsg = "-1s Healing Cool-down, \nSlow-Mo does damage";
        }
        if (healerLevel >= 10) {
            levelUpMsg = "+2% DPS Damage, \n+0.5 Heal Amount";
        }
    }

    public void checkLevelPerks(int healerLevel, Game game) {
        if (healerLevel < 1){
            dpsReloadRatio -= 0.05;
        }
        if (healerLevel == 1) {
            dpsDamageRatio += 0.1;
        }
        if (healerLevel == 2) {
            healActivated = true;
        }
        if (healerLevel == 3) {
            dpsReloadRatio -= 0.05;
        }
        if (healerLevel == 4) {
            slowMoActivated = true;
        }
        if (healerLevel == 5) {
            dpsDamageRatio += 0.1;
            slowMoCoolDown -= 2;
        }
        if (healerLevel == 6) {
            dpsReloadRatio -= 0.05;
            healAmount += 2;
        }
        if (healerLevel == 7) {
            dpsDamageRatio += 0.1;
            slowMoCoolDown -= 2;
        }
        if (healerLevel == 8) {
            dpsReloadRatio -= 0.05;
            healAmount += 2;
        }
        if (healerLevel == 9) {
            improvedSlowMo = true;
        }
        if (healerLevel >= 10) {
            dpsDamageRatio += 0.02;
            healAmount += 0.5;
        }
        game.dpsDamage *= dpsDamageRatio;
        game.dpsReloadTime *= dpsReloadRatio;
    }
}
