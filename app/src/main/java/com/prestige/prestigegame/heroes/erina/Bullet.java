package com.prestige.prestigegame.heroes.erina;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.util.Log;

import androidx.core.content.ContextCompat;

import com.prestige.prestigegame.GameDisplay;
import com.prestige.prestigegame.GameLoop;
import com.prestige.prestigegame.R;
import com.prestige.prestigegame.gameobject.Circle;
import com.prestige.prestigegame.gameobject.Enemy;
import com.prestige.prestigegame.gameobject.GameObject;
import com.prestige.prestigegame.gameobject.Player;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Collections;

public class Bullet extends Circle {
    public static final double SPEED_PIXELS_PER_SECOND = 35.0;
    private static final double MAX_SPEED = SPEED_PIXELS_PER_SECOND;
    private double enemyValue;
    private Player player;
    private double closest;
    private int screenWidth;
    private int screenHeight;
    private double distanceToEnemy;
    private double distanceToEnemyX;
    private double distanceToEnemyY;
    private Bitmap bulletBmp;
    private Bitmap rotatedBulletBmp;
    private Enemy closestEnemy;
    public boolean foundEnemy = false;
    public double bulletAngle;
    private boolean firstAbilityUse = true;
    private int multiShotCounter = 0;
    private final double UPS = GameLoop.MAX_UPS;
    private final double initialDelay = UPS*15;
    public boolean activated = false;
    public boolean leftBullet;

    HashMap<Enemy, Double> enemyMap = new HashMap<>();

    public Bullet(Context context, Player player, boolean leftBullet) {
        super(
                context,
                ContextCompat.getColor(context, R.color.spell),
                player.getPositionX(),
                player.getPositionY()-50,
                10
        );
        this.leftBullet = leftBullet;
        this.player = player;

        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inScaled = false;
        bulletBmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.bullet, bitmapOptions);
        velocityX = player.getDirectionX()*MAX_SPEED;
        velocityY = player.getDirectionY()*MAX_SPEED;

        screenWidth = context.getResources().getDisplayMetrics().widthPixels;
        screenHeight = context.getResources().getDisplayMetrics().heightPixels;
    }

    public void drawBullet(Canvas canvas, GameDisplay gameDisplay){
        Matrix matrix = new Matrix();
        matrix.postRotate((float)bulletAngle);
        rotatedBulletBmp = Bitmap.createBitmap(bulletBmp, 0, 0, bulletBmp.getWidth(), bulletBmp.getHeight(), matrix, true);
        if (leftBullet){
            canvas.drawBitmap(rotatedBulletBmp,
                    (float) gameDisplay.gameToDisplayCoordinatesX(positionX)-30,
                    (float) gameDisplay.gameToDisplayCoordinatesY(positionY)-30,
                    null);
        } else {
            canvas.drawBitmap(rotatedBulletBmp,
                    (float) gameDisplay.gameToDisplayCoordinatesX(positionX)+30,
                    (float) gameDisplay.gameToDisplayCoordinatesY(positionY)-30,
                    null);
        }
    }

    @Override
    public void update() {

        double directionX = distanceToEnemyX/distanceToEnemy;
        double directionY = distanceToEnemyY/distanceToEnemy;

        if(distanceToEnemy > 0) { // Avoid division by zero
            velocityX = directionX*MAX_SPEED;
            velocityY = directionY*MAX_SPEED;
            if (directionX < 0){
                bulletAngle = Math.toDegrees(Math.atan(distanceToEnemyY/distanceToEnemyX))-90;
            } else {
                bulletAngle = Math.toDegrees(Math.atan(distanceToEnemyY/distanceToEnemyX))+90;
            }
        } else {
            velocityX = 0;
            velocityY = 0;
        }

        positionX += velocityX;
        positionY += velocityY;
    }

    public void getClosestEnemy(List<Enemy> enemyList, Player player){
        for(Enemy enemy : enemyList){
            distanceToEnemy = GameObject.getDistanceBetweenObjects(enemy, player);
            enemyMap.put(enemy, distanceToEnemy);
        }
        if (!enemyMap.isEmpty()){
            closest = Collections.min(enemyMap.values());
        }
        for(Entry<Enemy, Double> entry: enemyMap.entrySet()){
            if(entry.getValue() == closest){
                closestEnemy = entry.getKey();
                distanceToEnemyX = closestEnemy.getPositionX() - player.getPositionX();
                distanceToEnemyY = closestEnemy.getPositionY() - player.getPositionY()+80;
                foundEnemy = true;
            }
        }
    }

    public float getBulletAngle(){
        return (float) bulletAngle;
    }
}
