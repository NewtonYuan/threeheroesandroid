package com.prestige.prestigegame.heroes.alfred;

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

public class Alfred {
    //Constants
    private int screenWidth;
    private int screenHeight;
    private int updatesBeforeNextMoveFrame = MAX_UPDATES_BEFORE_NEXT_MOVE_FRAME;
    private static final int MAX_UPDATES_BEFORE_NEXT_MOVE_FRAME = 5;
    private final double UPS = GameLoop.MAX_UPS;
    private float imgPositionX;
    private float imgPositionY;
    private Context context;

    //Animation Arrays
    private Bitmap[] tankAnimation;
    private Bitmap[] tankFlippedAnimation;
    private final int idleFrame = 0;
    private int movingFrame = 1;

    //Animation Sprites
    private Bitmap tankIdle;
    private Bitmap tankMove1;
    private Bitmap tankMove2;
    private Bitmap tankFlipped;
    private Bitmap tankFlipped2;
    private Bitmap tankFlipped3;

    //Character Abilities
    private Slash slash;
    private boolean slashActivated = false;
    private double slashCD = 14*UPS;
    private TempHP tempHP;
    private boolean tempHPActivated = false;
    private double tempHPCD = 40*UPS;
    private double tempHPDuration = 10*UPS;
    private boolean tempHPReset = true;

    //Character Values
    private String level;
    public String levelUpMsg;
    public float damageTakenRatio = 1;
    private int slashTimeCounter = 0;
    private int tempHPTimeCounter = 0;
    private int tempHPResetTimeCounter = 0;
    private boolean firstSlash = true;
    private boolean firstTempHP = true;

    //Sound
    MediaPlayer tempHPSoundPlayer;
    MediaPlayer slashSoundPlayer;

    public Alfred(Context context, int screenWidth, int screenHeight, Player player) {
        //Variables
        this.context = context;
        imgPositionX = screenWidth/2;
        imgPositionY = screenHeight/2;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;

        //Sound
        tempHPSoundPlayer = MediaPlayer.create(context, R.raw.temphp);
        slashSoundPlayer = MediaPlayer.create(context, R.raw.swordslash);

        //Sprites
        slash = new Slash(context, player);
        tempHP = new TempHP(context);

        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inScaled = false;

        Matrix matrix = new Matrix();

        tankMove1 = BitmapFactory.decodeResource(context.getResources(), R.drawable.alfredmove1, bitmapOptions);
        tankIdle = BitmapFactory.decodeResource(context.getResources(), R.drawable.alfredidle, bitmapOptions);
        tankMove2 = BitmapFactory.decodeResource(context.getResources(), R.drawable.alfredmove2, bitmapOptions);

        matrix.postScale(-1, 1, tankMove1.getWidth()/2, tankMove1.getHeight()/2);
        tankFlipped = Bitmap.createBitmap(tankIdle, 0, 0, tankIdle.getWidth(), tankIdle.getHeight(), matrix, true);
        tankFlipped2 = Bitmap.createBitmap(tankMove1, 0, 0, tankMove1.getWidth(), tankMove1.getHeight(), matrix, true);
        tankFlipped3 = Bitmap.createBitmap(tankMove2, 0, 0, tankMove2.getWidth(), tankMove2.getHeight(), matrix, true);

        tankAnimation = new Bitmap[]{tankIdle, tankMove1, tankMove2};
        tankFlippedAnimation = new Bitmap[]{tankFlipped, tankFlipped2, tankFlipped3};
    }

    public void draw(Canvas canvas, Player player, Paint paint, Paint levelPaint, Paint strokePaint, GameDisplay gameDisplay) {
        level = String.valueOf(player.tankLevel);
        switch (player.getPlayerState().getState()) {
            case NOT_MOVING:
            case STARTED_MOVING:
                canvas.drawBitmap(tankAnimation[idleFrame], imgPositionX, imgPositionY, paint);
                break;
            case IS_MOVING:

                if (player.playerVelocityX < 0){
                    canvas.drawBitmap(tankAnimation[movingFrame], imgPositionX, imgPositionY, paint);
                } else {
                    canvas.drawBitmap(tankFlippedAnimation[movingFrame], imgPositionX, imgPositionY, paint);
                }
                break;
            default:
                break;
        }
        slash.drawSlash(canvas, gameDisplay);
        tempHP.drawTempHP(canvas);
        slash.drawSlashIcon(canvas);

        canvas.drawText(level, imgPositionX+80, imgPositionY+30, strokePaint);
        canvas.drawText(level, imgPositionX+80, imgPositionY+30, levelPaint);
        toggleIdxMovingFrame();
    }

