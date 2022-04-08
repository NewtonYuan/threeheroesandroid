package com.prestige.prestigegame.gameobject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Typeface;
import android.util.Log;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;

import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.prestige.prestigegame.GameDisplay;
import com.prestige.prestigegame.GameLoop;
import com.prestige.prestigegame.R;
import com.prestige.prestigegame.Utils;
import com.prestige.prestigegame.gamepanel.HealthBar;
import com.prestige.prestigegame.gamepanel.Joystick;
import com.prestige.prestigegame.gamepanel.ProgressBar;
import com.prestige.prestigegame.graphics.CharacterList;
import com.prestige.prestigegame.graphics.DamageAnimator;
import com.prestige.prestigegame.graphics.HealerAnimator;
import com.prestige.prestigegame.graphics.TankAnimator;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Player is the main character of the game, which the user can control with a touch joystick.
 * The player class is an extension of a Circle, which is an extension of a GameObject
 */
public class Player extends Circle {
    public static final double SPEED_PIXELS_PER_SECOND = 400.0;
    private static final double MAX_SPEED = SPEED_PIXELS_PER_SECOND / GameLoop.MAX_UPS;
    public int MAX_HEALTH_POINTS = 50;
    private Joystick joystick;
    private HealthBar healthBar;
    private int healthPoints = MAX_HEALTH_POINTS;
    private TankAnimator tankAnimator;
    private DamageAnimator damageAnimator;
    private HealerAnimator healerAnimator;
    private PlayerState playerState;
    private Paint playerPaint;
    private Paint shadow;
    private int screenHeight;
    private int screenWidth;
    private int imgPositionX;
    private int imgPositionY;
    private CharacterList characterList;
    private Context context;
    private ProgressBar progressBar;
    public int tankLevel = 0;
    public int damageLevel = 0;
    public int healerLevel = 0;
    private Paint levelPaint = new Paint();
    private Paint strokePaint = new Paint();
    private Paint invincibilityPaint = new Paint();
    private int counter = 0;
    public boolean invincibilityMode = false;
    public boolean toggleInvincible = false;
    public double playerVelocityX = 0;
    private int invincibilityTimer = 0;
    private final double UPS = GameLoop.MAX_UPS;
    Timer timer = new Timer();
    Timer timer2 = new Timer();

    public Player(Context context, Joystick joystick, double positionX, double positionY, double radius, TankAnimator tankAnimator, DamageAnimator damageAnimator, HealerAnimator healerAnimator, ProgressBar progressBar) {
        super(context, ContextCompat.getColor(context, R.color.player), positionX, positionY, radius);
        this.joystick = joystick;
        this.healthBar = new HealthBar(context, this);
        this.tankAnimator = tankAnimator;
        this.damageAnimator = damageAnimator;
        this.healerAnimator = healerAnimator;
        this.playerState = new PlayerState(this);
        this.context = context;
        this.progressBar = progressBar;

        tankAnimator = new TankAnimator(context);

        playerPaint = new Paint();
        playerPaint.setColor(Color.RED);
        playerPaint.setStyle(Paint.Style.STROKE);

        shadow = new Paint();
        shadow.setColor(Color.BLACK);
        shadow.setStyle(Paint.Style.FILL_AND_STROKE);
        shadow.setAlpha(70);

        screenWidth = context.getResources().getDisplayMetrics().widthPixels;
        screenHeight = context.getResources().getDisplayMetrics().heightPixels;

        imgPositionX = screenWidth/2-75;
        imgPositionY = screenHeight/2-75;
        characterList = new CharacterList(context);

        final Typeface font = ResourcesCompat.getFont(context, R.font.customfont);
        levelPaint.setColor(Color.WHITE);
        levelPaint.setTextSize(50);
        levelPaint.setTextAlign(Paint.Align.CENTER);
        levelPaint.setTypeface(font);

        strokePaint.setColor(Color.BLACK);
        strokePaint.setTextSize(50);
        strokePaint.setTextAlign(Paint.Align.CENTER);
        strokePaint.setTypeface(font);
        strokePaint.setStyle(Paint.Style.STROKE);
        strokePaint.setStrokeWidth(5);

        ColorFilter invincibilityFilter = new PorterDuffColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
        invincibilityPaint.setColorFilter(invincibilityFilter);
    }

