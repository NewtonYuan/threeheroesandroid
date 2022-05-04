package com.prestige.prestigegame.heroes.alfred;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.util.Log;

import androidx.core.content.ContextCompat;

import com.prestige.prestigegame.GameDisplay;
import com.prestige.prestigegame.GameLoop;
import com.prestige.prestigegame.R;
import com.prestige.prestigegame.Utils;
import com.prestige.prestigegame.gameobject.Circle;
import com.prestige.prestigegame.gameobject.Enemy;
import com.prestige.prestigegame.gameobject.GameObject;
import com.prestige.prestigegame.gameobject.Player;
import com.prestige.prestigegame.graphics.CharacterList;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class Slash extends Circle{
    public static final double SPEED_PIXELS_PER_SECOND = 25.0;
    private static final double MAX_SPEED = SPEED_PIXELS_PER_SECOND;
    private int screenWidth;
    private int screenHeight;
    private Paint paint;
    private Bitmap slashIcon;
    private Bitmap slashBmp;
    private Bitmap rotatedSlashBmp;
    public boolean activated = false;
    private float positionX;
    private float positionY;
    private int alpha = 150;
    private double distanceToEnemy;
    private double distanceToEnemyX;
    private double distanceToEnemyY;
    private double closest;
    private Enemy closestEnemy;
    public double slashAngle;
    public boolean startSlash = false;
    private Player player;
    HashMap<Enemy, Double> enemyMap = new HashMap<>();

    public Slash(Context context, Player player) {
        super(context, R.color.colorPrimaryDark, player.getPositionX()-100, player.getPositionY()-100, 80);
        this.player = player;

        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inScaled = false;
        slashIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.slashicon, bitmapOptions);
        slashBmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.slash, bitmapOptions);

        screenWidth = context.getResources().getDisplayMetrics().widthPixels;
        screenHeight = context.getResources().getDisplayMetrics().heightPixels;

        velocityX = player.getDirectionX()*MAX_SPEED;
        velocityY = player.getDirectionY()*MAX_SPEED;

        paint = new Paint();
        paint.setAlpha(alpha);

        this.positionX = screenWidth/2;
        this.positionY = screenHeight/2;
    }

    public void update() {
        if (startSlash){
            super.positionX = player.getPositionX()-100;
            super.positionY = player.getPositionY()-100;
            startSlash = false;
        }
        double directionX = distanceToEnemyX / distanceToEnemy;
        double directionY = distanceToEnemyY / distanceToEnemy;

        if (distanceToEnemy > 0) { // Avoid division by zero
            velocityX = directionX * MAX_SPEED;
            velocityY = directionY * MAX_SPEED;
            if (directionX < 0) {
                slashAngle = Math.toDegrees(Math.atan(distanceToEnemyY / distanceToEnemyX)) - 90;
            } else {
                slashAngle = Math.toDegrees(Math.atan(distanceToEnemyY / distanceToEnemyX)) + 90;
            }
        } else {
            velocityX = 0;
            velocityY = 0;
        }

        super.positionX += velocityX;
        super.positionY += velocityY;
    }

    public void drawSlashIcon(Canvas canvas) {
        if (activated){
            canvas.drawBitmap(slashIcon,
                    positionX,
                    positionY,
                    paint
            );
            paint.setAlpha(alpha);
            alpha -= 3;
            if (alpha <= 0){
                alpha = 150;
                activated = false;
            }
        }
    }

    public void drawSlash(Canvas canvas, GameDisplay gameDisplay){
        if (activated) {
            Matrix matrix = new Matrix();
            matrix.postRotate((float) slashAngle);
            rotatedSlashBmp = Bitmap.createBitmap(slashBmp, 0, 0, slashBmp.getWidth(), slashBmp.getHeight(), matrix, true);
            canvas.drawBitmap(rotatedSlashBmp,
                    (float) gameDisplay.gameToDisplayCoordinatesX(super.positionX),
                    (float) gameDisplay.gameToDisplayCoordinatesY(super.positionY),
                    null);
        }
    }

    public void getClosestEnemy(List<Enemy> enemyList, Player player){
        for(Enemy enemy : enemyList){
            distanceToEnemy = GameObject.getDistanceBetweenObjects(enemy, player);
            enemyMap.put(enemy, distanceToEnemy);
        }
        if (!enemyMap.isEmpty()){
            closest = Collections.min(enemyMap.values());
        }
        for(Map.Entry<Enemy, Double> entry: enemyMap.entrySet()){
            if(entry.getValue() == closest){
                closestEnemy = entry.getKey();
                distanceToEnemyX = closestEnemy.getPositionX() - player.getPositionX();
                distanceToEnemyY = closestEnemy.getPositionY() - player.getPositionY()+60;
            }
        }
        enemyMap.clear();
    }
}

