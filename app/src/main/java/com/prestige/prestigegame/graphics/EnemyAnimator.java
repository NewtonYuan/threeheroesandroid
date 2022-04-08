package com.prestige.prestigegame.graphics;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.util.Log;

import com.prestige.prestigegame.GameDisplay;
import com.prestige.prestigegame.R;
import com.prestige.prestigegame.gameobject.Enemy;
import com.prestige.prestigegame.gameobject.Player;

import java.util.ArrayList;
import java.util.List;

public class EnemyAnimator {
    private Bitmap[] enemyAnimation;
    private Bitmap[] enemyFlippedAnimation;
    private Bitmap enemyIdle;
    private Bitmap enemyMove1;
    private Bitmap enemyMove2;
    private Bitmap enemyFlipped;
    private Bitmap enemyFlipped2;
    private Bitmap enemyFlipped3;
    private final int idleFrame = 0;
    private int movingFrame = 1;
    private float updatesBeforeNextMoveFrame = MAX_UPDATES_BEFORE_NEXT_MOVE_FRAME;
    private static final float MAX_UPDATES_BEFORE_NEXT_MOVE_FRAME = 5;
    public List<Enemy> enemyList = new ArrayList<Enemy>();
    public boolean flipped = false;

    public EnemyAnimator(Context context) {
        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inScaled = false;

        Matrix matrix = new Matrix();

        enemyMove1 = BitmapFactory.decodeResource(context.getResources(), R.drawable.enemymove1, bitmapOptions);
        enemyIdle = BitmapFactory.decodeResource(context.getResources(), R.drawable.enemyidle, bitmapOptions);
        enemyMove2 = BitmapFactory.decodeResource(context.getResources(), R.drawable.enemymove2, bitmapOptions);

        matrix.postScale(-1, 1, enemyMove1.getWidth()/2, enemyMove1.getHeight()/2);
        enemyFlipped = Bitmap.createBitmap(enemyIdle, 0, 0, enemyMove1.getWidth(), enemyMove1.getHeight(), matrix, true);
        enemyFlipped2 = Bitmap.createBitmap(enemyMove1, 0, 0, enemyMove1.getWidth(), enemyMove1.getHeight(), matrix, true);
        enemyFlipped3 = Bitmap.createBitmap(enemyMove2, 0, 0, enemyMove1.getWidth(), enemyMove1.getHeight(), matrix, true);

        enemyAnimation = new Bitmap[]{enemyIdle, enemyMove1, enemyMove2};
        enemyFlippedAnimation = new Bitmap[]{enemyFlipped, enemyFlipped2, enemyFlipped3};
    }


    public void draw(Canvas canvas, float positionX, float positionY, Paint paint, Enemy enemy) {
        if (enemy.enemyVelocityX < 0){
            canvas.drawBitmap(enemyFlippedAnimation[movingFrame], positionX-50, positionY-50, paint);
            flipped = true;
        } else {
            canvas.drawBitmap(enemyAnimation[movingFrame], positionX-50, positionY-50, paint);
            flipped = false;
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
            updatesBeforeNextMoveFrame = MAX_UPDATES_BEFORE_NEXT_MOVE_FRAME*enemyList.toArray().length;
        }
    }
}
