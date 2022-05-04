package com.prestige.prestigegame.gameobject;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Typeface;

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
import com.prestige.prestigegame.graphics.TankAnimator;

/**
 * Player is the main character of the game, which the user can control with a touch joystick.
 * The player class is an extension of a Circle, which is an extension of a GameObject
 */
public class Player extends Circle {
    public static final double SPEED_PIXELS_PER_SECOND = 480.0;
    private static final double MAX_SPEED = SPEED_PIXELS_PER_SECOND / GameLoop.MAX_UPS;
    public float MAX_HEALTH_POINTS = 50;
    private Joystick joystick;
    public HealthBar healthBar;
    private float healthPoints = MAX_HEALTH_POINTS;
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

    public Player(Context context, Joystick joystick, double positionX, double positionY, double radius, ProgressBar progressBar) {
        super(context, ContextCompat.getColor(context, R.color.player), positionX, positionY, radius);
        this.joystick = joystick;
        this.healthBar = new HealthBar(context, this);
        this.playerState = new PlayerState(this);
        this.context = context;
        this.progressBar = progressBar;

        playerPaint = new Paint();
        playerPaint.setColor(Color.RED);
        playerPaint.setStyle(Paint.Style.STROKE);

        shadow = new Paint();
        shadow.setColor(Color.BLACK);
        shadow.setStyle(Paint.Style.FILL_AND_STROKE);
        shadow.setAlpha(70);

        screenWidth = context.getResources().getDisplayMetrics().widthPixels;
        screenHeight = context.getResources().getDisplayMetrics().heightPixels;

        imgPositionX = screenWidth/2-50;
        imgPositionY = screenHeight/2-50;
        characterList = new CharacterList(context);

        final Typeface font = ResourcesCompat.getFont(context, R.font.customfont);
        levelPaint.setColor(Color.WHITE);
        levelPaint.setTextSize(40);
        levelPaint.setTextAlign(Paint.Align.CENTER);
        levelPaint.setTypeface(font);

        strokePaint.setColor(Color.BLACK);
        strokePaint.setTextSize(40);
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
        //Shadows
        canvas.drawOval(screenWidth/2-25, screenHeight/2-10, screenWidth/2+25, screenHeight/2+10, shadow);
        canvas.drawOval(screenWidth/2-75, screenHeight/2+90, screenWidth/2-25, screenHeight/2+110, shadow);
        canvas.drawOval(screenWidth/2+25, screenHeight/2+90, screenWidth/2+75, screenHeight/2+110, shadow);
    }

    public float getHealthPoint() {
        return healthPoints;
    }

    public void setHealthPoint(float healthPoints) {
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
