package com.prestige.prestigegame.gamepanel;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Typeface;

import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.prestige.prestigegame.R;


/**
 * GameOver is a panel which draws the text Game Over to the screen.
 */
public class GameOver {
    private Context context;
    private int screenWidth;
    private int screenHeight;
    private Paint backBox;
    private Bitmap initialReturnHomeButton;
    private Bitmap initialReturnHomeButton1;
    private Bitmap returnHomeButton;
    private Bitmap returnHomeButton1;
    private float returnHomeButtonX;
    private float returnHomeButtonY;
    private float scale;
    private float scaleW;
    public boolean adShown = false;

    public GameOver(Context context) {
        this.context = context;
        screenWidth = context.getResources().getDisplayMetrics().widthPixels;
        screenHeight = context.getResources().getDisplayMetrics().heightPixels;

        backBox = new Paint();
        backBox.setColor(Color.BLACK);
        backBox.setStyle(Paint.Style.FILL_AND_STROKE);
        backBox.setAlpha(120);

        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inScaled = false;
        initialReturnHomeButton = BitmapFactory.decodeResource(context.getResources(), R.drawable.adrevive, bitmapOptions);
        initialReturnHomeButton1 = BitmapFactory.decodeResource(context.getResources(), R.drawable.returnhome, bitmapOptions);

        int width = initialReturnHomeButton.getWidth();
        int height = initialReturnHomeButton.getHeight();
        scaleW = ((float)(screenWidth/4.3)/width);
        scale = ((float)(screenHeight/15.54)/height);

        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);

        returnHomeButton = Bitmap.createBitmap(initialReturnHomeButton, 0, 0, width, height, matrix, false);
        returnHomeButton1 = Bitmap.createBitmap(initialReturnHomeButton1, 0, 0, width, height, matrix, false);
    }

    public void draw(Canvas canvas) {
        final Typeface font = ResourcesCompat.getFont(context, R.font.customfont);

        String gameOverText = "Game Over";
        String playAgainText = "Watch Ad to Revive?";
        String GGText = "Return Home?";
        String noThanksText = "No Thanks.";

        Paint gameOverPaint = new Paint();
        int color = ContextCompat.getColor(context, R.color.white);
        gameOverPaint.setColor(color);
        float textSize = scale*100;
        gameOverPaint.setTextSize(textSize);
        gameOverPaint.setTextAlign(Paint.Align.CENTER);
        gameOverPaint.setTypeface(font);

        Paint optionTextPaint = new Paint();
        float levelTextSize = scale*70;
        optionTextPaint.setColor(color);
        optionTextPaint.setTextSize(levelTextSize);
        optionTextPaint.setTypeface(font);
        optionTextPaint.setTextAlign(Paint.Align.CENTER);

        returnHomeButtonX = screenWidth/2-returnHomeButton.getWidth()/2;
        returnHomeButtonY = screenHeight/3*2+returnHomeButton.getHeight()/3;

        canvas.drawRect(screenWidth/7, screenHeight/6, screenWidth/7*6, screenHeight/6*5, backBox);
        canvas.drawText(gameOverText, screenWidth/2, screenHeight/4, gameOverPaint);
        if (!adShown){
            canvas.drawBitmap(returnHomeButton, returnHomeButtonX, returnHomeButtonY, null);
            canvas.drawText(noThanksText, screenWidth/2, returnHomeButtonY+returnHomeButton.getHeight()+scale*80, optionTextPaint);
            canvas.drawText(playAgainText, screenWidth/2, screenHeight/3*2, gameOverPaint);
        } else {
            canvas.drawBitmap(returnHomeButton1, returnHomeButtonX, returnHomeButtonY, null);
            canvas.drawText(GGText, screenWidth/2, screenHeight/3*2, gameOverPaint);
        }
    }

    public boolean returnHomeButtonClicked(int touchPositionX, int touchPositionY){
        return (touchPositionX >= returnHomeButtonX && touchPositionX <= returnHomeButtonX+returnHomeButton.getWidth() && touchPositionY >= returnHomeButtonY && touchPositionY <= returnHomeButtonY+returnHomeButton.getHeight());
    }

    public boolean noThanksButtonClicked(int touchPositionX, int touchPositionY){
        return (touchPositionX >= returnHomeButtonX && touchPositionX <= returnHomeButtonX+returnHomeButton.getWidth() && touchPositionY >= returnHomeButtonY+returnHomeButton.getHeight()+scale*10 && touchPositionY <= returnHomeButtonY+returnHomeButton.getHeight()+scale*80);
    }
}
