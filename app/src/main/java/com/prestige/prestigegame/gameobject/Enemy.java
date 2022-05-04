/*package com.prestige.prestigegame.gameobject;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import androidx.core.content.ContextCompat;

import com.prestige.prestigegame.GameDisplay;
import com.prestige.prestigegame.GameLoop;
import com.prestige.prestigegame.R;
import com.prestige.prestigegame.graphics.CharacterList;

public class Enemy extends Circle {


    public Enemy(Context context, Player player, double positionX, double positionY, double radius) {
        super(context, ContextCompat.getColor(context, R.color.enemy), positionX, positionY, radius);
        this.player = player;

        screenWidth = context.getResources().getDisplayMetrics().widthPixels;
        screenHeight = context.getResources().getDisplayMetrics().heightPixels;

        characterList = new CharacterList(context);

        shadow = new Paint();
        shadow.setColor(Color.BLACK);
        shadow.setStyle(Paint.Style.FILL_AND_STROKE);
        shadow.setAlpha(70);
    }

    public static boolean readyToSpawn() {

        if (updatesUntilNextSpawn <= 0) {
            updatesUntilNextSpawn += UPDATES_PER_SPAWN;
            return true;
        } else {
            updatesUntilNextSpawn --;
            return false;
        }
    }
    public void drawEnemy(Canvas canvas, GameDisplay gameDisplay) {
        canvas.drawBitmap(characterList.getEnemyBmp(),
                (float) gameDisplay.gameToDisplayCoordinatesX(positionX),
                (float) gameDisplay.gameToDisplayCoordinatesY(positionY),
                null
        );
        canvas.drawOval((float) gameDisplay.gameToDisplayCoordinatesX(positionX),
                (float) gameDisplay.gameToDisplayCoordinatesY(positionY)+55,
                (float) gameDisplay.gameToDisplayCoordinatesX(positionX)+50,
                (float) gameDisplay.gameToDisplayCoordinatesY(positionY)+65,
                shadow);
    }

    public void update() {

        angle = Math.random()*Math.PI*2;
        newPositionX = Math.cos(angle)*500+player.positionX;
        newPositionY = Math.sin(angle)*500+player.positionY;
    }

    public double getNewPositionX(){
        return newPositionX;
    }

    public double getNewPositionY(){
        return newPositionY;
    }
}
*/
package com.prestige.prestigegame.gameobject;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Typeface;

import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.prestige.prestigegame.GameDisplay;
import com.prestige.prestigegame.GameLoop;
import com.prestige.prestigegame.R;
import com.prestige.prestigegame.graphics.CharacterList;
import com.prestige.prestigegame.graphics.EnemyAnimator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Enemy is a character which always moves in the direction of the player.
 * The Enemy class is an extension of a Circle, which is an extension of a GameObject
 */
public class Enemy extends Circle {
    public double ORIGINAL_SPEED = Player.SPEED_PIXELS_PER_SECOND*0.7 / GameLoop.MAX_UPS;
    public double MAX_SPEED = Player.SPEED_PIXELS_PER_SECOND*0.7 / GameLoop.MAX_UPS;
    private static final double SPAWNS_PER_MINUTE = 180;
    private static final double SPAWNS_PER_SECOND = SPAWNS_PER_MINUTE/80.0;
    private static final double UPDATES_PER_SPAWN = GameLoop.MAX_UPS/SPAWNS_PER_SECOND;
    public double updatesUntilNextSpawn = UPDATES_PER_SPAWN;
    private Player player;
    private int screenWidth;
    private int screenHeight;
    private CharacterList characterList;
    private Paint shadow;
    private double angle;
    private double newPositionX;
    private double newPositionY;
    private EnemyAnimator enemyAnimator;
    public boolean knockedBack = false;
    public boolean frozen = false;
    public double enemyVelocityX = 0;
    public int hitPoints = 0;
    public int enemyLevel = 0;
    public int damage = 0;
    public boolean finishedDying = false;
    private Paint ouchPaint = new Paint();
    private Paint frozenPaint = new Paint();
    private int alpha = 255;
    public int maxEnemies = 20;
    private int frozenAlpha = 200;
    private Paint levelPaint = new Paint();
    private Paint strokePaint = new Paint();
    private final double UPS = GameLoop.MAX_UPS;
    public final double delay = UPS*7;
    public int chosenLevel = 0;
    public List<Integer> levelProbability = new ArrayList<Integer>(Collections.nCopies(100, 0));

