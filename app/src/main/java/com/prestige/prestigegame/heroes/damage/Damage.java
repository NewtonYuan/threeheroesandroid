package com.prestige.prestigegame.heroes.damage;

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
import com.prestige.prestigegame.gameobject.ExplosiveArrow;
import com.prestige.prestigegame.gameobject.Player;
import com.prestige.prestigegame.graphics.EnemyAnimator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Damage {
    //Animation Arrays
    private Bitmap[] damageAnimation;
    private Bitmap[] damageFlippedAnimation;
    private int idleFrame = 0;
    private int movingFrame = 1;

    //Animation Sprites
    private Bitmap damageIdle;
    private Bitmap damageMove1;
    private Bitmap damageMove2;
    private Bitmap damageFlipped;
    private Bitmap damageFlipped2;
    private Bitmap damageFlipped3;

    //Character Weapon
    private Bitmap bow;
    private Bitmap rotatedBow;
    public float bowAngle = 0;
    public List<Arrow> arrowList = new ArrayList<Arrow>();
    public List<GodArrow> godArrowList = new ArrayList<GodArrow>();
    private List<ExplosiveArrow> explosiveArrowList = new ArrayList<ExplosiveArrow>();
    private Explosion explosion;

    //Character Values
    private String level;
    private int arrowTimeCounter = 0;
    private int godArrowTimeCounter = 0;
    private int explosiveArrowTimeCounter = 0;
    public double baseArrowReloadTime = 1;
    public double baseArrowDamage = 1;
    public double arrowReloadTime = 1;
    public double arrowDamage = 1;
    public double explosiveArrowReloadTime = 10;
    public boolean explosiveArrowActivated = false;
    public boolean godArrowActivated = false;
    public String levelUpMsg;

    //Sound
    MediaPlayer arrowSoundPlayer;
    MediaPlayer arrow2SoundPlayer;
    MediaPlayer arrow3SoundPlayer;
    MediaPlayer[] arrowSoundList;
    MediaPlayer explosionSoundPlayer;

    //Constants
    private int screenWidth;
    private int screenHeight;
    private int updatesBeforeNextMoveFrame = MAX_UPDATES_BEFORE_NEXT_MOVE_FRAME;
    private static final int MAX_UPDATES_BEFORE_NEXT_MOVE_FRAME = 5;
    private final double UPS = GameLoop.MAX_UPS;
    private float imgPositionX;
    private float imgPositionY;
    private Context context;

    public Damage(Context context, int screenWidth, int screenHeight, Player player) {
        //Variables
        this.context = context;
        imgPositionX = screenWidth/2-50;
        imgPositionY = screenHeight/2-100;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;

        //Sound
        arrowSoundPlayer = MediaPlayer.create(context, R.raw.arrow);
        arrow2SoundPlayer = MediaPlayer.create(context, R.raw.arrow2);
        arrow3SoundPlayer = MediaPlayer.create(context, R.raw.arrow3);
        arrowSoundList = new MediaPlayer[]{arrowSoundPlayer, arrow2SoundPlayer, arrow3SoundPlayer};
        explosionSoundPlayer = MediaPlayer.create(context, R.raw.explosion);

        //Sprites
        explosion = new Explosion(context, player, 0,0, 125);

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

    public void draw(Canvas canvas, Player player, Paint paint, Paint levelPaint, Paint strokePaint, GameDisplay gameDisplay) {
        level = String.valueOf(player.damageLevel);
        switch (player.getPlayerState().getState()) {
            case NOT_MOVING:
                if (bowAngle > 0){
                    canvas.drawBitmap(damageAnimation[idleFrame], imgPositionX, imgPositionY, paint);
                } else {
                    canvas.drawBitmap(damageFlippedAnimation[idleFrame], imgPositionX, imgPositionY, paint);
                }
                break;
            case STARTED_MOVING:
                canvas.drawBitmap(damageFlippedAnimation[idleFrame], imgPositionX, imgPositionY, paint);
                break;
            case IS_MOVING:
                if (bowAngle > 0){
                    canvas.drawBitmap(damageAnimation[movingFrame], imgPositionX, imgPositionY, paint);
                } else {
                    canvas.drawBitmap(damageFlippedAnimation[movingFrame], imgPositionX, imgPositionY, paint);
                }
                break;
            default:
                break;
        }
        Matrix bowMatrix = new Matrix();
        bowMatrix.postRotate(bowAngle);
        rotatedBow = Bitmap.createBitmap(bow, 0, 0, bow.getWidth(), bow.getHeight(), bowMatrix, true);
        canvas.drawBitmap(rotatedBow, imgPositionX, imgPositionY, paint);

        for (Arrow arrow: arrowList){
            arrow.drawArrow(canvas, gameDisplay);
        }
        for (GodArrow godArrow : godArrowList){
            godArrow.drawArrow(canvas, gameDisplay);
        }
        for (ExplosiveArrow explosive : explosiveArrowList){
            explosive.drawArrow(canvas, gameDisplay);
        }
        explosion.drawExplosion(canvas);

        canvas.drawText(level, imgPositionX+80, imgPositionY+30, strokePaint);
        canvas.drawText(level, imgPositionX+80, imgPositionY+30, levelPaint);
        toggleIdxMovingFrame();
    }

    public void update(Player player, EnemyAnimator enemyAnimator, Game game){
        explosion.update();

        //Timings
        arrowTimeCounter++;
        if (arrowTimeCounter >= this.arrowReloadTime*UPS){
            arrowList.add(new Arrow(context, player));
            if (!game.sfxMuted) { arrowSoundList[(int) (Math.random()*3)].start(); }
            arrowTimeCounter = 0;
        }
        godArrowTimeCounter++;
        if (this.godArrowActivated){
            if (godArrowTimeCounter >= UPS*7){
                godArrowList.add(new GodArrow(context, player));
                if (!game.sfxMuted) { arrowSoundList[(int) (Math.random()*3)].start(); }
                godArrowTimeCounter = 0;
            }
        }
        explosiveArrowTimeCounter++;
        if (explosiveArrowTimeCounter >= this.explosiveArrowReloadTime*UPS && this.explosiveArrowActivated){
            explosiveArrowList.add(new ExplosiveArrow(context, player));
            if (!game.sfxMuted) { arrowSoundList[(int) (Math.random()*3)].start(); }
            explosiveArrowTimeCounter = 0;
        }

        //New Arrows
        for (Arrow arrow : arrowList) {
            if (!arrow.foundEnemy){
                arrow.getClosestEnemy(enemyAnimator.enemyList, player);
            }
            arrow.update();
            this.bowAngle = arrow.getArrowAngle();
        }
        if (this.godArrowActivated){
            for (GodArrow godArrow : godArrowList) {
                if(!godArrow.foundEnemy){
                    godArrow.getClosestEnemy(enemyAnimator.enemyList, player);
                }
                godArrow.update();
                this.bowAngle = godArrow.getArrowAngle();
            }
        }
        for (ExplosiveArrow explosive : explosiveArrowList) {
            if(!explosive.foundEnemy){
                explosive.getClosestEnemy(enemyAnimator.enemyList, player);
            }
            explosive.update();
            this.bowAngle = explosive.getArrowAngle();
        }

        //Collision
        Iterator<Enemy> enemyIterator = enemyAnimator.enemyList.iterator();
        while (enemyIterator.hasNext()){
            Enemy enemy = enemyIterator.next();
            Iterator<Arrow> arrowIterator = arrowList.iterator();
            while (arrowIterator.hasNext()) {
                Circle arrow = arrowIterator.next();
                if (Circle.isColliding(arrow, enemy)) {
                    arrowIterator.remove();
                    enemy.hitPoints -= this.arrowDamage;
                    break;
                }
                if (arrow.getPositionX() >= player.getPositionX()+screenWidth ||
                        arrow.getPositionX() <= player.getPositionX()-screenWidth ||
                        arrow.getPositionY() >= player.getPositionY()+screenHeight ||
                        arrow.getPositionY() <= player.getPositionY()-screenHeight){
                    arrowIterator.remove();
                    break;
                }
            }

            Iterator<GodArrow> godArrowIterator = godArrowList.iterator();
            while (godArrowIterator.hasNext()) {
                Circle godArrow = godArrowIterator.next();
                // Remove enemy if it collides with a spell
                if (Circle.isColliding(godArrow, enemy)) {
                    enemy.hitPoints -= this.arrowDamage*3;
                    break;
                }
                if (godArrow.getPositionX() >= player.getPositionX()+screenWidth ||
                        godArrow.getPositionX() <= player.getPositionX()-screenWidth ||
                        godArrow.getPositionY() >= player.getPositionY()+screenHeight ||
                        godArrow.getPositionY() <= player.getPositionY()-screenHeight){
                    godArrowIterator.remove();
                    break;
                }
            }

            Iterator<ExplosiveArrow> explosiveIterator = explosiveArrowList.iterator();
            while (explosiveIterator.hasNext()) {
                Circle explosive = explosiveIterator.next();
                // Remove enemy if it collides with a spell
                if (Circle.isColliding(explosive, enemy)) {
                    if (!game.sfxMuted) { explosionSoundPlayer.start(); }
                    explosion.getExplosionPosition((int)enemy.getPositionX(), (int)enemy.getPositionY());
                    explosiveIterator.remove();
                    break;
                }
                if (explosive.getPositionX() >= player.getPositionX()+screenWidth ||
                        explosive.getPositionX() <= player.getPositionX()-screenWidth ||
                        explosive.getPositionY() >= player.getPositionY()+screenHeight ||
                        explosive.getPositionY() <= player.getPositionY()-screenHeight){
                    explosiveIterator.remove();
                    break;
                }
            }
            if (explosion.activated){
                if (Circle.isColliding(enemy, explosion)){
                    enemy.hitPoints -= this.arrowDamage*2;
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
            baseArrowReloadTime -= 0.1;
        }
        if (damageLevel == 1) {
            arrowDamage += 0.5;
            baseArrowDamage += 0.5;
        }
        if (damageLevel == 2) {
            arrowReloadTime -= 0.1;
            baseArrowReloadTime -= 0.1;
        }
        if (damageLevel == 3) {
            arrowDamage += 0.5;
            baseArrowDamage += 0.5;
        }
        if (damageLevel == 4) {
            explosiveArrowActivated = true;
        }
        if (damageLevel == 5) {
            arrowReloadTime -= 0.1;
            baseArrowReloadTime -= 0.1;
            explosiveArrowReloadTime -= 1;
        }
        if (damageLevel == 6) {
            arrowDamage += 0.5;
            baseArrowDamage += 0.5;
            explosiveArrowReloadTime -= 1;
        }
        if (damageLevel == 7) {
            arrowReloadTime -= 0.1;
            arrowReloadTime -= 0.1;
            explosiveArrowReloadTime -= 1;
        }
        if (damageLevel == 8) {
            arrowDamage += 0.5;
            baseArrowDamage += 0.5;
            explosiveArrowReloadTime -= 1;
        }
        if (damageLevel == 9) {
            godArrowActivated = true;
        }
        if (damageLevel >= 10) {
            arrowDamage += 0.2;
            baseArrowDamage += 0.2;
        }
    }
}
