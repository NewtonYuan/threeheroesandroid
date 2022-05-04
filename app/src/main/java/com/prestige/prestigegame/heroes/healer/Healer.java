package com.prestige.prestigegame.heroes.healer;

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

import java.util.Iterator;

public class Healer {
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
    private Freeze freeze;

    //Character Values
    private int healTimeCounter = 0;
    private int bigHealTimeCounter = 0;
    private String level;
    public String levelUpMsg;
    public int healCoolDown = 15;
    public int healAmount = 3;
    public int bigHealCoolDown = 70;
    public boolean bigHealActivated = false;

    //Sound
    MediaPlayer healSoundPlayer;
    MediaPlayer bigHealSoundPlayer;

    //Constants
    private int screenWidth;
    private int screenHeight;
    private int updatesBeforeNextMoveFrame = MAX_UPDATES_BEFORE_NEXT_MOVE_FRAME;
    private static final int MAX_UPDATES_BEFORE_NEXT_MOVE_FRAME = 5;
    private final double UPS = GameLoop.MAX_UPS;
    private float imgPositionX;
    private float imgPositionY;
    private Context context;
    private Game game;

    public Healer(Context context, int screenWidth, int screenHeight, Player player, Game game) {
        //Variables
        this.game = game;
        this.context = context;
        imgPositionX = screenWidth/2-100;
        imgPositionY = screenHeight/2;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;

        //Sound
        healSoundPlayer = MediaPlayer.create(context, R.raw.heal);
        bigHealSoundPlayer = MediaPlayer.create(context, R.raw.bigheal);

        //Sprites
        freeze = new Freeze(context, player, 0, 0, 100, game);

        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inScaled = false;

        Matrix matrix = new Matrix();

        healerMove1 = BitmapFactory.decodeResource(context.getResources(), R.drawable.healermove1, bitmapOptions);
        healerIdle = BitmapFactory.decodeResource(context.getResources(), R.drawable.healeridle, bitmapOptions);
        healerMove2 = BitmapFactory.decodeResource(context.getResources(), R.drawable.healermove2, bitmapOptions);

        matrix.postScale(-1, 1, healerMove1.getWidth()/2, healerMove1.getHeight()/2);
        healerFlipped = Bitmap.createBitmap(healerIdle, 0, 0, healerMove1.getWidth(), healerMove1.getHeight(), matrix, true);
        healerFlipped2 = Bitmap.createBitmap(healerMove1, 0, 0, healerMove1.getWidth(), healerMove1.getHeight(), matrix, true);
        healerFlipped3 = Bitmap.createBitmap(healerMove2, 0, 0, healerMove1.getWidth(), healerMove1.getHeight(), matrix, true);

        healerAnimation = new Bitmap[]{healerIdle, healerMove1, healerMove2};
        healerFlippedAnimation = new Bitmap[]{healerFlipped, healerFlipped2, healerFlipped3};
    }

    public void draw(Canvas canvas, Player player, Paint paint, Paint levelPaint, Paint strokePaint, GameDisplay gameDisplay) {
        level = String.valueOf(player.healerLevel);
        freeze.drawHealerFreeze(canvas, gameDisplay);
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

        canvas.drawText(level, imgPositionX+80, imgPositionY+30, strokePaint);
        canvas.drawText(level, imgPositionX+80, imgPositionY+30, levelPaint);
        toggleIdxMovingFrame();
    }

    public void update(Player player, EnemyAnimator enemyAnimator){
        freeze.update();

        //Timings
        healTimeCounter++;
        if (healTimeCounter >= this.healCoolDown*UPS && player.getHealthPoint() < player.MAX_HEALTH_POINTS) {
            player.setHealthPoint(player.getHealthPoint()+this.healAmount);
            if (player.getHealthPoint() > player.MAX_HEALTH_POINTS){
                player.setHealthPoint(player.MAX_HEALTH_POINTS);
            } else {
                if (!game.sfxMuted) { healSoundPlayer.start(); }
            }
            healTimeCounter = 0;
        }
        bigHealTimeCounter++;
        if (bigHealTimeCounter >= this.bigHealCoolDown*UPS && player.getHealthPoint() < player.MAX_HEALTH_POINTS && this.bigHealActivated) {
            player.setHealthPoint(player.getHealthPoint()+player.MAX_HEALTH_POINTS/2);
            if (player.getHealthPoint() > player.MAX_HEALTH_POINTS){
                player.setHealthPoint(player.MAX_HEALTH_POINTS);
            } else {
                if (!game.sfxMuted) { bigHealSoundPlayer.start(); }
            }
            bigHealTimeCounter = 0;
        }

        //Collision
        Iterator<Enemy> enemyIterator = enemyAnimator.enemyList.iterator();
        while (enemyIterator.hasNext()) {
            Enemy enemy = enemyIterator.next();
            if (freeze.activated && Circle.isColliding(enemy, freeze) && !enemy.frozen){
                enemy.frozen = true;
                if (freeze.improvedFreeze){
                    enemy.hitPoints--;
                }
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
            levelUpMsg = "-1s Healing Cool-down";
        }
        if (healerLevel == 1) {
            levelUpMsg = "+2 Healing Power";
        }
        if (healerLevel == 2) {
            levelUpMsg = "+Ability: Freeze (9s Cool-down)";
        }
        if (healerLevel == 3) {
            levelUpMsg = "-1s Healing Cool-down, \n-1s Freeze Cool-down";
        }
        if (healerLevel == 4) {
            levelUpMsg = "+Ability: Big Heal \n(65s Cool-down)";
        }
        if (healerLevel == 5) {
            levelUpMsg = "+2 Healing Power, \n-2s Freeze Cool-down";
        }
        if (healerLevel == 6) {
            levelUpMsg = "-1s Healing Cool-down, \n-5s Big Heal Cool-down";
        }
        if (healerLevel == 7) {
            levelUpMsg = "+2 Healing Power, \n-2s Freeze Cool-down";
        }
        if (healerLevel == 8) {
            levelUpMsg = "-1s Healing Cool-down, \5-5s Big Heal Cool-down";
        }
        if (healerLevel == 9) {
            levelUpMsg = "-2s Freeze Cool-down, \nFreeze does damage";
        }
        if (healerLevel >= 10) {
            levelUpMsg = "+0.4 Healing Power";
        }
    }

    public void checkLevelPerks(int healerLevel) {
        if (healerLevel < 1){
            healCoolDown -= 1;
        }
        if (healerLevel == 1) {
            healAmount += 2;
        }
        if (healerLevel == 2) { }
        if (healerLevel == 3) {
            healCoolDown -= 1;
        }
        if (healerLevel == 4) {
            bigHealActivated = true;
        }
        if (healerLevel == 5) {
            healAmount += 2;
        }
        if (healerLevel == 6) {
            healCoolDown -= 1;
            bigHealCoolDown -= 5;
        }
        if (healerLevel == 7) {
            healAmount += 2;
        }
        if (healerLevel == 8) {
            healCoolDown -= 1;
            bigHealCoolDown -= 5;
        }
        if (healerLevel == 9) {
        }
        if (healerLevel >= 10) {
            healAmount += 0.4;
        }
    }
}
