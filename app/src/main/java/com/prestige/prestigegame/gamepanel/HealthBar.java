package com.prestige.prestigegame.gamepanel;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

import androidx.core.content.ContextCompat;

import com.prestige.prestigegame.GameDisplay;
import com.prestige.prestigegame.R;
import com.prestige.prestigegame.gameobject.Player;

/**
 * HealthBar display the players health to the screen
 */
public class HealthBar {
    private Player player;
    private Paint borderPaint, healthPaint, outlinePaint;
    private int width, height, margin; // pixel value
    private float healthPointPercentage;
    private int healthColor;
    private int healthColorFull;
    private int health3Color;
    private int health2Color;
    private int health1Color;
    private int healthLowColor;
    private int healthEmptyColor;
    private Paint healthSeparator;

    public HealthBar(Context context, Player player) {
        this.player = player;
        this.width = 100;
        this.height = 20;
        this.margin = 2;

        this.borderPaint = new Paint();
        borderPaint.setColor(Color.BLACK);

        this.outlinePaint = new Paint();
        outlinePaint.setColor(Color.BLACK);
        outlinePaint.setStyle(Paint.Style.STROKE);
        outlinePaint.setStrokeWidth(2);

        this.healthPaint = new Paint();
        healthColor = ContextCompat.getColor(context, R.color.healthBarHealth);
        healthColorFull = ContextCompat.getColor(context, R.color.healthBarFull);
        health3Color = ContextCompat.getColor(context, R.color.healthBar3);
        health2Color = ContextCompat.getColor(context, R.color.healthBar2);
        health1Color = ContextCompat.getColor(context, R.color.healthBar1);
        healthLowColor = ContextCompat.getColor(context, R.color.healthBarLow);
        healthEmptyColor = ContextCompat.getColor(context, R.color.healthBarEmpty);

        healthSeparator = new Paint();
        healthSeparator.setStrokeWidth(3);
        healthSeparator.setColor(Color.BLACK);
        healthSeparator.setAlpha(80);
    }

    public void draw(Canvas canvas, GameDisplay gameDisplay) {
        float x = (float) player.getPositionX();
        float y = (float) player.getPositionY()+180;
        float distanceToPlayer = 30;
        healthPointPercentage = (float) player.getHealthPoint()/player.MAX_HEALTH_POINTS;
        getHealthColor();

        // Draw border
        float borderLeft, borderTop, borderRight, borderBottom;
        borderLeft = x - width/2;
        borderRight = x + width/2;
        borderBottom = y - distanceToPlayer;
        borderTop = borderBottom - height;
        canvas.drawRect(
                (float) gameDisplay.gameToDisplayCoordinatesX(borderLeft),
                (float) gameDisplay.gameToDisplayCoordinatesY(borderTop),
                (float) gameDisplay.gameToDisplayCoordinatesX(borderRight),
                (float) gameDisplay.gameToDisplayCoordinatesY(borderBottom),
                borderPaint);

        // Draw health
        float healthLeft, healthTop, healthRight, healthBottom, healthWidth, healthHeight;
        healthWidth = width - 2*margin;
        healthHeight = height - 2*margin;
        healthLeft = borderLeft + margin;
        healthRight = healthLeft + healthWidth*healthPointPercentage;
        healthBottom = borderBottom - margin;
        healthTop = healthBottom - healthHeight;
        canvas.drawRect(
                (float) gameDisplay.gameToDisplayCoordinatesX(healthLeft),
                (float) gameDisplay.gameToDisplayCoordinatesY(healthTop),
                (float) gameDisplay.gameToDisplayCoordinatesX(healthRight),
                (float) gameDisplay.gameToDisplayCoordinatesY(healthBottom),
                healthPaint);

        double numberOfLines = (double)player.MAX_HEALTH_POINTS/10;
        double lineSeparationLength = healthWidth/numberOfLines;
        double lineX = healthLeft+lineSeparationLength;
        double lineWidth = lineSeparationLength;

        while (lineWidth <= width){
            canvas.drawLine(
                    (float) gameDisplay.gameToDisplayCoordinatesX(lineX),
                    (float) gameDisplay.gameToDisplayCoordinatesY(healthTop),
                    (float) gameDisplay.gameToDisplayCoordinatesX(lineX),
                    (float) gameDisplay.gameToDisplayCoordinatesY(healthBottom),
                    healthSeparator);
            lineX += lineSeparationLength;
            lineWidth += lineSeparationLength;
        }
    }

    public void getHealthColor(){
        if (healthPointPercentage >= 0.5){
            healthPaint.setColor(healthEmptyColor);
        }
        if (healthPointPercentage >= 0.17){
            healthPaint.setColor(healthLowColor);
        }
        if (healthPointPercentage >= 0.33){
            healthPaint.setColor(health1Color);
        }
        if (healthPointPercentage >= 0.5){
            healthPaint.setColor(health2Color);
        }
        if (healthPointPercentage >= 0.67){
            healthPaint.setColor(health3Color);
        }
        if (healthPointPercentage >= 0.83){
            healthPaint.setColor(healthColorFull);
        }
        if (healthPointPercentage >= 1){
            healthPaint.setColor(healthColor);
        }
    }
}
