package com.prestige.prestigegame.heroes.tank;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;

import com.prestige.prestigegame.Game;
import com.prestige.prestigegame.GameDisplay;
import com.prestige.prestigegame.GameLoop;
import com.prestige.prestigegame.R;
import com.prestige.prestigegame.gameobject.Circle;
import com.prestige.prestigegame.gameobject.Enemy;
import com.prestige.prestigegame.gameobject.Player;
import com.prestige.prestigegame.graphics.EnemyAnimator;

import java.util.Iterator;

public class Tank {
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
    private Shield shield;
    private KnockBack knockBack;

    //Character Values
    private String level;
    public String levelUpMsg;

    //Sound

    //Constants
    private int screenWidth;
    private int screenHeight;
    private int updatesBeforeNextMoveFrame = MAX_UPDATES_BEFORE_NEXT_MOVE_FRAME;
    private static final int MAX_UPDATES_BEFORE_NEXT_MOVE_FRAME = 5;
    private final double UPS = GameLoop.MAX_UPS;
    private float imgPositionX;
    private float imgPositionY;
    private Context context;
    private Player player;

    public Tank(Context context, int screenWidth, int screenHeight, Player player, Game game) {
        shield = new Shield(context, player, 0, 0, 125, game);
        knockBack = new KnockBack(context, player, 0, 0, 400, game);

        //Variables
        this.context = context;
        this.player = player;
        imgPositionX = screenWidth/2;
        imgPositionY = screenHeight/2;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;

        //Sprites
        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inScaled = false;

        Matrix matrix = new Matrix();

        tankMove1 = BitmapFactory.decodeResource(context.getResources(), R.drawable.tankmove1, bitmapOptions);
        tankIdle = BitmapFactory.decodeResource(context.getResources(), R.drawable.tankidle, bitmapOptions);
        tankMove2 = BitmapFactory.decodeResource(context.getResources(), R.drawable.tankmove2, bitmapOptions);

        matrix.postScale(-1, 1, tankMove1.getWidth()/2, tankMove1.getHeight()/2);
        tankFlipped = Bitmap.createBitmap(tankIdle, 0, 0, tankMove1.getWidth(), tankMove1.getHeight(), matrix, true);
        tankFlipped2 = Bitmap.createBitmap(tankMove1, 0, 0, tankMove1.getWidth(), tankMove1.getHeight(), matrix, true);
        tankFlipped3 = Bitmap.createBitmap(tankMove2, 0, 0, tankMove1.getWidth(), tankMove1.getHeight(), matrix, true);

        tankAnimation = new Bitmap[]{tankIdle, tankMove1, tankMove2};
        tankFlippedAnimation = new Bitmap[]{tankFlipped, tankFlipped2, tankFlipped3};
    }


    public void draw(Canvas canvas, Player player, Paint paint, Paint levelPaint, Paint strokePaint, GameDisplay gameDisplay) {
        level = String.valueOf(player.tankLevel);
        knockBack.drawTankKnockBack(canvas);
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
        shield.drawTankShield(canvas);

        canvas.drawText(level, imgPositionX+80, imgPositionY+30, strokePaint);
        canvas.drawText(level, imgPositionX+80, imgPositionY+30, levelPaint);
        toggleIdxMovingFrame();
    }

    public void update(Player player, EnemyAnimator enemyAnimator){
        shield.update();
        knockBack.update();

        //Collision
        Iterator<Enemy> enemyIterator = enemyAnimator.enemyList.iterator();
        while (enemyIterator.hasNext()) {
            Enemy enemy = enemyIterator.next();
            if (shield.activated){
                if (Circle.isColliding(enemy, shield)) {
                    // Remove enemy if it collides with the player
                    shield.checkShield();
                    enemyIterator.remove();
                    continue;
                }
            }
            if (knockBack.activated && Circle.isColliding(enemy, knockBack)) {
                enemy.knockedBack = true;
                if (knockBack.improvedKnockBack){
                    enemy.hitPoints--;
                }
                continue;
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
        if (tankLevel < 2) {
            levelUpMsg = "+5HP";
        }
        if (tankLevel == 2) {
            levelUpMsg = "+5HP, +Ability: Shield \n(2 Charges, 15s Cool-down)";
        }
        if (tankLevel == 3) {
            levelUpMsg = "+5HP, -1s Shield Cool-down";
        }
        if (tankLevel == 4) {
            levelUpMsg = "+5HP, -1s Shield Cool-down, \n+Ability: Knock-back (10s Cool-down)";
        }
        if (tankLevel == 5) {
            levelUpMsg = "+5HP, -1s Shield & \nKnock-back Cool-down, +1 Shield Charge";
        }
        if (tankLevel == 6 || tankLevel == 7) {
            levelUpMsg = "+5HP, -1s Shield & \nKnock-back Cool-down";
        }
        if (tankLevel == 8) {
            levelUpMsg = "+5HP, -1s Shield & \nKnock-back Cool-down, +1 Shield Charge";
        }
        if (tankLevel == 9) {
            levelUpMsg = "+5HP, -1s Shield & Knock-back \nCool-down, Knock-back does damage";
        }
        if (tankLevel >= 10) {
            levelUpMsg = "+5HP";
        }
    }

    public void checkLevelPerks(int level) {
        if (level >= 10){
            player.MAX_HEALTH_POINTS += 3;
            player.setHealthPoint(player.getHealthPoint()+3);
        } else {
            player.MAX_HEALTH_POINTS += 5;
            player.setHealthPoint(player.getHealthPoint()+5);
        }
    }
}
