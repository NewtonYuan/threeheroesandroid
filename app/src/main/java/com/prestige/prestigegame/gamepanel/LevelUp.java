package com.prestige.prestigegame.gamepanel;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.Log;

import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.prestige.prestigegame.R;
import com.prestige.prestigegame.gameobject.Player;
import com.prestige.prestigegame.graphics.DamageAnimator;
import com.prestige.prestigegame.graphics.HealerAnimator;
import com.prestige.prestigegame.graphics.TankAnimator;


/**
 * GameOver is a panel which draws the text Game Over to the screen.
 */
public class LevelUp {
    private Context context;
    private int screenWidth;
    private int screenHeight;
    private Paint backBox;
    private Bitmap initialOptionsBmp;
    private Bitmap optionsBmp;
    private float optionX;
    private float option1Y;
    private float option2Y;
    private float option3Y;
    private float scaleWidth;
    private float scaleHeight;
    private TankAnimator tankAnimator;
    private DamageAnimator damageAnimator;
    private HealerAnimator healerAnimator;
    private Player player;

    public LevelUp(Context context, TankAnimator tankAnimator, DamageAnimator damageAnimator, HealerAnimator healerAnimator, Player player) {
        this.context = context;
        this.tankAnimator = tankAnimator;
        this.damageAnimator = damageAnimator;
        this.healerAnimator = healerAnimator;
        this.player = player;
        screenWidth = context.getResources().getDisplayMetrics().widthPixels;
        screenHeight = context.getResources().getDisplayMetrics().heightPixels;

        backBox = new Paint();
        backBox.setColor(Color.BLACK);
        backBox.setStyle(Paint.Style.FILL_AND_STROKE);
        backBox.setAlpha(120);

        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inScaled = false;
        initialOptionsBmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.options, bitmapOptions);

        int width = initialOptionsBmp.getWidth();
        int height = initialOptionsBmp.getHeight();
        scaleWidth = ((float)(screenWidth/1.54)/width);
        scaleHeight = ((float)(screenHeight/8.325)/height);

        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);

        optionsBmp = Bitmap.createBitmap(initialOptionsBmp, 0, 0, width, height, matrix, false);
        initialOptionsBmp.recycle();
    }

    public void draw(Canvas canvas) {
        Log.i("Scale", String.valueOf(screenHeight));
        final Typeface font = ResourcesCompat.getFont(context, R.font.customfont);
        Log.i("ScreenHeight", String.valueOf(screenWidth));
        Log.i("ScreenHeight", String.valueOf(optionsBmp.getWidth()));

        String gameOverText = "Choose One";
        tankAnimator.getLevelUpText(player.tankLevel);
        damageAnimator.getLevelUpText(player.damageLevel);
        healerAnimator.getLevelUpText(player.healerLevel);
        String tankText = tankAnimator.levelUpMsg;
        String dpsText = damageAnimator.levelUpMsg;
        String healerText = healerAnimator.levelUpMsg;
        String damageHeading = "+DPS";
        String tankHeading = "+Tank";
        String healerHeading = "+Healer";

        int color = ContextCompat.getColor(context, R.color.white);
        int levelColor = ContextCompat.getColor(context, R.color.colorPrimaryDark);

        Paint gameOverPaint = new Paint();
        float textSize = scaleHeight*150;
        gameOverPaint.setColor(color);
        gameOverPaint.setTextSize(textSize);
        gameOverPaint.setTypeface(font);
        gameOverPaint.setTextAlign(Paint.Align.CENTER);

        Paint optionTextPaint = new Paint();
        float levelTextSize = scaleHeight*50;
        optionTextPaint.setColor(levelColor);
        optionTextPaint.setTextSize(levelTextSize);
        optionTextPaint.setTypeface(font);
        optionTextPaint.setTextAlign(Paint.Align.CENTER);

        Paint optionHeadingPaint = new Paint();
        float optionHeadingSize = scaleHeight*120;
        optionHeadingPaint.setColor(levelColor);
        optionHeadingPaint.setTextSize(optionHeadingSize);
        optionHeadingPaint.setTypeface(font);
        optionHeadingPaint.setTextAlign(Paint.Align.CENTER);

        optionX =  screenWidth/2-optionsBmp.getWidth()/2;
        option1Y = screenHeight/4+optionsBmp.getHeight()-scaleHeight*210;
        option2Y = screenHeight/4+optionsBmp.getHeight()*2-scaleHeight*170;
        option3Y = screenHeight/4+optionsBmp.getHeight()*3-scaleHeight*130;

        float tankY =   option2Y+optionsBmp.getHeight()-levelTextSize/2-scaleHeight*50;
        float damageY = option1Y+optionsBmp.getHeight()-levelTextSize/2-scaleHeight*50;
        float healerY = option3Y+optionsBmp.getHeight()-levelTextSize/2-scaleHeight*50;

        canvas.drawRect(screenWidth/7, screenHeight/6, screenWidth/7*6, screenHeight/6*5, backBox);
        canvas.drawText(gameOverText, screenWidth/2, screenHeight/4, gameOverPaint);
        canvas.drawBitmap(optionsBmp, optionX, option1Y, null);
        canvas.drawBitmap(optionsBmp, optionX, option2Y, null);
        canvas.drawBitmap(optionsBmp, optionX, option3Y, null);

        canvas.drawText(healerHeading, optionX+optionsBmp.getWidth()/2, healerY-scaleHeight*80, optionHeadingPaint);
        canvas.drawText(damageHeading, optionX+optionsBmp.getWidth()/2, damageY-scaleHeight*80, optionHeadingPaint);
        canvas.drawText(tankHeading, optionX+optionsBmp.getWidth()/2, tankY-scaleHeight*80, optionHeadingPaint);
        for (String line: tankText.split("\n")) {
            canvas.drawText(line, optionX+optionsBmp.getWidth()/2, tankY, optionTextPaint);
            tankY += 50;
        }
        for (String line: dpsText.split("\n")) {
            canvas.drawText(line, optionX+optionsBmp.getWidth()/2, damageY, optionTextPaint);
            damageY += 50;
        }
        for (String line: healerText.split("\n")) {
            canvas.drawText(line, optionX+optionsBmp.getWidth()/2, healerY, optionTextPaint);
            healerY += 50;
        }
    }

    public boolean optionOneClicked(int touchPositionX, int touchPositionY){
        return (touchPositionX >= optionX && touchPositionX <= optionX+optionsBmp.getWidth() && touchPositionY >= option1Y && touchPositionY <= option1Y+optionsBmp.getHeight());
    }

    public boolean optionTwoClicked(int touchPositionX, int touchPositionY){
        return (touchPositionX >= optionX && touchPositionX <= optionX+optionsBmp.getWidth() && touchPositionY >= option2Y && touchPositionY <= option2Y+optionsBmp.getHeight());
    }

    public boolean optionThreeClicked(int touchPositionX, int touchPositionY){
        return (touchPositionX >= optionX && touchPositionX <= optionX+optionsBmp.getWidth() && touchPositionY >= option3Y && touchPositionY <= option3Y+optionsBmp.getHeight());
    }

    public int getLowestPosition(){
        return ((int)(option3Y+optionsBmp.getHeight()));
    }
}