    public void update() {
        if(toggleInvincible){
            invincibilityMode = true;
            invincibilityTimer++;
            if (invincibilityTimer >= UPS*1.5){
                invincibilityMode = false;
                toggleInvincible = false;
                invincibilityTimer = 0;
            }
        }

        // Update velocity based on actuator of joystick
        velocityX = joystick.getActuatorX()*MAX_SPEED;
        velocityY = joystick.getActuatorY()*MAX_SPEED;

        // Update position
        positionX += velocityX;
        positionY += velocityY;

        playerVelocityX = velocityX;

        // Update direction
        if (velocityX != 0 || velocityY != 0) {
            // Normalize velocity to get direction (unit vector of velocity)
            double distance = Utils.getDistanceBetweenPoints(0, 0, velocityX, velocityY);
            directionX = velocityX/distance;
            directionY = velocityY/distance;
        }

        playerState.update();
    }

    public void draw(Canvas canvas, GameDisplay gameDisplay) {
        String tankLevel = String.valueOf(this.tankLevel);
        String damageLevel = String.valueOf(this.damageLevel);
        String healerLevel = String.valueOf(this.healerLevel);

        Bitmap damageBmp = characterList.getDamageBmp();
        Bitmap healerBmp = characterList.getHealerBmp();
        Bitmap tankBmp = characterList.getTankBmp();

        //Shadows
        canvas.drawOval(screenWidth/2-125, screenHeight/2+145, screenWidth/2-25, screenHeight/2+170, shadow);
        canvas.drawOval(screenWidth/2+25, screenHeight/2+145, screenWidth/2+125, screenHeight/2+170, shadow);
        canvas.drawOval(screenWidth/2-50, screenHeight/2+15, screenWidth/2+50, screenHeight/2+40, shadow);

        if (!invincibilityMode){
            damageAnimator.draw(canvas, imgPositionX, imgPositionY-80, this, null);
            healerAnimator.draw(canvas, imgPositionX-80, imgPositionY+50, this, null);
            tankAnimator.draw(canvas, imgPositionX+80, imgPositionY+80, this, null);
        } else {
            damageAnimator.draw(canvas, imgPositionX, imgPositionY-80, this, invincibilityPaint);
            healerAnimator.draw(canvas, imgPositionX-80, imgPositionY+50, this, invincibilityPaint);
            tankAnimator.draw(canvas, imgPositionX+80, imgPositionY+80, this, invincibilityPaint);
        }


        canvas.drawText(damageLevel, imgPositionX+130, imgPositionY-55, strokePaint);
        canvas.drawText(damageLevel, imgPositionX+130, imgPositionY-55, levelPaint);

        canvas.drawText(healerLevel, imgPositionX+50, imgPositionY+100, strokePaint);
        canvas.drawText(healerLevel, imgPositionX+50, imgPositionY+100, levelPaint);

        canvas.drawText(tankLevel, imgPositionX+200, imgPositionY+100, strokePaint);
        canvas.drawText(tankLevel, imgPositionX+200, imgPositionY+100, levelPaint);
        healthBar.draw(canvas, gameDisplay);
    }

    public int getHealthPoint() {
        return healthPoints;
    }

    public void setHealthPoint(int healthPoints) {
        // Only allow positive values
        if (healthPoints >= 0){
            this.healthPoints = healthPoints;
        } else {
            this.healthPoints = 0;
        }
    }

    public PlayerState getPlayerState() {
        return playerState;
    }
}
