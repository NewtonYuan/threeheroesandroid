package com.prestige.prestigegame.map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

import com.prestige.prestigegame.GameDisplay;
import com.prestige.prestigegame.R;
import com.prestige.prestigegame.gameobject.Player;
import com.prestige.prestigegame.graphics.SpriteSheet;

public class Map {
    public Bitmap mapBmp;
    private int positionX;
    private int positionY;
    private int screenWidth;
    private int screenHeight;

    public Map(Context context) {
        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inScaled = false;
        mapBmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.groundtile2, bitmapOptions);
        screenWidth = context.getResources().getDisplayMetrics().widthPixels;
        screenHeight = context.getResources().getDisplayMetrics().heightPixels;
        positionX = screenWidth/2-750;
        positionY = screenHeight/2-750;

    }

    public void draw(Canvas canvas, Player player){
        canvas.drawBitmap(mapBmp, (int)(positionX-player.getPositionX()), (int)(positionY-player.getPositionY()), null);
        canvas.drawBitmap(mapBmp, (int)(positionX+1500-player.getPositionX()), (int)(positionY+1500-player.getPositionY()), null);
        canvas.drawBitmap(mapBmp, (int)(positionX+1500-player.getPositionX()), (int)(positionY-1500-player.getPositionY()), null);
        canvas.drawBitmap(mapBmp, (int)(positionX-1500-player.getPositionX()), (int)(positionY+1500-player.getPositionY()), null);
        canvas.drawBitmap(mapBmp, (int)(positionX-1500-player.getPositionX()), (int)(positionY-1500-player.getPositionY()), null);
        canvas.drawBitmap(mapBmp, (int)(positionX-1500-player.getPositionX()), (int)(positionY-player.getPositionY()), null);
        canvas.drawBitmap(mapBmp, (int)(positionX+1500-player.getPositionX()), (int)(positionY-player.getPositionY()), null);
        canvas.drawBitmap(mapBmp, (int)(positionX-player.getPositionX()), (int)(positionY+1500-player.getPositionY()), null);
        canvas.drawBitmap(mapBmp, (int)(positionX-player.getPositionX()), (int)(positionY-1500-player.getPositionY()), null);
    }

    public void updateX(Player player){
        positionX = (int) player.getPositionX();
    }

    public void updateY(Player player){
        positionY = (int) player.getPositionY();
    }
}