    public void update(Player player, EnemyAnimator enemyAnimator, Game game){
        //Timings
        if (slashActivated){
            slashTimeCounter++;
            if (slashTimeCounter >= this.slashCD || firstSlash){
                if (!game.sfxMuted) { slashSoundPlayer.start(); }
                slash.getClosestEnemy(enemyAnimator.enemyList, player);
                if (firstSlash){
                    firstSlash = false;
                }
                slash.activated = true;
                //SOUND COMES HERE
                slashTimeCounter = 0;
                slash.startSlash = true;
            }
            slash.update();
        }
        if (tempHPActivated){
            tempHPTimeCounter++;
            if (tempHPTimeCounter >= this.tempHPCD || firstTempHP){
                tempHP.activated = true;
                if (firstTempHP){
                    firstTempHP = false;
                }
                if (!game.sfxMuted) { tempHPSoundPlayer.start(); }
                player.MAX_HEALTH_POINTS *= 2;
                player.setHealthPoint(player.getHealthPoint()*2);
                tempHPReset = true;
                tempHPTimeCounter = 0;
            }
            if (tempHPReset) {
                tempHPResetTimeCounter++;
                if (tempHPResetTimeCounter >= this.tempHPDuration){
                    player.MAX_HEALTH_POINTS /= 2;
                    player.setHealthPoint(player.getHealthPoint()/2);
                    tempHPReset = false;
                    tempHPResetTimeCounter = 0;
                }
            }
        }

        //Collisions
        if (slash.activated){
            for (Enemy enemy: enemyAnimator.enemyList){
                if (Circle.isColliding(slash, enemy)){
                    enemy.hitPoints /= 2;
                    enemy.MAX_SPEED *= 0.7;
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
    public void getLevelUpText(int tankLevel) {
        if (tankLevel < 1) {
            levelUpMsg = "+5HP";
        }
        if (tankLevel == 1) {
            levelUpMsg = "-4% Damage Taken";
        }
        if (tankLevel == 2) {
            levelUpMsg = "+Ability: Sword Slash \n(14s Cool-down)";
        }
        if (tankLevel == 3) {
            levelUpMsg = "-4% Damage Taken \n-1s Slash Cool-down";
        }
        if (tankLevel == 4) {
            levelUpMsg = "+5HP \n-1s Slash Cool-down";
        }
        if (tankLevel == 5) {
            levelUpMsg = "-4% Damage Taken \n-1s Slash Cool-down";
        }
        if (tankLevel == 6) {
            levelUpMsg = "+5HP \n-1s Slash Cool-down";
        }
        if (tankLevel == 7) {
            levelUpMsg = "+Ability: Temporary HP \n(40s Cool-down, 10s Duration)";
        }
        if (tankLevel == 8) {
            levelUpMsg = "+5HP \n-1s Slash Cool-down";
        }
        if (tankLevel == 9) {
            levelUpMsg = "-4% Damage Taken \n-1s Slash Cool-down";
        }
        if (tankLevel >= 10) {
            levelUpMsg = "2 HP, \n-1% Damage Taken";
        }
    }

    public void checkLevelPerks(int tankLevel, Player player, Game game) {
        if (tankLevel < 1){
            player.MAX_HEALTH_POINTS += 5;
        }
        if (tankLevel == 1) {
            damageTakenRatio -= 0.04;
        }
        if (tankLevel == 2) {
            slashActivated = true;
        }
        if (tankLevel == 3) {
            damageTakenRatio -= 0.04;
            slashCD -= UPS;
        }
        if (tankLevel == 4) {
            player.MAX_HEALTH_POINTS += 5;
            slashCD -= UPS;
        }
        if (tankLevel == 5) {
            damageTakenRatio -= 0.04;
            slashCD -= UPS;
        }
        if (tankLevel == 6) {
            player.MAX_HEALTH_POINTS += 5;
            slashCD -= UPS;
        }
        if (tankLevel == 7) {
            tempHPActivated = true;
        }
        if (tankLevel == 8) {
            player.MAX_HEALTH_POINTS += 5;
            slashCD -= UPS;
        }
        if (tankLevel == 9) {
            damageTakenRatio -= 0.04;
            slashCD -= UPS;
        }
        if (tankLevel >= 10) {
            player.MAX_HEALTH_POINTS += 2;
            damageTakenRatio -= 0.01;
        }
        game.reductionRatio = damageTakenRatio;
    }
}
