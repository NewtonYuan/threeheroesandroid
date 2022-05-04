package com.prestige.prestigegame.heroes.alfred;

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

import com.prestige.prestigegame.GameDisplay;
import com.prestige.prestigegame.GameLoop;
import com.prestige.prestigegame.R;
import com.prestige.prestigegame.Utils;
import com.prestige.prestigegame.gameobject.Circle;
import com.prestige.prestigegame.gameobject.Player;
import com.prestige.prestigegame.graphics.CharacterList;

import java.util.Timer;
import java.util.TimerTask;

public class TempHP {
    private int screenWidth;
    private int screenHeight;
    private Paint paint;
    private Bitmap tempHPBmp;
    public boolean activated = false;
    private float positionX;
    private float positionY;
    private int alpha = 150;

    public TempHP(Context context) {
        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inScaled = false;
        tempHPBmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.temphp, bitmapOptions);

        screenWidth = context.getResources().getDisplayMetrics().widthPixels;
        screenHeight = context.getResources().getDisplayMetrics().heightPixels;

        paint = new Paint();
        paint.setAlpha(alpha);

        this.positionX = screenWidth/2;
        this.positionY = screenHeight/2;
    }

    public void drawTempHP(Canvas canvas) {
        if (activated){
            canvas.drawBitmap(tempHPBmp,
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
}

