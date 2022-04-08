package com.prestige.prestigegame.graphics;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.Log;

import com.prestige.prestigegame.GameDisplay;
import com.prestige.prestigegame.R;
import com.prestige.prestigegame.gameobject.Player;
import com.prestige.prestigegame.gameobject.Spell;

public class DamageAnimator {
    private Bitmap[] damageAnimation;
    private Bitmap[] damageFlippedAnimation;
    private Bitmap damageIdle;
    private Bitmap damageMove1;
    private Bitmap damageMove2;
    private Bitmap bow;
    private Bitmap rotatedBow;
    private Bitmap damageFlipped;
    private Bitmap damageFlipped2;
    private Bitmap damageFlipped3;
    public String levelUpMsg;
    public float bowAngle = 0;
    private final int idleFrame = 0;
    private int movingFrame = 1;
    private int updatesBeforeNextMoveFrame = MAX_UPDATES_BEFORE_NEXT_MOVE_FRAME;
    private static final int MAX_UPDATES_BEFORE_NEXT_MOVE_FRAME = 5;
    public double arrowReloadTime = 1;
    public double arrowDamage = 1;
    public double explosiveArrowReloadTime = 10;
    public boolean explosiveArrowActivated = false;
    public boolean godArrowActivated = false;

    public DamageAnimator(Context context) {
        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inScaled = false;

        Matrix matrix = new Matrix();

        damageMove1 = BitmapFactory.decodeResource(context.getResources(), R.drawable.damagemove1, bitmapOptions);
        damageIdle = BitmapFactory.decodeResource(context.getResources(), R.drawable.damage, bitmapOptions);
        damageMove2 = BitmapFactory.decodeResource(context.getResources(), R.drawable.damagemove2, bitmapOptions);
        bow = BitmapFactory.decodeResource(context.getResources(), R.drawable.bow, bitmapOptions);

        matrix.postScale(-1, 1, damageMove1.getWidth()/2, damageMove1.getHeight()/2);
        damageFlipped = Bitmap.createBitmap(damageIdle, 0, 0, damageMove1.getWidth(), damageMove1.getHeight(), matrix, true);
        damageFlipped2 = Bitmap.createBitmap(damageMove1, 0, 0, damageMove1.getWidth(), damageMove1.getHeight(), matrix, true);
        damageFlipped3 = Bitmap.createBitmap(damageMove2, 0, 0, damageMove1.getWidth(), damageMove1.getHeight(), matrix, true);

        damageAnimation = new Bitmap[]{damageIdle, damageMove1, damageMove2};
        damageFlippedAnimation = new Bitmap[]{damageFlipped, damageFlipped2, damageFlipped3};
    }


    public void draw(Canvas canvas, int positionX, int positionY, Player player, Paint paint) {
        switch (player.getPlayerState().getState()) {
            case NOT_MOVING:
                if (bowAngle > 0){
                    canvas.drawBitmap(damageAnimation[idleFrame], positionX, positionY, paint);
                } else {
                    canvas.drawBitmap(damageFlippedAnimation[idleFrame], positionX, positionY, paint);
                }
                break;
            case STARTED_MOVING:
                canvas.drawBitmap(damageFlippedAnimation[idleFrame], positionX, positionY, paint);
                break;
            case IS_MOVING:
                if (bowAngle > 0){
                    canvas.drawBitmap(damageAnimation[movingFrame], positionX, positionY, paint);
                } else {
                    canvas.drawBitmap(damageFlippedAnimation[movingFrame], positionX, positionY, paint);
                }
                break;
            default:
                break;
        }
        Matrix bowMatrix = new Matrix();
        bowMatrix.postRotate(bowAngle);
        rotatedBow = Bitmap.createBitmap(bow, 0, 0, bow.getWidth(), bow.getHeight(), bowMatrix, true);
        canvas.drawBitmap(rotatedBow, positionX, positionY, paint);
        toggleIdxMovingFrame();
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

    public void getLevelUpText(int damageLevel) {
        if (damageLevel < 1){
            levelUpMsg = "-0.1s Reload Time";
        }
        if (damageLevel == 1) {
            levelUpMsg = "+0.5 Damage";
        }
        if (damageLevel == 2) {
            levelUpMsg = "-0.1s Reload Time";
        }
        if (damageLevel == 3) {
            levelUpMsg = "+0.5 Damage";
        }
        if (damageLevel == 4) {
            levelUpMsg = "+Ability: Explosive Arrows \n(10s Cool-down)";
        }
        if (damageLevel == 5) {
            levelUpMsg = "-0.1s Reload Time, \n-1s Explosive Arrow Cool-down";
        }
        if (damageLevel == 6) {
            levelUpMsg = "+0.5 Damage, \n-1s Explosive Arrow Cool-down";
        }
        if (damageLevel == 7) {
            levelUpMsg = "-0.1s Reload Time, \n-1s Explosive Arrow Cool-down";
        }
        if (damageLevel == 8) {
            levelUpMsg = "+0.5 Damage, \n-1s Explosive Arrow Cool-down";
        }
        if (damageLevel == 9) {
            levelUpMsg = "+Ability: God-Bullet (7s Cool-down)";
        }
        if (damageLevel >= 10) {
            levelUpMsg = "+0.2 Damage";
        }
    }

    public void checkLevelPerks(int damageLevel) {
        if (damageLevel < 1){
            arrowReloadTime -= 0.1;
        }
        if (damageLevel == 1) {
            arrowDamage += 0.5;
        }
        if (damageLevel == 2) {
            arrowReloadTime -= 0.1;
        }
        if (damageLevel == 3) {
            arrowDamage += 0.5;
        }
        if (damageLevel == 4) {
            explosiveArrowActivated = true;
        }
        if (damageLevel == 5) {
            arrowReloadTime -= 0.1;
            explosiveArrowReloadTime -= 1;
        }
        if (damageLevel == 6) {
            arrowDamage += 0.5;
            explosiveArrowReloadTime -= 1;
        }
        if (damageLevel == 7) {
            arrowReloadTime -= 0.1;
            explosiveArrowReloadTime -= 1;
        }
        if (damageLevel == 8) {
            arrowDamage += 0.5;
            explosiveArrowReloadTime -= 1;
        }
        if (damageLevel == 9) {
            godArrowActivated = true;
        }
        if (damageLevel >= 10) {
            arrowDamage += 0.2;
        }
    }
}
