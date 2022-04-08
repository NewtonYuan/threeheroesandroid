package com.prestige.prestigegame.graphics;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.prestige.prestigegame.R;

public class CharacterList {
    private Bitmap damageBmp;
    private Bitmap healerBmp;
    private Bitmap tankBmp;

    public CharacterList(Context context) {
        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inScaled = false;
        damageBmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.damage, bitmapOptions);
        healerBmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.healeridle, bitmapOptions);
        tankBmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.tank, bitmapOptions);
    }

    public Bitmap getDamageBmp(){
        return damageBmp;
    }

    public Bitmap getHealerBmp(){
        return healerBmp;
    }

    public Bitmap getTankBmp(){
        return tankBmp;
    }
}
