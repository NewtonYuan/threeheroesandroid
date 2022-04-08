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

public class HealerAnimator {
    private Bitmap[] healerAnimation;
    private Bitmap[] healerFlippedAnimation;
    private Bitmap healerIdle;
    private Bitmap healerMove1;
    private Bitmap healerMove2;
    private Bitmap healerFlipped;
    private Bitmap healerFlipped2;
    private Bitmap healerFlipped3;
    public String levelUpMsg;
    private final int idleFrame = 0;
    private int movingFrame = 1;
    private int updatesBeforeNextMoveFrame = MAX_UPDATES_BEFORE_NEXT_MOVE_FRAME;
    private static final int MAX_UPDATES_BEFORE_NEXT_MOVE_FRAME = 5;
    public int healCoolDown = 12;
    public int healAmount = 3;
    public int bigHealCoolDown = 60;
    public boolean bigHealActivated = false;

    public HealerAnimator(Context context) {
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


    public void draw(Canvas canvas, int positionX, int positionY, Player player, Paint paint) {
        switch (player.getPlayerState().getState()) {
            case NOT_MOVING:
                canvas.drawBitmap(healerAnimation[idleFrame], positionX, positionY, paint);
                break;
            case STARTED_MOVING:
                canvas.drawBitmap(healerAnimation[idleFrame], positionX, positionY, paint);
                break;
            case IS_MOVING:

                if (player.playerVelocityX < 0){
                    canvas.drawBitmap(healerAnimation[movingFrame], positionX, positionY, paint);
                } else {
                    canvas.drawBitmap(healerFlippedAnimation[movingFrame], positionX, positionY, paint);
                }
                break;
            default:
                break;
        }
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
    public void getLevelUpText(int healerLevel) {
        Log.i("TANKLEVEL", String.valueOf(healerLevel));
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
            levelUpMsg = "+Ability: Big Heal \n(60s Cool-down)";
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