    public Enemy(Context context, Player player, double positionX, double positionY, double radius, EnemyAnimator enemyAnimator, int level) {
        super(context, ContextCompat.getColor(context, R.color.enemy), positionX, positionY, radius);
        this.player = player;
        this.enemyAnimator = enemyAnimator;
        this.enemyLevel = level;

        if(this.enemyLevel <= 10){
            this.hitPoints = enemyLevel + 1;
            this.damage = enemyLevel*2 + 5;
        }
        if(this.enemyLevel > 5 && this.enemyLevel <= 10){
            this.MAX_SPEED = Player.SPEED_PIXELS_PER_SECOND * 0.725 / GameLoop.MAX_UPS;
        }
        if(this.enemyLevel > 10){
            this.MAX_SPEED = Player.SPEED_PIXELS_PER_SECOND * 0.75 / GameLoop.MAX_UPS;
            this.hitPoints = enemyLevel*2;
            this.damage = enemyLevel*3;
        }

        screenWidth = context.getResources().getDisplayMetrics().widthPixels;
        screenHeight = context.getResources().getDisplayMetrics().heightPixels;

        characterList = new CharacterList(context);

        shadow = new Paint();
        shadow.setColor(Color.BLACK);
        shadow.setStyle(Paint.Style.FILL_AND_STROKE);
        shadow.setAlpha(70);

        ColorFilter ouchFilter = new PorterDuffColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);
        ouchPaint.setColorFilter(ouchFilter);
        ouchPaint.setAlpha(alpha);

        int frozenColor = ContextCompat.getColor(context, R.color.enemyFrozen);
        ColorFilter frozenFilter = new PorterDuffColorFilter(frozenColor, PorterDuff.Mode.SRC_IN);
        frozenPaint.setColorFilter(frozenFilter);
        frozenPaint.setAlpha(frozenAlpha);

        final Typeface font = ResourcesCompat.getFont(context, R.font.customfont);
        levelPaint.setColor(Color.WHITE);
        levelPaint.setTextSize(35);
        levelPaint.setTextAlign(Paint.Align.CENTER);
        levelPaint.setTypeface(font);

