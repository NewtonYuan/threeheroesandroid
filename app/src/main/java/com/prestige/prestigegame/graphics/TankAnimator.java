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

public class TankAnimator {
    private Bitmap[] tankAnimation;
    private Bitmap[] tankFlippedAnimation;
    private Bitmap tankIdle;
    private Bitmap tankMove1;
    private Bitmap tankMove2;
    private Bitmap tankFlipped;
    private Bitmap tankFlipped2;
    private Bitmap tankFlipped3;
    public String levelUpMsg;
    private final int idleFrame = 0;
    private int movingFrame = 1;
    private int updatesBeforeNextMoveFrame = MAX_UPDATES_BEFORE_NEXT_MOVE_FRAME;
    private static final int MAX_UPDATES_BEFORE_NEXT_MOVE_FRAME = 5;

    public TankAnimator(Context context) {
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


    public void draw(Canvas canvas, int positionX, int positionY, Player player, Paint paint) {
        switch (player.getPlayerState().getState()) {
            case NOT_MOVING:
                canvas.drawBitmap(tankAnimation[idleFrame], positionX, positionY, paint);
                break;
            case STARTED_MOVING:
                canvas.drawBitmap(tankAnimation[idleFrame], positionX, positionY, paint);
                break;
            case IS_MOVING:

                if (player.playerVelocityX < 0){
                    canvas.drawBitmap(tankAnimation[movingFrame], positionX, positionY, paint);
                } else {
                    canvas.drawBitmap(tankFlippedAnimation[movingFrame], positionX, positionY, paint);
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
}
