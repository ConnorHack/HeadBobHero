package com.example.ben.headbobhero;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.View;
public class GameBoard extends View{
    private Paint p;
    private List<HeadBob> headBobs = null;
    private int starAlpha = 80;
    private int starFade = 2;

    synchronized public void resetStarField() {
        headBobs = null;
    }

    public GameBoard(Context context, AttributeSet aSet) {
        super(context, aSet);
        //it's best not to create any new objects in the on draw
        //initialize them as class variables here
        p = new Paint();
    }

    private enum HeadBobDirection {
        DOWN, LEFT, RIGHT
    }

    private class HeadBob {
        public HeadBobDirection direction;

        public Point point;

        public HeadBob(HeadBobDirection direction, Point point) {
            this.direction = direction;
            this.point = point;
        }
    }

    private void initializeHeadBobs(int maxX, int maxY) {
        headBobs = new ArrayList<HeadBob>();
        for (int i=0; i<3; i++) {
            Random r = new Random();
            int x = i * 100;
            int y = maxY / 2 - 50;
            int directionInt = r.nextInt(3);
            HeadBobDirection direction = null;

            switch (directionInt) {
                case 0:
                    direction = HeadBobDirection.DOWN;
                    break;
                case 1:
                    direction = HeadBobDirection.LEFT;
                    break;
                case 2:
                    direction = HeadBobDirection.RIGHT;
                    break;
            }

            headBobs.add(new HeadBob(direction, new Point(x, y)));
        }
    }

    @Override
    synchronized public void onDraw(Canvas canvas) {
        //create a black canvas
        p.setColor(Color.BLACK);
        p.setAlpha(255);
        p.setStrokeWidth(1);
        canvas.drawRect(0, 0, getWidth(), getHeight(), p);
        //initialize the starfield if needed
        if (headBobs ==null) {
            initializeHeadBobs(canvas.getWidth(), canvas.getHeight());
        }
        //draw the stars

        p.setStrokeWidth(100);
        for (int i=0; i<3; i++) {
            HeadBob bob = headBobs.get(i);
            switch (bob.direction) {
                case DOWN:
                    p.setColor(Color.BLUE);
                    break;
                case LEFT:
                    p.setColor(Color.RED);
                    break;
                case RIGHT:
                    p.setColor(Color.GREEN);
                    break;
            }
            canvas.drawPoint(headBobs.get(i).point.x, headBobs.get(i).point.y, p);
        }
    }
}
