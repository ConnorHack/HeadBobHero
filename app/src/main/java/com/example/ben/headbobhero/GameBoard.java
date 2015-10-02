package com.example.ben.headbobhero;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;
public class GameBoard extends View{
    private Paint p;
    private Paint textPaint;
    private List<HeadBob> headBobs = null;

    private static Bitmap bm_bob_down = null;
    private static Bitmap bm_bob_left = null;
    private static Bitmap bm_bob_right = null;


    public GameBoard(Context context, AttributeSet aSet) {
        super(context, aSet);
        //it's best not to create any new objects in the on draw
        //initialize them as class variables here
        p = new Paint();
        textPaint = new Paint();

        bm_bob_down = BitmapFactory.decodeResource(getResources(), R.drawable.bob_down);
        bm_bob_left = BitmapFactory.decodeResource(getResources(), R.drawable.bob_left);
        bm_bob_right = BitmapFactory.decodeResource(getResources(), R.drawable.bob_right);
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
        for (int i=0; i<10; i++) {
            Random r = new Random();
            int x = i * -128;
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

        Iterator<HeadBob> headBobIterator = headBobs.iterator();
        while (headBobIterator.hasNext()) {
            HeadBob bob = headBobIterator.next();
            if(bob.point.x > -128 && bob.point.x < getWidth()) {
                switch (bob.direction) {
                    case DOWN:
                        canvas.drawBitmap(bm_bob_down, bob.point.x, bob.point.y, null);
                        break;
                    case LEFT:
                        canvas.drawBitmap(bm_bob_left, bob.point.x, bob.point.y, null);
                        break;
                    case RIGHT:
                        canvas.drawBitmap(bm_bob_right, bob.point.x, bob.point.y, null);
                        break;
                }
            } else if(bob.point.x > getWidth()) {
                headBobIterator.remove();
            }

            bob.point.x+=3;
        }

        if(headBobs.size() == 0) {

            textPaint.setColor(Color.WHITE);
            textPaint.setAlpha(255);
            textPaint.setTextAlign(Paint.Align.CENTER);
            textPaint.setTypeface(Typeface.SANS_SERIF);
            textPaint.setTextSize(48);


            int xPos = (canvas.getWidth() / 2);
            int yPos = (int) ((canvas.getHeight() / 2) - ((textPaint.descent() + textPaint.ascent()) / 2));
            canvas.drawText("Game Over", xPos, yPos, textPaint);
        } else {

            p.setStrokeWidth(10);
            p.setColor(Color.RED);
            canvas.drawLine(getWidth() - 128, 0, getWidth() - 128, getHeight(), p);
        }
    }
}