        strokePaint.setColor(Color.BLACK);
        strokePaint.setTextSize(35);
        strokePaint.setTextAlign(Paint.Align.CENTER);
        strokePaint.setTypeface(font);
        strokePaint.setStyle(Paint.Style.STROKE);
        strokePaint.setStrokeWidth(5);
    }

    /**
     * readyToSpawn checks if a new enemy should spawn, according to the decided number of spawns
     * per minute (see SPAWNS_PER_MINUTE at top)
     * @return
     */
    public boolean readyToSpawn() {

        if (updatesUntilNextSpawn <= 0) {
            updatesUntilNextSpawn += UPDATES_PER_SPAWN;
            return true;
        } else {
            updatesUntilNextSpawn --;
            return false;
        }
    }

    public void drawEnemy(Canvas canvas, GameDisplay gameDisplay) {
        String enemyLevel = String.valueOf(this.enemyLevel);
        if (frozen){
            enemyAnimator.draw(canvas, (float) gameDisplay.gameToDisplayCoordinatesX(positionX), (float) gameDisplay.gameToDisplayCoordinatesY(positionY), frozenPaint, this);
            frozenPaint.setAlpha(alpha);
            frozenAlpha -= 2;
            if (frozenAlpha <= 0){
                frozenAlpha = 255;
                frozen = false;
            }
        }
        if (hitPoints <= 0){
            enemyAnimator.draw(canvas, (float) gameDisplay.gameToDisplayCoordinatesX(positionX), (float) gameDisplay.gameToDisplayCoordinatesY(positionY), ouchPaint, this);
            ouchPaint.setAlpha(alpha);
            alpha -= 20;
            if (alpha <= 70){
                shadow.setAlpha(alpha);
            }
            if (alpha <= 0){
                alpha = 255;
                finishedDying = true;
            }
        }
        if (!frozen && hitPoints > 0){
            enemyAnimator.draw(canvas, (float) gameDisplay.gameToDisplayCoordinatesX(positionX), (float) gameDisplay.gameToDisplayCoordinatesY(positionY), null, this);
        }

        if (enemyAnimator.flipped){
            canvas.drawOval((float) gameDisplay.gameToDisplayCoordinatesX(positionX)-10,
                    (float) gameDisplay.gameToDisplayCoordinatesY(positionY)+20,
                    (float) gameDisplay.gameToDisplayCoordinatesX(positionX)+30,
                    (float) gameDisplay.gameToDisplayCoordinatesY(positionY)+35,
                    shadow);
        } else {
            canvas.drawOval((float) gameDisplay.gameToDisplayCoordinatesX(positionX)-30,
                    (float) gameDisplay.gameToDisplayCoordinatesY(positionY)+20,
                    (float) gameDisplay.gameToDisplayCoordinatesX(positionX)+10,
                    (float) gameDisplay.gameToDisplayCoordinatesY(positionY)+35,
                    shadow);
        }

        if(this.enemyLevel >= 2){
            levelPaint.setColor(Color.parseColor("#FFFAF1"));
        }
        if(this.enemyLevel >= 4){
            levelPaint.setColor(Color.parseColor("#EDDDB8"));
        }
        if(this.enemyLevel >= 7){
            levelPaint.setColor(Color.parseColor("#DCC080"));
        }
        if(this.enemyLevel == 10){
            levelPaint.setColor(Color.parseColor("#CAA347"));
        }
        if(this.enemyLevel > 10){
            levelPaint.setColor(Color.parseColor("#FF6666"));
        }
        canvas.drawText(enemyLevel, (float)gameDisplay.gameToDisplayCoordinatesX(positionX)+20, (float)gameDisplay.gameToDisplayCoordinatesY(positionY)-5, strokePaint);
        canvas.drawText(enemyLevel, (float)gameDisplay.gameToDisplayCoordinatesX(positionX)+20, (float)gameDisplay.gameToDisplayCoordinatesY(positionY)-5, levelPaint);
    }

    public void update() {
        // =========================================================================================
        //   Update velocity of the enemy so that the velocity is in the direction of the player
        // =========================================================================================
        // Calculate vector from enemy to player (in x and y)
        double distanceToPlayerX = player.getPositionX() - positionX;
        double distanceToPlayerY = player.getPositionY() - positionY;

        // Calculate (absolute) distance between enemy (this) and player
        double distanceToPlayer = GameObject.getDistanceBetweenObjects(this, player);

        // Calculate direction from enemy to player
        double directionX = distanceToPlayerX / distanceToPlayer;
        double directionY = distanceToPlayerY / distanceToPlayer;

        // Set velocity in the direction to the player
        if (distanceToPlayer > 0) { // Avoid division by zero
            velocityX = directionX * MAX_SPEED;
            velocityY = directionY * MAX_SPEED;
        } else {
            velocityX = 0;
            velocityY = 0;
        }
        enemyVelocityX = velocityX;

        // =========================================================================================
        //   Update position of the enemy
        // =========================================================================================
        if (knockedBack){
            positionX -= velocityX*4;
            positionY -= velocityY*4;
            if (positionX >= player.positionX+screenWidth/2
            || positionX <= player.positionX-screenWidth/2
            || positionY >= player.positionY+screenHeight/3
            || positionY <= player.positionY-screenHeight/3){
                knockedBack = false;
            }
        }
        if (hitPoints <= 0 || frozen){ }
        else {
            positionX += velocityX;
            positionY += velocityY;
        }
    }

    public void getNewPosition() {
        angle = Math.random()*Math.PI*2;
        newPositionX = Math.cos(angle)*screenWidth/4*3+player.positionX;
        newPositionY = Math.sin(angle)*screenHeight/4*3+player.positionY;
    }

    public double getNewPositionX(){
        return newPositionX;
    }

    public double getNewPositionY(){
        return newPositionY;
    }

    public int getEnemyLevel(){
        chosenLevel = levelProbability.get((int)(Math.random()*100));
        return chosenLevel;
    }
}

