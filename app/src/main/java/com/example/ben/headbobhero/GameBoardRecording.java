package com.example.ben.headbobhero;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.CountDownTimer;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Connor on 10/18/2015.
 */
public class GameBoardRecording extends View {
    private Paint p;
    private Paint textPaint;

    public static List<HeadBob> headBobs = new ArrayList<HeadBob>();
    public static List<HeadBob> recordedHeadBobs = new ArrayList<HeadBob>();

    private Boolean hasSongEnded = false ;

    private final static String DEBUG = "GameBoard";

    private final Bitmap bm_bob_down;
    private final Bitmap bm_bob_left;
    private final Bitmap bm_bob_right;

    private boolean startedRecording = false;
    private boolean isSongPaused = false ;
    private CountDownTimer countdownToStart = null;
    private int secondsLeftBeforeStart = 5;

    public GameBoardRecording(Context context, AttributeSet aSet) {
        super(context, aSet);

        p = new Paint();
        textPaint = new Paint();

        bm_bob_down = BitmapFactory.decodeResource(getResources(), R.drawable.bob_down);
        bm_bob_left = BitmapFactory.decodeResource(getResources(), R.drawable.bob_left);
        bm_bob_right = BitmapFactory.decodeResource(getResources(), R.drawable.bob_right);
    }

    /**
     * <h2>Add a new headbob into both instances</h2>
     * @param headBob - A Headbob instance
     */
    public void addHeadBob(HeadBob headBob) {

        recordedHeadBobs.add(headBob);

        // 64 because of image's width
        headBobs.add(new HeadBob(getWidth() / 2 - (64), headBob.direction));
    }

    /**
     * <h2>Indicate that the song has ended</h2>
     */
    public void endSong() {
        hasSongEnded = true ;
    }

    @Override
    synchronized public void onDraw(Canvas canvas) {
        //create a black canvas
        p.setColor(Color.BLACK);
        p.setAlpha(255);
        p.setStrokeWidth(1);
        canvas.drawRect(0, 0, getWidth(), getHeight(), p);

        if (!startedRecording) {

            if (countdownToStart == null) {
                //TODO Put a black box here with an opacity of a certain degree
                //      This will hide the background items while displaying the ready text
                countdownToStart = new CountDownTimer(5000, 500) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        secondsLeftBeforeStart = (int) Math.ceil(millisUntilFinished / 1000) + 1;
                    }

                    @Override
                    public void onFinish() {
                        secondsLeftBeforeStart = 1;
                        startedRecording = true;
                        isSongPaused = false ;
                    }
                };
                countdownToStart.start();
            } else {
                textPaint.setColor(Color.WHITE);
                textPaint.setAlpha(255);
                textPaint.setTextAlign(Paint.Align.CENTER);
                textPaint.setTypeface(Typeface.SANS_SERIF);
                textPaint.setTextSize(48);


                int xPos = (canvas.getWidth() / 2);
                int yPos = (int) ((canvas.getHeight() / 2) - ((textPaint.descent() + textPaint.ascent()) / 2));
                canvas.drawText("Ready... " + Integer.toString(secondsLeftBeforeStart), xPos, yPos, textPaint);
            }
            return;
        }

        if (hasSongEnded) {
            // Game over
            textPaint.setColor(Color.WHITE);
            textPaint.setAlpha(255);
            textPaint.setTextAlign(Paint.Align.CENTER);
            textPaint.setTypeface(Typeface.SANS_SERIF);
            textPaint.setTextSize(34);

            int xPos = (canvas.getWidth() / 2);
            int yPos = (int) ((canvas.getHeight() / 2) - ((textPaint.descent() + textPaint.ascent()) / 2));

            canvas.drawText("Recording completed!", xPos, yPos, textPaint);
        } else {
            // Song hasn't ended!
            Iterator<HeadBob> headBobIterator = headBobs.iterator();
            int bobYPos = canvas.getHeight() / 2 - 50;

            while (headBobIterator.hasNext()) {
                HeadBob bob = headBobIterator.next();
                long bobOffset = bob.offset;

                if (bobOffset > -128 && bobOffset < getWidth()) {

                    switch (bob.direction) {
                        case DOWN:
                            canvas.drawBitmap(bm_bob_down, bobOffset, bobYPos, null);
                            break;
                        case LEFT:
                            canvas.drawBitmap(bm_bob_left, bobOffset, bobYPos, null);
                            break;
                        case RIGHT:
                            canvas.drawBitmap(bm_bob_right, bobOffset, bobYPos, null);
                            break;
                    }
                }

                if (bobOffset < -128) {
                    headBobIterator.remove();
                }

                if (!isSongPaused) {
                    bob.offset -= 4;
                }
            }

            //TODO Wait for the handle
                p.setStrokeWidth(10);
                p.setColor(Color.RED);
                canvas.drawLine(getWidth()/2, 0, getWidth()/2, getHeight(), p);
        }
    }

    public void resetStartedRecording() {
        startedRecording = false ;
        countdownToStart = null ;
    }

    public boolean isRecordingFinished() {
        return hasSongEnded ;
    }

    public void pauseSong() {
        isSongPaused = true ;
    }

    public boolean isSongPaused() {
        return isSongPaused ;
    }
}
