package com.prestige.prestigegame.heroes.erina;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.MediaPlayer;
import android.util.Log;


import com.prestige.prestigegame.Game;
import com.prestige.prestigegame.GameDisplay;
import com.prestige.prestigegame.GameLoop;
import com.prestige.prestigegame.R;
import com.prestige.prestigegame.gameobject.Circle;
import com.prestige.prestigegame.gameobject.Enemy;
import com.prestige.prestigegame.gameobject.ExplosiveArrow;
import com.prestige.prestigegame.gameobject.GameObject;
import com.prestige.prestigegame.gameobject.Player;
import com.prestige.prestigegame.graphics.EnemyAnimator;
import com.prestige.prestigegame.heroes.damage.Arrow;
import com.prestige.prestigegame.heroes.damage.GodArrow;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Erina {
    //Animation Arrays
    private Bitmap[] damageAnimation;
    private Bitmap[] damageFlippedAnimation;
    private final int idleFrame = 0;
    private int movingFrame = 1;

    //Animation Sprites
    private Bitmap damageIdle;
    private Bitmap damageMove1;
    private Bitmap damageMove2;
    private Bitmap damageFlipped;
    private Bitmap damageFlipped2;
    private Bitmap damageFlipped3;
    private Bitmap gunLeft;
    private Bitmap rotatedGunLeft;
    private Bitmap gunRight;
    private Bitmap rotatedGunRight;

    //Character Abilities
    public List<Bullet> bulletList = new ArrayList<Bullet>();
    private float gunLeftAngle = 0;
    private float gunRightAngle = 0;
    public List<FireRain> fireRainList = new ArrayList<FireRain>();
    private boolean fireRainActivated = false;
    private int fireRainCounter = 0;
    private int fireRainCD = 15;
    private boolean firstFireRain = true;
    private int fireRainNum = 3;

    //Character Values
    private String level;
    public String levelUpMsg;

    private int leftBulletTimeCounter = 0;
    private int rightBulletTimeCounter = -60;
    public double baseBulletCD = 1;
    public double baseBulletDamage = 0.6;
    public double bulletCD = 1;
    public double bulletDamage = 0.6;

    private boolean fireIconActivated = false;
    private Bitmap fireIcon;
    private Paint fireIconPaint;
    private int iconAlpha = 150;

    //Sound
    MediaPlayer abulletSoundPlayer;
    MediaPlayer abullet2SoundPlayer;
    MediaPlayer abullet3SoundPlayer;
    MediaPlayer[] abulletSoundList;
    MediaPlayer bbulletSoundPlayer;
    MediaPlayer bbullet2SoundPlayer;
    MediaPlayer bbullet3SoundPlayer;
    MediaPlayer[] bbulletSoundList;
    MediaPlayer bulletSoundPlayer;
    MediaPlayer bullet2SoundPlayer;
    MediaPlayer bullet3SoundPlayer;
    MediaPlayer[] bulletSoundList;
    MediaPlayer rightBulletSoundPlayer;
    MediaPlayer rightBullet2SoundPlayer;
    MediaPlayer rightBullet3SoundPlayer;
    MediaPlayer[] rightBulletSoundList;
    MediaPlayer fireRainSoundPlayer;
    private boolean aSound = false;
    private boolean bSound = false;

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

    public Erina(Context context, int screenWidth, int screenHeight, Player player, Game game) {
        //Variables
        this.game = game;
        this.context = context;
        imgPositionX = screenWidth/2-50;
        imgPositionY = screenHeight/2-100;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;

        //Sound
        bulletSoundPlayer = MediaPlayer.create(context, R.raw.bullet);
        bullet2SoundPlayer = MediaPlayer.create(context, R.raw.bullet2);
        bullet3SoundPlayer = MediaPlayer.create(context, R.raw.bullet3);
        bulletSoundList = new MediaPlayer[]{bullet2SoundPlayer, bullet3SoundPlayer};
        abulletSoundPlayer = MediaPlayer.create(context, R.raw.bullet);
        abullet2SoundPlayer = MediaPlayer.create(context, R.raw.bullet2);
        abullet3SoundPlayer = MediaPlayer.create(context, R.raw.bullet3);
        abulletSoundList = new MediaPlayer[]{abullet2SoundPlayer, abullet3SoundPlayer};
        bbulletSoundPlayer = MediaPlayer.create(context, R.raw.bullet);
        bbullet2SoundPlayer = MediaPlayer.create(context, R.raw.bullet2);
        bbullet3SoundPlayer = MediaPlayer.create(context, R.raw.bullet3);
        bbulletSoundList = new MediaPlayer[]{bbullet2SoundPlayer, bbullet3SoundPlayer};
        rightBulletSoundPlayer = MediaPlayer.create(context, R.raw.bullet);
        rightBullet2SoundPlayer = MediaPlayer.create(context, R.raw.bullet2);
        rightBullet3SoundPlayer = MediaPlayer.create(context, R.raw.bullet3);
        rightBulletSoundList = new MediaPlayer[]{rightBullet2SoundPlayer, rightBullet3SoundPlayer};
        fireRainSoundPlayer = MediaPlayer.create(context, R.raw.firerain);

        //Sprites
        fireIconPaint = new Paint();
        fireIconPaint.setAlpha(iconAlpha);

        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inScaled = false;

        fireIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.firerainicon, bitmapOptions);

        Matrix matrix = new Matrix();

        damageMove1 = BitmapFactory.decodeResource(context.getResources(), R.drawable.erinamove1, bitmapOptions);
        damageIdle = BitmapFactory.decodeResource(context.getResources(), R.drawable.erinaidle, bitmapOptions);
        damageMove2 = BitmapFactory.decodeResource(context.getResources(), R.drawable.erinamove2, bitmapOptions);
        gunLeft = BitmapFactory.decodeResource(context.getResources(), R.drawable.gunleft, bitmapOptions);
        gunRight = BitmapFactory.decodeResource(context.getResources(), R.drawable.gunright, bitmapOptions);

        matrix.postScale(-1, 1, damageMove1.getWidth()/2, damageMove1.getHeight()/2);
        damageFlipped = Bitmap.createBitmap(damageIdle, 0, 0, damageMove1.getWidth(), damageMove1.getHeight(), matrix, true);
        damageFlipped2 = Bitmap.createBitmap(damageMove1, 0, 0, damageMove1.getWidth(), damageMove1.getHeight(), matrix, true);
        damageFlipped3 = Bitmap.createBitmap(damageMove2, 0, 0, damageMove1.getWidth(), damageMove1.getHeight(), matrix, true);

        damageAnimation = new Bitmap[]{damageIdle, damageMove1, damageMove2};
        damageFlippedAnimation = new Bitmap[]{damageFlipped, damageFlipped2, damageFlipped3};
    }

    public void draw(Canvas canvas, Player player, Paint paint, Paint levelPaint, Paint strokePaint, GameDisplay gameDisplay) {
        level = String.valueOf(player.damageLevel);
        switch (player.getPlayerState().getState()) {
            case NOT_MOVING:
            case STARTED_MOVING:
                canvas.drawBitmap(damageAnimation[idleFrame], imgPositionX, imgPositionY, paint);
                break;
            case IS_MOVING:

                if (player.playerVelocityX < 0){
                    canvas.drawBitmap(damageAnimation[movingFrame], imgPositionX, imgPositionY, paint);
                } else {
                    canvas.drawBitmap(damageFlippedAnimation[movingFrame], imgPositionX, imgPositionY, paint);
                }
                break;
            default:
                break;
        }
        Matrix gunLeftMatrix = new Matrix();
        gunLeftMatrix.postRotate(gunLeftAngle);
        rotatedGunLeft = Bitmap.createBitmap(gunLeft, 0, 0, gunLeft.getWidth(), gunLeft.getHeight(), gunLeftMatrix, true);
        canvas.drawBitmap(rotatedGunLeft, imgPositionX+10, imgPositionY+10, paint);

        Matrix gunRightMatrix = new Matrix();
        gunRightMatrix.postRotate(gunRightAngle);
        rotatedGunRight = Bitmap.createBitmap(gunRight, 0, 0, gunRight.getWidth(), gunRight.getHeight(), gunRightMatrix, true);
        canvas.drawBitmap(rotatedGunRight, imgPositionX+60, imgPositionY+10, paint);

        drawFireIcon(canvas);

        for (Bullet bullet: bulletList){
            bullet.drawBullet(canvas, gameDisplay);
        }
        Iterator<FireRain> fireRainIterator = fireRainList.iterator();
        while (fireRainIterator.hasNext()) {
            FireRain fireRain = fireRainIterator.next();
            fireRain.drawFireRain(canvas, gameDisplay);
            if (!fireRain.activated){
                fireRainIterator.remove();
            }
        }

        canvas.drawText(level, imgPositionX+80, imgPositionY+30, strokePaint);
        canvas.drawText(level, imgPositionX+80, imgPositionY+30, levelPaint);
        toggleIdxMovingFrame();
    }

    public void update(Player player, EnemyAnimator enemyAnimator){
        //Timings
        leftBulletTimeCounter++;
        if (leftBulletTimeCounter >= this.bulletCD*UPS){
            if (!game.sfxMuted) {
                if (aSound){
                    abulletSoundList[(int) (Math.random()*2)].start();
                    aSound = false;
                } else {
                    bulletSoundList[(int) (Math.random()*2)].start();
                    aSound = true;
                }
            }
            bulletList.add(new Bullet(context, player, true));
            leftBulletTimeCounter = 0;
        }
        rightBulletTimeCounter++;
        if (rightBulletTimeCounter >= this.bulletCD*UPS){
            if (!game.sfxMuted) {
                if (bSound){
                    bbulletSoundList[(int) (Math.random()*2)].start();
                    bSound = false;
                } else {
                    rightBulletSoundList[(int) (Math.random()*2)].start();
                    bSound = true;
                }
            }
            bulletList.add(new Bullet(context, player, false));
            rightBulletTimeCounter = 0;
        }
        if (fireRainActivated) {
            fireRainCounter++;
            if (fireRainCounter >= fireRainCD*UPS || firstFireRain){
                if (!game.sfxMuted) { fireRainSoundPlayer.start(); }
                if (firstFireRain){
                    firstFireRain = false;
                }
                for (int i = 0; i < fireRainNum; i++) {
                    fireRainList.add(new FireRain(context, player, 0, 0));
                }
                for (FireRain fireRain : fireRainList){
                    fireRain.getRandomPosition();
                    fireRain.activated = true;
                    fireRain.update();
                }
                fireIconActivated = true;
                fireRainCounter = 0;
            }
        }

        //Update Bullets
        for (Bullet bullet : bulletList) {
            if (!bullet.foundEnemy){
                bullet.getClosestEnemy(enemyAnimator.enemyList, player);
            }
            bullet.update();
            if (bullet.leftBullet){
                this.gunLeftAngle = bullet.getBulletAngle();
            } else {
                this.gunRightAngle = bullet.getBulletAngle();
            }
        }

        //Collision
        for (Enemy enemy : enemyAnimator.enemyList) {
            Iterator<Bullet> bulletIterator = bulletList.iterator();
            while (bulletIterator.hasNext()) {
                Circle bullet = bulletIterator.next();
                if (Circle.isColliding(bullet, enemy)) {
                    bulletIterator.remove();
                    enemy.hitPoints -= bulletDamage;
                    break;
                }
                if (bullet.getPositionX() >= player.getPositionX() + screenWidth ||
                        bullet.getPositionX() <= player.getPositionX() - screenWidth ||
                        bullet.getPositionY() >= player.getPositionY() + screenHeight ||
                        bullet.getPositionY() <= player.getPositionY() - screenHeight) {
                    bulletIterator.remove();
                    break;
                }
            }
            for (Circle fireRain : fireRainList) {
                if (Circle.isColliding(fireRain, enemy)) {
                    enemy.hitPoints -= bulletDamage;
                    break;
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

    public void drawFireIcon(Canvas canvas){
        if (fireIconActivated){
            canvas.drawBitmap(fireIcon,
                    imgPositionX,
                    imgPositionY,
                    fireIconPaint
            );
            fireIconPaint.setAlpha(iconAlpha);
            iconAlpha -= 3;
            if (iconAlpha <= 0){
                iconAlpha = 150;
                fireIconActivated = false;
            }
        }
    }

    public void getLevelUpText(int damageLevel) {
        if (damageLevel < 1) {
            levelUpMsg = "-0.1s Reload Time";
        }
        if (damageLevel == 1) {
            levelUpMsg = "+0.4 Damage";
        }
        if (damageLevel == 2) {
            levelUpMsg = "+Ability: Fire Rain \n(15s Cool-down, 3 Charges)";
        }
        if (damageLevel == 3) {
            levelUpMsg = "-0.1 Reload Time, \n-2s Fire Rain CD";
        }
        if (damageLevel == 4) {
            levelUpMsg = "+0.4 Damage \n+1 Fire Rain Charge";
        }
        if (damageLevel == 5) {
            levelUpMsg = "-0.1 Reload Time, \n-1s Fire Rain CD";
        }
        if (damageLevel == 6) {
            levelUpMsg = "+0.4 Damage, \n-1s Fire Rain CD";
        }
        if (damageLevel == 7) {
            levelUpMsg = "-0.1 Reload Time, \n+1 Fire Rain Charge";
        }
        if (damageLevel == 8) {
            levelUpMsg = "+0.4 Damage, \5-1s Fire Rain CD";
        }
        if (damageLevel == 9) {
            levelUpMsg = "+1 Damage, \n-1s Fire Rain CD";
        }
        if (damageLevel >= 10) {
            levelUpMsg = "+0.3 Damage";
        }
    }

    public void checkLevelPerks(int damageLevel) {
        if (damageLevel < 1){
            baseBulletCD -= 0.1;
        }
        if (damageLevel == 1) {
            baseBulletDamage += 0.4;
        }
        if (damageLevel == 2) {
            fireRainActivated = true;
        }
        if (damageLevel == 3) {
            baseBulletCD -= 0.1;
            fireRainCD -= 2;
        }
        if (damageLevel == 4) {
            baseBulletDamage += 0.4;
            fireRainNum += 1;
        }
        if (damageLevel == 5) {
            baseBulletCD -= 0.1;
            fireRainCD -= 1;
        }
        if (damageLevel == 6) {
            baseBulletDamage += 0.4;
            fireRainCD -= 1;
        }
        if (damageLevel == 7) {
            baseBulletCD -= 0.1;
            fireRainNum += 1;
        }
        if (damageLevel == 8) {
            baseBulletDamage += 0.4;
            fireRainCD -= 1;
        }
        if (damageLevel == 9) {
            baseBulletDamage += 1;
            fireRainCD -= 1;
        }
        if (damageLevel >= 10) {
            baseBulletDamage += 0.3;
        }
    }
}
