package com.prestige.prestigegame.gameobject;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.prestige.prestigegame.GameDisplay;
import com.prestige.prestigegame.graphics.CharacterList;

/**
 * Circle is an abstract class which implements a draw method from GameObject for drawing the object
 * as a circle.
 */
public abstract class Circle extends GameObject {
    private CharacterList characterList;
    protected double radius;
    protected Paint paint;
    private Paint shadow;

    public Circle(Context context, int color, double positionX, double positionY, double radius) {
        super(positionX, positionY);
        this.radius = radius;

        // Set colors of circle
        paint = new Paint();
        paint.setColor(color);

        characterList = new CharacterList(context);

        shadow = new Paint();
        shadow.setColor(Color.BLACK);
        shadow.setStyle(Paint.Style.FILL_AND_STROKE);
        shadow.setAlpha(70);
    }

    /**
     * isColliding checks if two circle objects are colliding, based on their positions and radii.
     * @param obj1
     * @param obj2
     * @return
     */
    public static boolean isColliding(Circle obj1, Circle obj2) {
        double distance = getDistanceBetweenObjects(obj1, obj2);
        double distanceToCollision = obj1.getRadius() + obj2.getRadius();
        if (distance < distanceToCollision)
            return true;
        else
            return false;
    }

    public void draw(Canvas canvas, GameDisplay gameDisplay) {
        canvas.drawCircle(
                (float) gameDisplay.gameToDisplayCoordinatesX(positionX),
                (float) gameDisplay.gameToDisplayCoordinatesY(positionY),
                (float) radius,
                paint
        );
    }

    public double getRadius() {
        return radius;
    }
}