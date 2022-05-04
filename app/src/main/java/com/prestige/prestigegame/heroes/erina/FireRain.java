package com.prestige.prestigegame.heroes.erina;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.util.Log;

import androidx.core.content.ContextCompat;

import com.prestige.prestigegame.Game;
import com.prestige.prestigegame.GameDisplay;
import com.prestige.prestigegame.GameLoop;
import com.prestige.prestigegame.R;
import com.prestige.prestigegame.Utils;
import com.prestige.prestigegame.gameobject.Circle;
import com.prestige.prestigegame.gameobject.Player;
import com.prestige.prestigegame.graphics.CharacterList;

import java.util.Timer;
import java.util.TimerTask;

public class FireRain extends Circle {
    private Player player;
    private int screenWidth;
    private int screenHeight;
    public boolean activated = false;
    private float positionX = 0;
    private float positionY = 0;
    private double angle;
    private Bitmap fireRainBmp;
    private int alpha = 100;
    private Paint fireRainPaint;

    public FireRain(Context context, Player player, double positionX, double positionY) {
        super(context, ContextCompat.getColor(context, R.color.coin), positionX, positionY, 100);
        this.player = player;

        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inScaled = false;
        fireRainBmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.firerain, bitmapOptions);

        screenWidth = context.getResources().getDisplayMetrics().widthPixels;
        screenHeight = context.getResources().getDisplayMetrics().heightPixels;

        fireRainPaint = new Paint();
        fireRainPaint.setAlpha(alpha);

        this.positionX = screenWidth/2-200;
        this.positionY = screenHeight/2-200;
    }

    public void drawFireRain(Canvas canvas, GameDisplay gameDisplay){
        if (activated){
            canvas.drawBitmap(fireRainBmp,
                    (float)(positionX-player.positionX),
                    (float)(positionY-player.positionY),
                    fireRainPaint
            );
            fireRainPaint.setAlpha(alpha);
            alpha -= 0.5;
            if (alpha <= 0){
                alpha = 100;
                activated = false;
            }
        }
    }

    public void update() {
        super.positionX = positionX-screenWidth/2+100;
        super.positionY = positionY-screenHeight/2+100;
    }

    public void getRandomPosition(){
        angle = Math.random()*Math.PI*2;
        positionX = (int)(Math.cos(angle)*screenWidth/3+player.positionX-100+screenWidth/2);
        positionY = (int)(Math.sin(angle)*screenWidth/3+player.positionY-100+screenHeight/2);
    }
}

