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
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;
public class GameBoard extends View{
    private Paint p;
    private Paint textPaint;

    boolean hasInitializedBobs = false;
    public static List<HeadBob> headBobs = new ArrayList<HeadBob>();

    private final Bitmap bm_bob_down;
    private final Bitmap bm_bob_left;
    private final Bitmap bm_bob_right;


    public GameBoard(Context context, AttributeSet aSet) {
        super(context, aSet);


        p = new Paint();
        textPaint = new Paint();

        bm_bob_down = BitmapFactory.decodeResource(getResources(), R.drawable.bob_down);
        bm_bob_left = BitmapFactory.decodeResource(getResources(), R.drawable.bob_left);
        bm_bob_right = BitmapFactory.decodeResource(getResources(), R.drawable.bob_right);
    }


    private void initializeHeadBobs() {

        if(headBobs.size() == 0) {
            headBobs = new ArrayList<HeadBob>();
            for (int i = 0; i < 10; i++) {
                Random r = new Random();

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

                headBobs.add(new HeadBob(i * 128, direction));
            }
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
        if (!hasInitializedBobs) {
            initializeHeadBobs();
            hasInitializedBobs = true;
        }
        int bobYPos = canvas.getHeight() / 2 - 50;

        Iterator<HeadBob> headBobIterator = headBobs.iterator();
        while (headBobIterator.hasNext()) {
            HeadBob bob = headBobIterator.next();
            long bobOffset = bob.offset + getWidth();
            if(bobOffset > -128 && bobOffset < getWidth()) {
                switch (bob.direction) {
                    case DOWN:
                        canvas.drawBitmap(bm_bob_down,bobOffset, bobYPos, null);
                        break;
                    case LEFT:
                        canvas.drawBitmap(bm_bob_left, bobOffset, bobYPos, null);
                        break;
                    case RIGHT:
                        canvas.drawBitmap(bm_bob_right, bobOffset, bobYPos, null);
                        break;
                }
            } else if(bobOffset< -128) {
                headBobIterator.remove();
            }

            bob.offset -=4;
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

            textPaint.setColor(Color.WHITE);
            textPaint.setAlpha(255);
            textPaint.setTextAlign(Paint.Align.RIGHT);
            textPaint.setTypeface(Typeface.SANS_SERIF);
            textPaint.setTextSize(18);

            canvas.drawText("Bobs Left: " + headBobs.size(), getWidth() - 20, 20, textPaint);

            p.setStrokeWidth(10);
            p.setColor(Color.RED);
            canvas.drawLine(128, 0, 128, getHeight(), p);
        }
    }
}
